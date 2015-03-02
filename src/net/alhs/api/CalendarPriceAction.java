package net.alhs.api;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import net.alhs.common.FangTai;
import net.alhs.common.FangTaiObject;
import net.sysmain.common.ConnectionManager;

/**
 * 价格和日期接口
 * @author Owner
 *
 */
public class CalendarPriceAction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	
	
	private FangTai fangTai = null;
	
	
	private Connection conn = null;
	
	public void setConnection(Connection conn)
	{
		this.conn = conn;
	}
	
	
	
	/**
	 * 家游网数据库连接
	 */
	private Connection webConn = null;
	
	
	public void setWebConnection(Connection webConn)
	{
		this.webConn = webConn;
	}
	
	private static SimpleDateFormat pDate = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 清除家游网中的数据，并重新导入
	 *
	 */
	public void clearAndInit()
	{	
		String sql = "Select guid from house where fwzt='2' and fxGuid is not null";
		boolean isCreateConn = false;
		boolean isCreateOuterConn = false;
		
		try
		{
			long t1 = System.currentTimeMillis();
			if(this.conn == null) 
			{
				this.conn = ConnectionManager.getInstance().getConnection();
				isCreateConn = true;
			}
			
			if(this.webConn == null) 
			{
				this.webConn = ConnectionManager.getInstance().getConnection("homtrip.xml");
				isCreateOuterConn = true;
			}
			ResultSet rs = this.conn.createStatement().executeQuery(sql);
			Calendar cal = Calendar.getInstance();
			String beginDate = pDate.format(cal.getTime());
			while(rs.next())
			{
				String houseGuid = rs.getString("guid");
				putToWebByHouse(houseGuid, beginDate);
				//休眠10秒，释放CPU
				Thread.sleep(1000);
			}
			System.out.println("日历价格初始化完成，耗时：" + (System.currentTimeMillis()-t1)/1000d + "毫秒");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(isCreateConn) ConnectionManager.close(this.conn);
			if(isCreateOuterConn) ConnectionManager.close(this.webConn);
		}
	}
	
	/**
	 * 依据订单的变化操作
	 * @param orderGuid  订单的guid
	 */
	public void putToWebByOrder(String orderGuid)
	{
		String sql = "Select houseGuid,rzrq,tfrq from fwddzb where guid='" + orderGuid + "'";
		boolean isCreateConn = false;
		boolean isCreateOuterConn = false;
		
		try
		{
			if(this.conn == null) 
			{
				this.conn = ConnectionManager.getInstance().getConnection();
				isCreateConn = true;
			}
			
			if(this.webConn == null) 
			{
				this.webConn = ConnectionManager.getInstance().getConnection("homtrip.xml");
				isCreateOuterConn = true;
			}
			ResultSet rs = this.conn.createStatement().executeQuery(sql);
			String beginDate = null;
			String endDate = null;
			if(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				String curDate = pDate.format(cal.getTime());
				String houseGuid = rs.getString("houseGuid");
				beginDate = pDate.format(rs.getDate("rzrq"));
				endDate = pDate.format(rs.getDate("tfrq"));
				if(curDate.compareTo(endDate) > 1)
				{//超过了订单的截止日期，不统计
					return;
				}
				if(curDate.compareTo(beginDate) > 1)
				{//只处理当前日期之后的数据
					beginDate = curDate;
				}
				putToWebByHouse(houseGuid, beginDate, endDate);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(isCreateConn) ConnectionManager.close(this.conn);
			if(isCreateOuterConn) ConnectionManager.close(this.webConn);
		}
	}
	
	public void putToWebByHouse(String houseGuid, String beginDate)
	{
		putToWebByHouse(houseGuid, beginDate, null);
	}
	
	public void putToWebByHouse(String houseGuid)
	{
		putToWebByHouse(houseGuid, null, null);
	}
	
	/**
	 * 通过房型guid进行处理，用于调价管理的接口
	 * @param fxGuids
	 */
	public void putToWebByFangXing(String[] fxGuids)
	{
		boolean isCreateConn = false;
		boolean isCreateOuterConn = false;
		StringBuffer sqlBuf = new StringBuffer();
		
		for(int i=0; i<fxGuids.length; i++)
		{
			if(i > 0) sqlBuf.append(" union ");
			sqlBuf.append("Select guid from House where fxGuid='").append(fxGuids[i]).append("' and fwzt='2'");
		}
		
		try
		{
			if(this.conn == null) 
			{
				this.conn = ConnectionManager.getInstance().getConnection();
				isCreateConn = true;
			}
			if(this.webConn == null) 
			{
				this.webConn = ConnectionManager.getInstance().getConnection("homtrip.xml");
				isCreateOuterConn = true;
			}
			ResultSet rs = this.conn.createStatement().executeQuery(sqlBuf.toString());
			while(rs.next())
			{
				putToWebByHouse(rs.getString("guid"));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(isCreateConn) ConnectionManager.close(this.conn);
			if(isCreateOuterConn) ConnectionManager.close(this.webConn);
		}
	}
	
	/**
	 * 按房屋或模板房
	 * @param houseGuid 模板房的guid/房屋guid
	 * @param beginDate 开始日期
	 * @param conn      oa数据连接
	 * @param webConn   网站数据库连接
	 */
	public void putToWebByHouse(String houseGuid, String beginDate, String endDate)
	{		
		Calendar cal = Calendar.getInstance();
		String curDate = pDate.format(cal.getTime());
		if(beginDate == null || curDate.compareTo(beginDate) > 1)
		{//只处理当前日期之后的数据
			beginDate = curDate;
		}
		
		boolean isCreateConn = false;
		boolean isCreateOuterConn = false;
		try
		{
			/**
			 * 1、返回房型guid
			 */
			String sql = "select fxGuid,fwzt from house where Guid='" + houseGuid + "'";
			String fxGuid = null;
			String fwzt = null;
			if(this.conn == null) 
			{
				this.conn = ConnectionManager.getInstance().getConnection();
				isCreateConn = true;
			}
			ResultSet rs0 = this.conn.createStatement().executeQuery(sql);
			if(rs0.next())
			{
				fxGuid = rs0.getString("fxGuid");
				fwzt = rs0.getString("fwzt");
			}
			if(fxGuid == null || fwzt == null) return;
			
			long currentTime = System.currentTimeMillis() / 1000;
			if(!"2".equals(fwzt))
			{//如果不是模板房，返回对应的模板房guid
				ResultSet rs1 = this.conn.createStatement().executeQuery("Select guid from house where fxGuid='" + fxGuid +"' and fwzt='2'");
				if(rs1.next())
				{
					houseGuid = rs1.getString("guid");
				}
			}
			
			int itemId = -1;
			if(this.webConn == null) 
			{
				this.webConn = ConnectionManager.getInstance().getConnection("homtrip.xml");
				isCreateOuterConn = true;
			}
			/**
			 * 2、返回网站的房屋的ID
			 */
			ResultSet rsWebHouse = this.webConn.createStatement().executeQuery("select itemId from ht_mall where Guid='" + houseGuid + "'");
			if(rsWebHouse.next())
			{
				itemId = rsWebHouse.getInt("itemId");
			}
			/**
			 * 网站对应的模板房信息不存在
			 */
			if(itemId == -1) return;
				
			if(fangTai == null) fangTai = new FangTai(this.conn);
			
			/**
			 * 房屋的数据
			 *    业主房  fwzt='1'
			 *    可售   issale='1'
			 */
			String sqlFx = "select count(*) as num,1 as type from house where fwzt='1' and issale='1' and fxGuid='" + fxGuid + "'";
			       sqlFx = sqlFx + " union select count(*) as num,2 as type from house where (fwzt='1' or fwzt='3') and fxGuid='" + fxGuid + "'";
			ResultSet rs = this.conn.createStatement().executeQuery(sqlFx);
			/**
			 * 业主房的数量
			 */
			int count = 0;
			int totalCount = 0;
			while(rs.next())
			{
				if(rs.getInt("type") == 1)
				{
					count = rs.getInt("num");
				}
				else if(rs.getInt("type") == 2)
				{
					totalCount = rs.getInt("num");
				}
			}
			
			/**
			 * 关键点：价格日期为基础决定调整的时间区间
			 * 当前之后的日历价格，时间为key，格式：2014-07-06
			 * 
			 */
			HashMap prices = fangTai.getDayOfPrice(houseGuid, beginDate, endDate);
			
			HashMap orders = fangTai.getOrdersByHouse(fxGuid, beginDate, endDate);
			
			/**
			 * 同步家游网
			 */
			Iterator its = prices.keySet().iterator();
			PreparedStatement psWeb1 = this.webConn.prepareStatement("Delete from ht_price where modeid=? and mdate=?");				
			PreparedStatement psWeb2 = this.webConn.prepareStatement("Insert into ht_price(modeid,leftcount,price,mtime,mdate) values(?,?,?,?,?)");
			PreparedStatement psWeb3 = this.webConn.prepareStatement("update ht_mall set amount=? where itemId=?");
			int leftCount = 0;
			while(its.hasNext())
			{
				String date = (String)its.next();   //某日的信息
				java.sql.Date cDate = java.sql.Date.valueOf(date);
				//执行删除
				psWeb1.setInt(1, itemId);
				psWeb1.setDate(2, cDate);
				psWeb1.execute();
				
				//执行添加
				FangTaiObject fto = (FangTaiObject)prices.get(date);
				Integer orderNum = (Integer)orders.get(date);
				psWeb2.setInt(1, itemId);
				if(orderNum != null)
				{//存在订单
					leftCount =  count-orderNum.intValue();
				}
				else
				{
					leftCount = count;
				}
				if(leftCount < 0) 
				{
					System.out.println(date + ",模板房guid：" + houseGuid + "，可售小于0");
					leftCount = 0;
				}
				psWeb2.setInt(2, leftCount);
				psWeb2.setDouble(3, Double.parseDouble(fto.getPrice()));
				psWeb2.setLong(4, currentTime);
				psWeb2.setDate(5, cDate);
				psWeb2.execute();
				
				//修改总套数
				psWeb3.setInt(1, totalCount);
				psWeb3.setInt(2, itemId);
				psWeb3.execute();
			}
			ConnectionManager.close(psWeb1);
			ConnectionManager.close(psWeb2);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(isCreateConn) ConnectionManager.close(this.conn);
			if(isCreateOuterConn) ConnectionManager.close(this.webConn);
		}
	}
}
