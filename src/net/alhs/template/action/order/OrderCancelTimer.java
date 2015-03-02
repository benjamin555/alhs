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
		 * Ԥ�������� �Զ�ȡ��
		 */
		this.orderAutoCancel();
		
		/**
		 * �Ѷ��Զ�no show(0-5����)
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
	 * �Զ�ȡ��Ԥ������������
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
			 *  ��ѯ Ԥ������������ȡ����û��(������,δ��ס)�Ķ���
			 */
			sql = "SELECT guid FROM fwddzb ddzb WHERE ddzb.status <>'4' AND (ddzb.zcqx IS NULL OR ddzb.zcqx='0') AND (ddzb.ddzt='0' or ddzb.ddzt='1') AND NOW()>=CONCAT(rzrq,' ',IFNULL(zdqxsj_xs,'00'),':',IFNULL(zdqxsj_fz,'00'),':00')";
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			/**
			 * ȡ��
			 */
			sql = "update fwddzb set zcqx='1' where guid=?";
			PreparedStatement ps1 = conn.prepareStatement(sql);
			while(rs.next())
			{
				String guid = rs.getString(1);         //����Guid
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
	 * �Ѷ�������ס����û�����Զ�NO SHOW(ÿ������12��ִ��)
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
			 *  ��ѯ �Ѷ�����ȡ����û��(������,δ��ס)�Ķ���
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
				String guid = rs.getString(1);         //����Guid
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
