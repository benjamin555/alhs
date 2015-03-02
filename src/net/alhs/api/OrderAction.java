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
 * �����ӿ���
 * @author Administrator
 *
 */
public class OrderAction extends TimerTask
{

	/**
	 * �����ļ�
	 */
	private static String xml = "homtrip.xml";

	private static SimpleDateFormat pDate = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * һ��ĺ�����
	 */
	private static long ONE_DAY = 1000 * 60 * 60 * 24;
	
	/**
	 * ץȡweb�Ķ������ݣ��洢OA����
	 * @throws Exception
	 */
	private static String ADD = "add";
	
	/**
	 * ���ض�Ӧ�ķ���guid
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
				 * �����ҵ����(fwzt=1)��˵���ͻ��ǰ����巿�µ���
				 */
				return fwmbGuid;
			}
			
			/**
			 * ���ط��͵�guid
			 */
			fxguid = rs.getString("fxguid");
		}
		if(fxguid == null) return null;
		
		/**
		 * ȡ�����µ����ʱ��ε�������Ч����
		 */
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("select a.houseGuid from fwddzb a,house b where a.houseguid=b.guid and b.fxguid='").append(fxguid).append("'")
		       .append(" and a.status>='4' and a.status<='6' and (a.zcqx is null or a.zcqx='0') and (a.noshow is null or a.noshow ='0')");
		
		
		sqlBuf.append(" and (");
		
		/**
		 * ��ʼ�ͽ���ʱ�� ��������סʱ�䡢�˷�ʱ�����
		 *    |---|  ��ʼ�ͽ���ʱ��
		 * |________| ��סʱ�䡢�˷�ʱ��
		 * 
		 */
		sqlBuf.append("(a.rzrq<='").append(startDate).append("' and a.tfrq>='").append(endDate).append("')");
		
		/**
		 * ��ʼ�ͽ���ʱ�� �Ͷ�����סʱ�䡢�˷�ʱ���н���
		 *    |-----|  ��ʼ�ͽ���ʱ��
		 * |_____| ��סʱ�䡢�˷�ʱ��
		 * 
		 *    |-----|  ��ʼ�ͽ���ʱ��
		 *       |_____| ��סʱ�䡢�˷�ʱ��
		 * 
		 *  |---------|  ��ʼ�ͽ���ʱ��
		 *    |_____| ��סʱ�䡢�˷�ʱ��
		 */
		sqlBuf.append(" or (a.rzrq<='").append(startDate).append("' and a.tfrq>='").append(startDate).append("')");
		sqlBuf.append(" or (a.rzrq<='").append(endDate).append("' and a.tfrq>='").append(endDate).append("')");
		

		/**
		 *  ������סʱ�䡢�˷�ʱ�� �ڿ�ʼ�ͽ���ʱ��֮��
		 *    |--------|  ��ʼ�ͽ���ʱ��
		 *     |_____| ��סʱ�䡢�˷�ʱ��
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
		 * �ô������÷��ݵ�����������
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
		 * ��ʼ����
		 */
		ftObject = (FangTaiObject)map.get(rzrq);
		if(ftObject != null)
		{
			total = total + Double.parseDouble(ftObject.getPrice());
		}
		
		/**
		 * ѭ��ȡÿ��ķ����ۼ�
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
		 * ��������
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
		 * Ĭ�Ϻ���վ�Խ�
		 */
		if("1".equals(Parameters.getCommonParameter("��վ���Խ�"))) return;
		
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
				 * �ӱ��صõ���վ���������itemId,��ʼΪ0
				 * 
				 */
				long maxItemId = getMaxItemId(conn, "100");
				long maxItemId0 = maxItemId;
				
				/**
				 * �������itemId ɨ����վ����
				 *  ��ʱֻ��һ��
				 */
				String sql = "select o.*,m.guid from ht_mall_order o,ht_mall m where o.mallid=m.itemid and o.itemid > ? order by o.itemid";
				//String sql = "select o.*,m.guid from ht_mall_order o,ht_mall m where o.mallid=m.itemid and o.itemId=62 and o.itemId<>? order by o.itemid";
				//�������SQL
				String sql0 = "insert into fwddzb(guid,houseGuid,sqsj,sqdh,ddsx,ddzt,rzrq,tfrq,rzts,zje,ffxj,qtfy,xm,bz,itemid,lxdh,ptdh,sqbmguid,sqbm,rzsjqj," +
						                          "status,gzlzt,zdqxsj_xs,zdqxsj_fz,yk,ffyj,jff)" +
				              "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'5','2',12,59,?,?,?)";

				ps = webConn.prepareStatement(sql);
				ps.setLong(1, maxItemId);
				rs = ps.executeQuery();
				while(rs.next())
				{
					int orderId = rs.getInt("itemid");                     //��վ��������
					int number = rs.getInt("number");                      //��������������
					
					/**
					 * ���ǵ��ж��׷�
					 */
					for(int xx=0; xx<number; xx++)
					{
						String fwmbGuid = rs.getString("guid");               //ģ�巿��guid
						
						/**
						 * ����ʱ��
						 */
						long _sqsj = rs.getLong("addtime") * 1000;                    //���ʱ��
						Timestamp sqsj = new Timestamp(_sqsj);
						
						String ddsx = "HT";                                        //��������(������)
						/**
						 * ��վ�����Ķ����ţ���ʽ
						 * HT201407040002
						 */
						EngineTools eTools = new EngineTools();
						String sqdh = ddsx + eTools.dateSerial(conn, 40, 4);                           
						
						String ddzt = "0";                                         //����״̬��Ĭ��0��������
						String rzrq = rs.getString("fromdate");                    //��ס����
						String tfrq = rs.getString("todate");                      //�˷�����
						if(rzrq == null || tfrq == null)
						{
							//��ʱ�Ⱥ����������
							continue;
						}
						
						/**
						 * ���ݵ�guid���������
						 * 
						 */
						String houseGuid = getHouseGuid(rzrq, tfrq, conn, fwmbGuid);
						if(houseGuid == null)
						{
							/**
							 *  û�з��������ķ��ݣ�ȡ����ģ���Id
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
						
						//��ס����o.send_days
						int rzts = (int)((pDate.parse(tfrq).getTime() - pDate.parse(rzrq).getTime())/ONE_DAY);       
						
						double qtfy = rs.getDouble("o.fee");                        //��������
						String xm = rs.getString("o.buyer_name");                   //�˿�����
						String bz = rs.getString("o.note");
						String mobile = rs.getString("buyer_mobile");               //�˿���ϵ�绰
						
						ps = conn.prepareStatement(sql0);
						/**
						 * ����ϵͳ����
						 */
						int index = 1;
						String ddGuid = new GUID().toString();
						ps.setString(index++, ddGuid);
						
						/**
						 * ����ģ�巿��guid���������ʱ��Ŀ�ȱ��
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
						 * �۸������⣬��Ҫ��OAϵͳ�л�ȡ
						 */
						FangTai ft = new FangTai(conn);
						HashMap map = ft.getDayOfPrice(houseGuid, rzrq, tfrq);
						FangTaiObject ftObject = null;
						
						if(map != null) ftObject = (FangTaiObject)map.get(rzrq);
						
						/**
						 * ��ʼ�յķ���
						 */
						double price = (ftObject != null)?Double.parseDouble(ftObject.getPrice()):0;
						double zje = 0d;;
						//ps.setDouble(index++, price);
						
						/**
						 * �����ܷ���
						 */
						if(rzts <= 1)
						{
							zje = price;
						}
						else
						{
							zje = getTotalPrice(rzrq, tfrq, map);
						}
						
						ps.setDouble(index++, zje);   //�����ܼ� 
						ps.setDouble(index++, zje);   //Ӧ�տ�
						ps.setDouble(index++, qtfy); 
						ps.setString(index++, xm);
						ps.setString(index++, bz);
						ps.setInt(index++, orderId);
						ps.setString(index++, mobile);				
						
						//ƽ̨����
						ps.setString(index++, orderId + "");
						
						/**
						 * �������ƺͲ���guid
						 */
						ps.setString(index++, bmGuid);
						ps.setString(index++, bmName);
						
						/**
						 * ��ס������
						 * 2014-07-04,2014-07-04,1;
						 */
						Calendar cal = Calendar.getInstance();
						cal.setTime(pDate.parse(tfrq));
						cal.add(Calendar.DAY_OF_MONTH, -1);
						String qujian = rzrq + "," + pDate.format(cal.getTime()) + "," + rzts + ";";
						ps.setString(index++, qujian);
						ps.setDouble(index++, zje);   //���
						ps.setDouble(index++, zje);   //����ԭ��
						ps.setDouble(index++, zje);   //������
						
						ps.executeUpdate();
						
						if(orderId > maxItemId)
						{
							maxItemId = orderId;
						}
						
						/**
						 * ��Ӷ����ķ�����Ϣ
						 */
						addFjxx(conn, ddGuid, new String[]{rzrq, tfrq}, houseGuid);
						
						/**
						 * ��Ӷ�������ס��Ϣ
						 */
						addRzxx(conn, ddGuid, rzrq, tfrq);
						
						/**
						 * ͬ����վ�������۸񡢿ɶ���������
						 */
						net.alhs.api.CalendarToWebThread.addOrder(ddGuid);
					}
				}
				
				/**
				 * ������վ���������itemId
				 */
				if(maxItemId > maxItemId0) saveMaxItemId(conn, maxItemId, "100");
				
				
				/**
				 * 
				 * ɨ����Ӷ������տ���Ϣ
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
	 * ��Ӷ����ķ�����Ϣ
	 * @param conn
	 * @param ddGuid    ��������guid
	 * @param date ��ʼ���� ��������
	 * @param fj        ����
	 * @param ts        ������ס������
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
			//��ȡ��ʼ�۸�
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
			 * ��������ͬ�ļ۸�
			 *  û�м۸�ֱ�Ӻ���
			 */
			if(ftObject != null && !currentPrice.equals(ftObject.getPrice()))
			{
				ps.setString(1, new GUID().toString());
				ps.setString(2, ddGuid);
				ps.setString(3, startDate);    //���������			
				/**
				 * ��ʼ�յķ���
				 */
				ps.setDouble(4, (currentPrice != null)?Double.parseDouble(currentPrice):0);				
				ps.setDouble(5, (currentPrice != null)?Double.parseDouble(currentPrice):0);
				ps.setString(6, null);   //��ע
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
		ps.setString(3, startDate);    //���������			
		/**
		 * ��ʼ�յķ���
		 */
		ps.setDouble(4, (currentPrice != null)?Double.parseDouble(currentPrice):0);				
		ps.setDouble(5, (currentPrice != null)?Double.parseDouble(currentPrice):0);
		ps.setString(6, null);   //��ע
		ps.setInt(7, ts);
		
		ps.execute();
	}
	
	/**
	 * ��Ӷ�������ס��Ϣ
	 * @param conn   
	 * @param ddGuid  ��������guid
	 * @param rdrq    ��סʱ��
	 * @param tfrq    �˷�����
	 * @throws Exception
	 */
	private static void addRzxx(Connection conn, String ddGuid, String rdrq, String tfrq) throws Exception
	{
		/**
		 * �����סʱ������
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
	 * ɨ����վ���տ��¼����Ӷ������տ���Ϣ
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
		 * status 2��֧�� 3 ֧���ɹ�
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
				 * û�ж�Ӧ�Ķ��������Ը�����¼
				 */
				if(ddGuid == null) continue;
				
				if(ps == null)  ps = conn.prepareStatement(sql);
				
				yisk = rs.getDouble("amount")/guids.size();
				
				ps.setString(1, new GUID().toString());
				ps.setString(2, ddGuid);
				ps.setString(3, "������˹����");
				total += yisk;
				ps.setDouble(4, yisk);
				ps.setTimestamp(5, new Timestamp(rs.getLong("sendtime") * 1000));
				ps.setString(6, "������");
				String trade_no = rs.getString("trade_no");
				if(guids.size() > 1) trade_no = trade_no + "_" + (mm+1);
				ps.setString(7, trade_no);
				
				ps.execute();
				
				/**
				 * ������Ѿ��տ�޸���Ӧ״̬
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
	 * �޸�OA������Ϣ��ͬ��web.
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
			 * ����oa��houseGuid��web�ķ��ݱ��в��itemid
			 */
			int itemid = 0;                    //web��������
			ps = conn.prepareStatement(sql0);
			ps.setString(1, order.getHouseGuid());
			rs = ps.executeQuery();
			if(rs.next()){
				itemid = rs.getInt("itemid");
			}
			else{
				throw new Exception("����"+order.getHouseGuid()+"δͬ��!");
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
	 * ��sequence_sys���ȡ��վ���������itemId
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
	 * ������վ���������itemId
	 * @param id
	 * @throws Exception
	 */
	private static void saveMaxItemId(Connection conn, long id, String typeId)throws Exception
	{
		String sql = "update sequence_sys set SerialNumber='"+id+"' where Id=" + typeId;
		conn.createStatement().executeUpdate(sql);
	}

	/**
	 * OA״̬�ı�ʱ��ͬ��web����״̬
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
