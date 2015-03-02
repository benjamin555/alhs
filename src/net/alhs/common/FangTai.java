package net.alhs.common;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sysmain.common.ConnectionManager;
import net.sysmain.util.StringTools;

public class FangTai implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Connection conn = null;

	public FangTai(Connection conn)
	{
		this.conn=conn;
	}

	private HashMap price = new HashMap(); 


	/**
	 * 本次请求中，对房型价格的缓存
	 * 房型guid, map数据
	 */
	private HashMap priceCache = new HashMap();

	/**
	 * 根据房屋GUID初始化
	 * @param houseGuid
	 * @throws Exception
	 * 
	 *    返回房型guid,日历guid
	 */
	private String[] priceInit(String houseGuid)throws Exception
	{
		String[] retValue = new String[2];
		PreparedStatement ps = null;
		ResultSet rs = null;

		/**
		 * 根据房屋guid查询该房屋的房型模版Guid(房型模版即为本身GUID)
		 */
		String sql = "select guid from house where fxGuid=(select fxGuid from house where guid = ?) and fwzt='2'";
		ps =conn.prepareStatement(sql);
		ps.setString(1, houseGuid);
		rs = ps.executeQuery();
		if(rs.next())
		{
			retValue[0] = rs.getString("guid");
		}

		/**
		 * 根据房屋guid查询该房屋的房型模版对应的rlGuid(房型模版即为本身的rlGuid)
		 */
		sql = "select rlGuid from house where fxGuid=(select fxGuid from house where guid = ?) and fwzt='2'";
		ps =conn.prepareStatement(sql);
		ps.setString(1, houseGuid);
		rs = ps.executeQuery();
		if(rs.next())
		{
			retValue[1] = rs.getString("rlGuid");
		}

		return retValue;
	}

	/**
	 * 查询房屋每天的价格（begindate，enddate为null查询所有）
	 * @param houseGuid
	 * @param begindate
	 * @param enddate
	 * @return
	 * @throws Exception
	 */
	public HashMap getDayOfPrice(String houseGuid,String begindate,String enddate)throws Exception
	{
		/**
		 * 初始化
		 */
		String[] retValue = this.priceInit(houseGuid);
		String fxGuid = retValue[0];
		String rlGuid = retValue[1];

		PreparedStatement ps = null;
		ResultSet rs = null;
		LinkedHashMap map = null;

		if(fxGuid == null || fxGuid.trim().equals("")) return null;

		if((map=(LinkedHashMap)priceCache.get(fxGuid)) == null)
		{
			map = new LinkedHashMap();
		}
		else
		{//直接从缓存中返回房型价格
			return map;
		}

		try
		{
			/**
			 * 查询房屋（房型）价格
			 */
			String sql = "select djr,xjr,wjm,wjt,djm,djt,zr,gzr from fwjg where houseGuid=?";
			ps =conn.prepareStatement(sql);
			ps.setString(1, fxGuid);
			rs = ps.executeQuery();
			if(rs.next())
			{
				price.put("大假日", rs.getDouble(1)+"");
				price.put("小假日", rs.getDouble(2)+"");
				price.put("旺季末", rs.getDouble(3)+"");
				price.put("旺季天", rs.getDouble(4)+"");
				price.put("淡季末", rs.getDouble(5)+"");
				price.put("淡季天", rs.getDouble(6)+"");
				price.put("周末", rs.getDouble(7)+"");
				price.put("工作日", rs.getDouble(8)+"");
			}

			sql = "select nyr,rqlx from rqsz where rlGuid = ?";
			if(begindate == null && enddate == null)
			{//如果没有指定时间，条件设置为当前日期之后
				sql += "and nyr>='"+StringTools.getCurrentDateTime().substring(0, 10)+"'";
			}
			//根据指定日期查
			if(begindate != null)
			{
				sql += " and nyr >='" + begindate + "'";
			}
			if(enddate != null)
			{
				sql += " and nyr<='" + enddate + "'";
			}
			sql += " order by nyr desc";

			ps = conn.prepareStatement(sql);
			ps.setString(1, rlGuid);
			rs = ps.executeQuery();
			while(rs.next())
			{
				String nyr = rs.getString("nyr");
				String rqlx = rs.getString("rqlx");
				FangTaiObject fto = FangTaiObject.getInstance();
				fto.setNyr(nyr);
				fto.setPrice((String)price.get(rqlx));
				map.put(nyr, fto);
			}
			ConnectionManager.close(ps);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{

		}

		this.priceCache.put(fxGuid, map);

		return map;
	}


	/**
	 * 返回起始区间内指定房型的订单
	 * @param fxGuid    房型guid
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public HashMap getOrdersByHouse(String fxGuid, String beginDate, String endDate) throws Exception
	{

		HashMap orders = new HashMap();
		/**
		 * 
		 * 订单主表的订单确认状态 status
		 * 4  已订 
		 * 5  预订 
		 * 6  锁定 
		 *   
		 *   zcqx 没有取消
		 * null
		 * 0  
		 */		
		StringBuffer sqlOrder = new StringBuffer("select c.rq from fwddzb a,house b,ddfjxx c where a.houseGuid=b.guid and b.fxguid='");

		sqlOrder.append(fxGuid).append("' and a.guid=c.zbguid and a.status>='4' and a.status<='6' and (a.zcqx is null or a.zcqx='0')");
		//简单处理，不进行时间的比较
//		if(beginDate != null && !beginDate.equals(""))
//		{//订单开始时间
//		sqlOrder.append(" and a.rzrq>='").append(beginDate).append("'");
//		}

//		if(endDate != null && !endDate.equals(""))
//		{//订单结束时间
//		sqlOrder.append(" and a.rzrq<='").append(endDate).append("'");
//		}
		Statement smt = this.conn.createStatement(); 
		ResultSet rs = smt.executeQuery(sqlOrder.toString());
		while(rs.next())
		{
			String dateStr = rs.getString("rq");
			if(dateStr != null && !dateStr.trim().equals(""))
			{
				String[] temp = dateStr.split(",");
				for(int i=0; i<temp.length; i++)
				{
					/**
					 * 起始日前之前，信息被忽略
					 */
					if(temp[i].compareTo(beginDate) < 0) continue;

					/**
					 * 在结束日期之后，信息被忽略
					 */
					if(endDate != null && temp[i].compareTo(endDate) > 0) continue;
					Integer count = (Integer)orders.get(temp[i]); 
					if(count != null)
					{//相同房型的订单+1
						int x = count.intValue()+1;
						orders.put(temp[i], new Integer(x));
					}
					else
					{//第一次初始化
						orders.put(temp[i], new Integer(1));
					}
				}
			}
		}
		ConnectionManager.close(smt);

		return orders;
	}


	/**
	 * 是否重新下单（所选房间所选日期是否已经有订单）
	 * @param houseGuid
	 * @param date
	 * @return List(重复下单的日期[null表示无重复])
	 * @throws Exception
	 */
	public List isHaveOrder(String houseGuid,String [] date)throws Exception{
		/**
		 * 从房价信息表查询所选房间的所有日期（不计算取消，noshow）
		 */
		String sql  = "select rq from ddfjxx where zbGuid in (select guid from fwddzb where houseGuid='"+houseGuid+"' AND (zcqx IS NULL OR zcqx='0') AND (noshow IS NULL OR noshow='0'))";

		Map map = new HashMap();  //存储日期
		Statement smt = this.conn.createStatement(); 
		ResultSet rs = smt.executeQuery(sql);
		while(rs.next()){
			String [] rq = rs.getString("rq").split(",");
			for (int i = 0; i < rq.length; i++) {
				map.put(rq[i], rq[i]);
			}
		}

		List dateList = null;
		//date格式：xxxx-xx-xx,xxxx-xx-xx
		for (int i = 0; i < date.length; i++) {
			String [] d = date[i].split(",");  //单个日期
			for(int j = 0; j < d.length; j++){
				if(map.containsKey(d[j])){
					if(dateList == null) {dateList = new ArrayList();};
					dateList.add(d[j]);
				}
			}
		}
		ConnectionManager.close(smt);

		return dateList;
	}

	/**
	 * 计算指定日期的订单数量（已订 预订 锁定 可订），默认当天
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public int [] getOrderNumByDate(String date)throws Exception{
		int [] num = {0,0,0,0};
		if(date==null || date.equals("")){  //date为空默认当天的日期
			date = StringTools.getCurrentDateTime().substring(0, 10);    
		}

		String sql  = "SELECT a.status,b.rq FROM fwddzb a,ddfjxx b  WHERE b.zbGuid=a.guid  AND (a.zcqx IS NULL OR a.zcqx='0') AND (a.noshow IS NULL OR a.noshow='0') AND b.rq LIKE '%"+date+"%'";

		Statement smt = this.conn.createStatement(); 
		ResultSet rs = smt.executeQuery(sql);
		while(rs.next()){
			String status = rs.getString("a.status");         //订单状态： 4 已订  5 预订 6 锁定 
			//String [] rq = rs.getString("b.rq").split(",");

			if(status.equals("4")){
				num[0]++;
			}
			else if(status.equals("5")){
				num[1]++;
			}
			else if(status.equals("6")){
				num[2]++;
			}
		}

		int count = 0;   //可售的业主房总数
		rs = smt.executeQuery("SELECT COUNT(*) c FROM house WHERE issale='1'");
		if(rs.next() && rs.getInt("c")>0){
			count = rs.getInt("c");
		}
		
		num[3] = count-num[0]-num[1]-num[2];
		
		ConnectionManager.close(smt);

		return num;
	}
}
