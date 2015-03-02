package net.alhs.template.action.fwgl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;
import net.sysmain.common.ConnectionManager;

public class HouseTimer extends TimerTask 
{
	private int day = -1;

	public void run() 
	{
		/**
		 * 控制每天0点到5点之间，且每天只执行1次
		 */
		Calendar cal = Calendar.getInstance();
		int h = cal.get(Calendar.HOUR_OF_DAY);   //当前时间
		int d = cal.get(Calendar.DAY_OF_MONTH);  //每月的日期
        
		//System.out.println("进入!!!!!!!!!!!!!!!!!!!!!");
		if(h > 6 || d == day) return;

		/**
		 * 执行更新
		 */
		this.updateWzts();
		this.updateHtTs();
		
		this.day = d;
	}
	/**
	 * 修改房间的未住天数
	 *
	 */
	public void updateWzts() 
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = null;

		try 
		{
			    sql = "select guid,qlsj from house where qlsj is not null";
			    conn = ConnectionManager.getInstance().getConnection();
			    ps = conn.prepareStatement(sql);
			    rs = ps.executeQuery();
			    
			    sql = "update house set wzts=? where guid=?";
			    PreparedStatement ps1 = conn.prepareStatement(sql);
			    while(rs.next())
			    {
			    	String guid = rs.getString(1);        //房子Guid
			    	String clearTime = rs.getString(2);   //清理时间
			    	
			    	ps1.setInt(1, this.getDays(clearTime));
			    	ps1.setString(2, guid);
			    	ps1.addBatch();
			    }
				
                   ps1.executeBatch();
				
				
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			day = -1; //执行失败，则还原
		}
		finally 
		{
			ConnectionManager.close(conn);
		}
	}
	
	/**
	 * 修改合同的倒计时天数
	 *
	 */
	public void updateHtTs() 
	{
		Connection conn = null;

		try 
		{
	
			    conn = ConnectionManager.getInstance().getConnection();

				conn.createStatement().executeUpdate("UPDATE house SET djsts=DATEDIFF(jsrq,NOW()) WHERE jsrq IS NOT NULL");
				
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			day = -1; //执行失败，则还原
		}
		finally 
		{
			ConnectionManager.close(conn);
		}
	}
	
	/**
	 * 返回当前时间与清理时间的天数
	 * @param clearTime
	 * @return
	 * @throws Exception
	 */
	private int getDays(String clearTime) throws Exception
	{
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		long dateToday = new Date().getTime(); // 当前时间
		int date = (int)((dateToday-fmt.parse(clearTime).getTime())/1000/60/60/24);
		return date;
	}
}
