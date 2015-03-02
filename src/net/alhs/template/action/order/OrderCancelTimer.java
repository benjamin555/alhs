package net.alhs.template.action.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.TimerTask;
import net.sysmain.common.ConnectionManager;

public class OrderCancelTimer extends TimerTask 
{
	
	public void run() 
	{
		/**
		 * 预定，锁定 自动取消
		 */
		this.orderAutoCancel();
		
		/**
		 * 已订自动no show(0-5分钟)
		 */
		Calendar cal = Calendar.getInstance();
		int h = cal.get(Calendar.HOUR_OF_DAY);   
		int m = cal.get(Calendar.MINUTE); 

		if(h==0 && m<5)
		{
			this.noShow();
		}


	}
	/**
	 * 自动取消预定和锁定订单
	 *
	 */
	public void orderAutoCancel() 
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = null;

		try 
		{
			/**
			 *  查询 预定，锁定（非取消）没来(待处理,未入住)的订单
			 */
			sql = "SELECT guid FROM fwddzb ddzb WHERE ddzb.status <>'4' AND (ddzb.zcqx IS NULL OR ddzb.zcqx='0') AND (ddzb.ddzt='0' or ddzb.ddzt='1') AND NOW()>=CONCAT(rzrq,' ',IFNULL(zdqxsj_xs,'00'),':',IFNULL(zdqxsj_fz,'00'),':00')";
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			/**
			 * 取消
			 */
			sql = "update fwddzb set zcqx='1' where guid=?";
			PreparedStatement ps1 = conn.prepareStatement(sql);
			while(rs.next())
			{
				String guid = rs.getString(1);         //订单Guid
				ps1.setString(1, guid);
				ps1.addBatch();
			}

			ps1.executeBatch();


		} 
		catch (Exception e)
		{
			e.printStackTrace();

		}
		finally 
		{
			ConnectionManager.close(conn);
		}
	}
	
	/**
	 * 已订订单入住人日没来，自动NO SHOW(每天晚上12点执行)
	 *
	 */
	public void noShow() 
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = null;

		try 
		{
			/**
			 *  查询 已订（非取消）没来(待处理,未入住)的订单
			 */
			sql = "SELECT guid FROM fwddzb ddzb WHERE ddzb.status ='4' AND (ddzb.zcqx IS NULL OR ddzb.zcqx='0') AND ddzb.noshow IS NULL AND (ddzb.ddzt='0' or ddzb.ddzt='1') AND NOW()>CONCAT(ddzb.rzrq,' 23:59:59')";
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			/**
			 * noshow
			 */
			sql = "update fwddzb set noshow='0' where guid=?";
			PreparedStatement ps1 = conn.prepareStatement(sql);
			while(rs.next())
			{
				String guid = rs.getString(1);         //订单Guid
				ps1.setString(1, guid);
				ps1.addBatch();
			}

			ps1.executeBatch();


		} 
		catch (Exception e)
		{
			e.printStackTrace();

		}
		finally 
		{
			ConnectionManager.close(conn);
		}
	}
}
