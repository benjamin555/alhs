package net.alhs.template.yezhu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimerTask;
import net.sysmain.common.ConnectionManager;
import net.sysmain.util.StringTools;

public class YeZhuTotalInfo extends TimerTask 
{

	public void run() 
	{
        /**
         * ͳ������
         */
		updateYzTotalNum();
		
		/**
		 * ����ҵ��ȯ״̬
		 */
		Calendar cal = Calendar.getInstance();
		int h = cal.get(Calendar.HOUR_OF_DAY);   
		int m = cal.get(Calendar.MINUTE); 

		if(h==0 && m<5)
		{
			this.yzqOutTime(null);
		}


	}
	/**
	 * ͳ��ҵ��������������ҵ��ȯ�������������������ܶ�
	 *
	 */
	public void updateYzTotalNum() 
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = null;

		try 
		{
			/**
			 * �޸�ҵ����ҵ��������
			 */
			sql = "SELECT yzGuid,COUNT(*) as c FROM house WHERE fwzt='1' GROUP BY yzGuid";
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			sql = "update yzzl set houseNum=? where guid=?";
			PreparedStatement ps1 = conn.prepareStatement(sql);
			while(rs.next())
			{
				String guid = rs.getString("yzGuid");         //ҵ��Guid
				int num = rs.getInt("c");

				ps1.setInt(1, num);
				ps1.setString(2, guid);
				ps1.addBatch();
			}
			ps1.executeBatch();
			
			
			/**
			 * �޸�ҵ���Ķ��������������ܶ�
			 */
			sql = "SELECT h.yzGuid,COUNT(*) c,SUM(d.ffxj) s FROM fwddzb d,house h WHERE d.houseGuid=h.guid AND (d.noshow IS NULL OR d.noshow='0') AND (d.zcqx IS NULL OR d.zcqx='0')  GROUP BY h.yzGuid";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			sql = "update yzzl set ddNum=?,ddMoney=? where guid=?";
		    ps1 = conn.prepareStatement(sql);
			while(rs.next())
			{
				String guid = rs.getString("yzGuid");         //ҵ��Guid
				int num = rs.getInt("c");
				double money = rs.getDouble("s");

				ps1.setInt(1, num);
				ps1.setDouble(2, money);
				ps1.setString(3, guid);
				
				ps1.addBatch();
			}
			ps1.executeBatch();
			
			
			/**
			 * ����ҵ��ȯ��
			 */
		    HashMap map = new HashMap();
			sql = "SELECT b.yzGuid,COUNT(*) c FROM yzq a,house b WHERE a.houseGuid=b.guid AND zt>=0 and zt<1 GROUP BY b.yzGuid";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next())
			{
				String guid = rs.getString("yzGuid");         //ҵ��Guid
				int num = rs.getInt("c");
				map.put(guid, num+"");

			}
			sql = "select guid from yzzl";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while(rs.next())
			{
				String guid = rs.getString("guid");         //ҵ��Guid
				int num = 0;
			    if(map.containsKey(guid)){
			    	num = Integer.parseInt((String)map.get(guid));
			    }
			    conn.createStatement().executeUpdate("update yzzl set yzqNum="+num+" where guid='"+guid+"'");
			}
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
     * ����ҵ��ȯ״̬��ÿ��12��ִ�У�
     * ״̬��-1��ʧЧ  0����Ч�������ã�  0.1:��Ч����ʹ�ã� 1����ʹ��
     * @param houseGuid nullΪ����
     */
	public void yzqOutTime(String houseGuid) 
	{
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps0 = null;
		ResultSet rs = null;
		String sql = null;

		try 
		{
			String now = StringTools.getCurrentDateTime().substring(0, 10);  //��ǰ���� 
			conn = ConnectionManager.getInstance().getConnection();
			sql = "SELECT guid,yxq1_ksrq,yxq1_jsrq,yxq2_ksrq,yxq2_jsrq FROM yzq WHERE zt>=0 and zt<1";
			//�������ݶ�Ӧҵ��ȯ����
			if(houseGuid!=null){
				sql += " and houseGuid='"+houseGuid+"'";
			}
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
            sql = "update yzq set zt=? WHERE guid=?";
			ps0 = conn.prepareStatement(sql);
			
			while(rs.next()){
				String status = null;
				String guid = rs.getString(1);
				String d1 = rs.getString(2);
				String d2 = rs.getString(3);
				String d3 = rs.getString(4);
				String d4 = rs.getString(5);
				
				//��Ч��1 ��Ч��2 ���е����
				if(d3!=null && d4!=null){
					if(now.compareTo(d1)<0 || (now.compareTo(d2)>0 && now.compareTo(d3)<0)){
						status = "0";
					}
					else if((now.compareTo(d1)>=0 && now.compareTo(d2)<=0) || (now.compareTo(d3)>=0 && now.compareTo(d4)<=0)){
						status = "0.1";
					}
					else if(now.compareTo(d4)>=0){
						status = "-1";
					}
				}
				//����Ч��2 
				else{
					if(now.compareTo(d1)<0){
						status = "0";
					}
					else if((now.compareTo(d1)>=0 && now.compareTo(d2)<=0)){
						status = "0.1";
					}
					else if(now.compareTo(d2)>=0){
						status = "-1";
					}
				}
				
				ps0.setString(1, status);
				ps0.setString(2, guid);
				ps0.addBatch();
			}
			ps0.executeBatch();
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
