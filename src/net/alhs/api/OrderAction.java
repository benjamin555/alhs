package net.alhs.api;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;

import net.alhs.common.FangTai;
import net.alhs.common.FangTaiObject;
import net.sysmain.common.ConnectionManager;
import net.sysmain.common.EngineTools;
import net.sysmain.util.GUID;
import net.sysplat.access.Parameters;
/**
 * 订单接口类
 * @author Administrator
 *
 */
public class OrderAction extends TimerTask
{

	/**
	 * 配置文件
	 */
	private static String xml = "homtrip.xml";

	private static SimpleDateFormat pDate = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 一天的毫秒数
	 */
	private static long ONE_DAY = 1000 * 60 * 60 * 24;
	
	/**
	 * 抓取web的订单数据，存储OA表中
	 * @throws Exception
	 */
	private static String ADD = "add";
	
	/**
	 * 返回对应的房屋guid
	 * @param startDate
	 * @param endDate
	 * @param conn
	 * @param fwmbGuid
	 * @return
	 */
	private static String getHouseGuid(String startDate, String endDate, Connection conn, String fwmbGuid)  throws Exception
	{
		
		String fxguid = null;
		String sql = "select fxguid,fwzt from house where guid='" + fwmbGuid+ "'";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if(rs.next())
		{
			if("1".equals(rs.getString("fwzt")))
			{
				/**
				 * 如果是业主房(fwzt=1)，说明客户是按具体房下单的
				 */
				return fwmbGuid;
			}
			
			/**
			 * 返回房型的guid
			 */
			fxguid = rs.getString("fxguid");
		}
		if(fxguid == null) return null;
		
		/**
		 * 取房型下的这个时间段的所有有效订单
		 */
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("select a.houseGuid from fwddzb a,house b where a.houseguid=b.guid and b.fxguid='").append(fxguid).append("'")
		       .append(" and a.status>='4' and a.status<='6' and (a.zcqx is null or a.zcqx='0') and (a.noshow is null or a.noshow ='0')");
		
		
		sqlBuf.append(" and (");
		
		/**
		 * 开始和结束时间 被订单入住时间、退房时间包含
		 *    |---|  开始和结束时间
		 * |________| 入住时间、退房时间
		 * 
		 */
		sqlBuf.append("(a.rzrq<='").append(startDate).append("' and a.tfrq>='").append(endDate).append("')");
		
		/**
		 * 开始和结束时间 和订单入住时间、退房时间有交集
		 *    |-----|  开始和结束时间
		 * |_____| 入住时间、退房时间
		 * 
		 *    |-----|  开始和结束时间
		 *       |_____| 入住时间、退房时间
		 * 
		 *  |---------|  开始和结束时间
		 *    |_____| 入住时间、退房时间
		 */
		sqlBuf.append(" or (a.rzrq<='").append(startDate).append("' and a.tfrq>='").append(startDate).append("')");
		sqlBuf.append(" or (a.rzrq<='").append(endDate).append("' and a.tfrq>='").append(endDate).append("')");
		

		/**
		 *  订单入住时间、退房时间 在开始和结束时间之间
		 *    |--------|  开始和结束时间
		 *     |_____| 入住时间、退房时间
		 * 
		 */
		sqlBuf.append(" or (a.rzrq>='").append(startDate).append("' and a.tfrq<='").append(endDate).append("')");
		
		sqlBuf.append(")");
		
		HashMap orders = new HashMap();
		ResultSet rsOrder = conn.createStatement().executeQuery(sqlBuf.toString());
		while(rsOrder.next())
		{
			orders.put(rsOrder.getString("houseGuid"), "1");
		}
		
		/**
		 * 该处可设置房屋的随机排序规则
		 */
		sql = "Select guid from house where fxguid='" + fxguid + "' and fwzt='1'";
		ResultSet rsHouse = conn.createStatement().executeQuery(sql);
		while(rsHouse.next())
		{
			String guid = rsHouse.getString("guid");
			if(orders.get(guid) == null)
			{
				return guid;
			}
		}
		
		return null;
	}
	
	private static double getTotalPrice(String rzrq, String tfrq, HashMap map) throws Exception
	{
		if(map == null) return 0;
		FangTaiObject ftObject = null;
		double total = 0d;
		
		/**
		 * 起始日期
		 */
		ftObject = (FangTaiObject)map.get(rzrq);
		if(ftObject != null)
		{
			total = total + Double.parseDouble(ftObject.getPrice());
		}
		
		/**
		 * 循环取每天的房价累加
		 */
		Date t1 = pDate.parse(rzrq);
		Date t2 = pDate.parse(tfrq);
		Calendar cal = Calendar.getInstance();
		cal.setTime(t1);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		//System.out.println(pDate.format(cal.getTime()));
		while(cal.getTime().before(t2))
		{
			String dt = pDate.format(cal.getTime());
			ftObject = (FangTaiObject)map.get(dt);
			if(ftObject != null)
			{
				total = total + Double.parseDouble(ftObject.getPrice());
			}
			cal.add(Calendar.DAY_OF_MONTH, 1);
			//System.out.println(pDate.format(cal.getTime()));
		}
		
		/**
		 * 结束日期
		 */
//		ftObject = (FangTaiObject)map.get(tfrq);
//		if(ftObject != null)
//		{
//			total = total + Double.parseDouble(ftObject.getPrice());
//		}
		
		return total;
	}
	
	public static void addOrderFromWeb()
	{
		/**
		 * 默认和网站对接
		 */
		if("1".equals(Parameters.getCommonParameter("网站不对接"))) return;
		
		Connection webConn = null;
		Connection conn = null; 
		PreparedStatement ps = null;
		ResultSet rs = null;

		synchronized(ADD)
		{
			try
			{
				webConn  = ConnectionManager.getInstance().getConnection(xml);
				conn = ConnectionManager.getInstance().getConnection();
				
				//
				/**
				 * 从本地得到网站订单的最大itemId,初始为0
				 * 
				 */
				long maxItemId = getMaxItemId(conn, "100");
				long maxItemId0 = maxItemId;
				
				/**
				 * 根据最大itemId 扫描网站订单
				 *  暂时只需一个
				 */
				String sql = "select o.*,m.guid from ht_mall_order o,ht_mall m where o.mallid=m.itemid and o.itemid > ? order by o.itemid";
				//String sql = "select o.*,m.guid from ht_mall_order o,ht_mall m where o.mallid=m.itemid and o.itemId=62 and o.itemId<>? order by o.itemid";
				//订单添加SQL
				String sql0 = "insert into fwddzb(guid,houseGuid,sqsj,sqdh,ddsx,ddzt,rzrq,tfrq,rzts,zje,ffxj,qtfy,xm,bz,itemid,lxdh,ptdh,sqbmguid,sqbm,rzsjqj," +
						                          "status,gzlzt,zdqxsj_xs,zdqxsj_fz,yk,ffyj,jff)" +
				              "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'5','2',12,59,?,?,?)";

				ps = webConn.prepareStatement(sql);
				ps.setLong(1, maxItemId);
				rs = ps.executeQuery();
				while(rs.next())
				{
					int orderId = rs.getInt("itemid");                     //网站订单主键
					int number = rs.getInt("number");                      //订单数房的套数
					
					/**
					 * 考虑到有多套房
					 */
					for(int xx=0; xx<number; xx++)
					{
						String fwmbGuid = rs.getString("guid");               //模板房间guid
						
						/**
						 * 订单时间
						 */
						long _sqsj = rs.getLong("addtime") * 1000;                    //添加时间
						Timestamp sqsj = new Timestamp(_sqsj);
						
						String ddsx = "HT";                                        //订单属性(家游网)
						/**
						 * 网站过来的订单号，格式
						 * HT201407040002
						 */
						EngineTools eTools = new EngineTools();
						String sqdh = ddsx + eTools.dateSerial(conn, 40, 4);                           
						
						String ddzt = "0";                                         //订单状态（默认0：待处理）
						String rzrq = rs.getString("fromdate");                    //入住日期
						String tfrq = rs.getString("todate");                      //退房日期
						if(rzrq == null || tfrq == null)
						{
							//暂时先忽略这个错误
							continue;
						}
						
						/**
						 * 房屋的guid，随机分配
						 * 
						 */
						String houseGuid = getHouseGuid(rzrq, tfrq, conn, fwmbGuid);
						if(houseGuid == null)
						{
							/**
							 *  没有符合条件的房屋，取房型模板的Id
							 */
							houseGuid = fwmbGuid;
						}
						
						String bmGuid = null;
						String bmName = null;					
						String s = "select b.sqbmguid,b.sqbm from house a,qtjzx b where a.guid='" + fwmbGuid + "' and a.qtGuid=b.guid";
						ResultSet rsBm = conn.createStatement().executeQuery(s);
						if(rsBm.next())
						{
							bmGuid = rsBm.getString("sqbmguid");
							bmName = rsBm.getString("sqbm");
						}
						
						//入住天数o.send_days
						int rzts = (int)((pDate.parse(tfrq).getTime() - pDate.parse(rzrq).getTime())/ONE_DAY);       
						
						double qtfy = rs.getDouble("o.fee");                        //其他费用
						String xm = rs.getString("o.buyer_name");                   //顾客姓名
						String bz = rs.getString("o.note");
						String mobile = rs.getString("buyer_mobile");               //顾客联系电话
						
						ps = conn.prepareStatement(sql0);
						/**
						 * 订单系统主键
						 */
						int index = 1;
						String ddGuid = new GUID().toString();
						ps.setString(index++, ddGuid);
						
						/**
						 * 依据模板房的guid，返回这段时间的空缺房
						 *   to do
						 */
						ps.setString(index++, houseGuid);
						ps.setTimestamp(index++, sqsj);
						ps.setString(index++, sqdh);
						ps.setString(index++, ddsx); 
						ps.setString(index++, ddzt); 
						ps.setString(index++, rzrq); 
						ps.setString(index++, tfrq); 
						ps.setInt(index++, rzts); 
						
						/**
						 * 价格有问题，需要从OA系统中获取
						 */
						FangTai ft = new FangTai(conn);
						HashMap map = ft.getDayOfPrice(houseGuid, rzrq, tfrq);
						FangTaiObject ftObject = null;
						
						if(map != null) ftObject = (FangTaiObject)map.get(rzrq);
						
						/**
						 * 起始日的房价
						 */
						double price = (ftObject != null)?Double.parseDouble(ftObject.getPrice()):0;
						double zje = 0d;;
						//ps.setDouble(index++, price);
						
						/**
						 * 计算总房价
						 */
						if(rzts <= 1)
						{
							zje = price;
						}
						else
						{
							zje = getTotalPrice(rzrq, tfrq, map);
						}
						
						ps.setDouble(index++, zje);   //房费总计 
						ps.setDouble(index++, zje);   //应收款
						ps.setDouble(index++, qtfy); 
						ps.setString(index++, xm);
						ps.setString(index++, bz);
						ps.setInt(index++, orderId);
						ps.setString(index++, mobile);				
						
						//平台单号
						ps.setString(index++, orderId + "");
						
						/**
						 * 部门名称和部门guid
						 */
						ps.setString(index++, bmGuid);
						ps.setString(index++, bmName);
						
						/**
						 * 入住的区间
						 * 2014-07-04,2014-07-04,1;
						 */
						Calendar cal = Calendar.getInstance();
						cal.setTime(pDate.parse(tfrq));
						cal.add(Calendar.DAY_OF_MONTH, -1);
						String qujian = rzrq + "," + pDate.format(cal.getTime()) + "," + rzts + ";";
						ps.setString(index++, qujian);
						ps.setDouble(index++, zje);   //余款
						ps.setDouble(index++, zje);   //房费原价
						ps.setDouble(index++, zje);   //净房费
						
						ps.executeUpdate();
						
						if(orderId > maxItemId)
						{
							maxItemId = orderId;
						}
						
						/**
						 * 添加订单的房价信息
						 */
						addFjxx(conn, ddGuid, new String[]{rzrq, tfrq}, houseGuid);
						
						/**
						 * 添加订单的入住信息
						 */
						addRzxx(conn, ddGuid, rzrq, tfrq);
						
						/**
						 * 同步网站日历、价格、可订数的数据
						 */
						net.alhs.api.CalendarToWebThread.addOrder(ddGuid);
					}
				}
				
				/**
				 * 保存网站订单中最大itemId
				 */
				if(maxItemId > maxItemId0) saveMaxItemId(conn, maxItemId, "100");
				
				
				/**
				 * 
				 * 扫描添加订单的收款信息
				 * 
				 */
				addSkxx(conn, webConn); 
			}
			
			catch (Exception e)
            {
    			e.printStackTrace();
    		}
            
			finally
            {
    			ConnectionManager.close(conn);
    			ConnectionManager.close(webConn);
    		}
			
		}
	}
	
	/**
	 * 添加订单的房价信息
	 * @param conn
	 * @param ddGuid    订单主键guid
	 * @param date 开始日期 结束日期
	 * @param fj        房价
	 * @param ts        连续入住的天数
	 * @throws Exception
	 */
	private static void addFjxx(Connection conn, String ddGuid, String[] date, String houseGuid) throws Exception
	{
		String sql = "insert into ddfjxx(guid,zbGuid,rq,yfj,qrfj,bz,ts)values(?,?,?,?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		String currentPrice = "0";
		String startDate = date[0];
		FangTai ft = new FangTai(conn);
		HashMap map = ft.getDayOfPrice(houseGuid, date[0], date[1]);
		int ts = 0;
		
		FangTaiObject ft0 = null;
		if(map != null)
		{
			//获取初始价格
			ft0 = (FangTaiObject)map.get(date[0]);
			if(ft0 != null) currentPrice = ft0.getPrice();
		}
		
		Date t1 = pDate.parse(date[0]);
		Date t2 = pDate.parse(date[1]);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(t1);
		while(cal.getTime().before(t2))
		{
			String dt = pDate.format(cal.getTime());
			FangTaiObject ftObject = null;
			if(map != null)  ftObject = (FangTaiObject)map.get(dt);
			
			/**
			 * 遇到不相同的价格
			 *  没有价格，直接忽略
			 */
			if(ftObject != null && !currentPrice.equals(ftObject.getPrice()))
			{
				ps.setString(1, new GUID().toString());
				ps.setString(2, ddGuid);
				ps.setString(3, startDate);    //具体的日期			
				/**
				 * 起始日的房价
				 */
				ps.setDouble(4, (currentPrice != null)?Double.parseDouble(currentPrice):0);				
				ps.setDouble(5, (currentPrice != null)?Double.parseDouble(currentPrice):0);
				ps.setString(6, null);   //备注
				ps.setInt(7, ts);
				
				ps.execute();
				ts = 0;
				startDate = dt;
				currentPrice = ftObject.getPrice();
			}
			cal.add(Calendar.DAY_OF_MONTH, 1);
			ts++;
		}
		
		ps.setString(1, new GUID().toString());
		ps.setString(2, ddGuid);
		ps.setString(3, startDate);    //具体的日期			
		/**
		 * 起始日的房价
		 */
		ps.setDouble(4, (currentPrice != null)?Double.parseDouble(currentPrice):0);				
		ps.setDouble(5, (currentPrice != null)?Double.parseDouble(currentPrice):0);
		ps.setString(6, null);   //备注
		ps.setInt(7, ts);
		
		ps.execute();
	}
	
	/**
	 * 添加订单的入住信息
	 * @param conn   
	 * @param ddGuid  订单主键guid
	 * @param rdrq    入住时间
	 * @param tfrq    退房日期
	 * @throws Exception
	 */
	private static void addRzxx(Connection conn, String ddGuid, String rdrq, String tfrq) throws Exception
	{
		/**
		 * 添加入住时间区间
		 */
		String sql = "insert into ddrzqj(guid,zbGuid,rzrq,tfrq) values(?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, new GUID().toString());
		ps.setString(2, ddGuid);
		ps.setString(3, rdrq);
		ps.setString(4, tfrq);
		
		ps.execute();
	}
	
	/**
	 * 扫描网站的收款记录，添加订单的收款信息
	 * @param conn
	 * @param webConn
	 * @param ddGuid
	 * @param orderId
	 * @throws Exception
	 */
	private static void addSkxx(Connection conn, Connection webConn) throws Exception
	{
		long maxItemId0 = getMaxItemId(conn, "101");
		long maxItemId = maxItemId0;
		
		/**
		 * status 2已支付 3 支付成功
		 */
		String sql0 = "Select mall_order_Id,trade_no,amount,sendtime,receivetime from ht_finance_charge where receivetime>" + maxItemId + " and status>=2 order by receivetime";		
		ResultSet rs = webConn.createStatement().executeQuery(sql0);
		
		String sql = "insert into fwddsk(guid,zbGuid,sklx,skje,sksj,xm,lsh) values(?,?,?,?,?,?,?)";
		PreparedStatement ps = null;		
		
		while(rs.next())
		{
			double total = 0d;
			ArrayList guids = new ArrayList();
			int orderId = rs.getInt("mall_order_Id");
			long receivetime = rs.getLong("receivetime");
			maxItemId = receivetime;
			String sqlOrder = "select guid from fwddzb where itemid=" + orderId;
			
			ResultSet rsOrder = conn.createStatement().executeQuery(sqlOrder);
			while(rsOrder.next())
			{
				String ddGuid = rsOrder.getString("guid");
				if(ddGuid != null) guids.add(ddGuid);
			}

			for(int mm=0; mm<guids.size(); mm++)
			{
				String ddGuid = (String)guids.get(mm);
				double yisk = 0d;
				
				/**
				 * 没有对应的订单，忽略该条记录
				 */
				if(ddGuid == null) continue;
				
				if(ps == null)  ps = conn.prepareStatement(sql);
				
				yisk = rs.getDouble("amount")/guids.size();
				
				ps.setString(1, new GUID().toString());
				ps.setString(2, ddGuid);
				ps.setString(3, "艾丽豪斯中信");
				total += yisk;
				ps.setDouble(4, yisk);
				ps.setTimestamp(5, new Timestamp(rs.getLong("sendtime") * 1000));
				ps.setString(6, "家游网");
				String trade_no = rs.getString("trade_no");
				if(guids.size() > 1) trade_no = trade_no + "_" + (mm+1);
				ps.setString(7, trade_no);
				
				ps.execute();
				
				/**
				 * 如果是已经收款，修改相应状态
				 */
				sql = "update fwddzb set ysk=ifnull(ysk,0) + ?,status='4',yk=ifnull(zje,0)-ifnull(ysk,0)-?,zdqxsj_xs=null,zdqxsj_xs=null where guid=?";
				PreparedStatement ps1 = conn.prepareStatement(sql);
				ps1.setDouble(1, yisk);
				ps1.setDouble(2, yisk);
				ps1.setString(3, ddGuid);
				ps1.execute();
			}
		}
		if(maxItemId > maxItemId0) saveMaxItemId(conn, maxItemId, "101");
	}
	
	/**
	 * 修改OA订单信息，同步web.
	 * @throws Exception
	 */
	public static void updateOrderToWeb(OrderBean order) throws Exception{
		Connection conn = ConnectionManager.getInstance().getConnection(xml);
		PreparedStatement ps = null;
		ResultSet rs= null;

		String sql = "update ht_mall_order set mallid=?,seller=?,price=?,amount=?,fee=?,updatetime=?,note=?,status=? where itemid=?";

		String sql0 = "select itemid from ht_mall where guid=?";

		if(order != null){
			/**
			 * 根据oa中houseGuid从web的房屋表中查出itemid
			 */
			int itemid = 0;                    //web房屋主键
			ps = conn.prepareStatement(sql0);
			ps.setString(1, order.getHouseGuid());
			rs = ps.executeQuery();
			if(rs.next()){
				itemid = rs.getInt("itemid");
			}
			else{
				throw new Exception("房屋"+order.getHouseGuid()+"未同步!");
			}

			ps = conn.prepareStatement(sql);
			ps.setInt(1, itemid);
			ps.setString(2, order.getSeller());
			ps.setDouble(3, order.getPrice());
			ps.setDouble(4, order.getAmount());
			ps.setDouble(5, order.getFee());
			ps.setInt(6, Common.getTimeStamp(order.getUpdatetime()));
			ps.setString(7, order.getNote());
			ps.setInt(8, order.getStatus());
			ps.setInt(9, order.getItemid());
		}
	}
	
	/**
	 * 从sequence_sys表读取网站订单的最大itemId
	 * @return 
	 * @throws Exception
	 */
	private static long getMaxItemId(Connection conn, String typeId)throws Exception
	{
		long maxItemId = 0;
		String sql = "select SerialNumber from sequence_sys where Id=" + typeId;
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if(rs.next())
		{
			maxItemId = Long.parseLong(rs.getString(1));
		}
		
		return maxItemId;
		
	}
	
	/**
	 * 保存网站订单中最大itemId
	 * @param id
	 * @throws Exception
	 */
	private static void saveMaxItemId(Connection conn, long id, String typeId)throws Exception
	{
		String sql = "update sequence_sys set SerialNumber='"+id+"' where Id=" + typeId;
		conn.createStatement().executeUpdate(sql);
	}

	/**
	 * OA状态改变时，同步web订单状态
	 * @param order
	 * @throws Exception
	 */
	public static void updateWebOrderStatus(OrderBean order)throws Exception
	{
		Connection conn = ConnectionManager.getInstance().getConnection(xml);
		PreparedStatement ps = null;
		String sql = "update ht_mall_order set status=? where itemid=?";
		if(order != null){
			ps = conn.prepareStatement(sql);
			ps.setInt(1, order.getStatus());
			ps.setInt(2, order.getItemid());
		}
	}

	public void run() 
	{
		addOrderFromWeb();
	}
}
