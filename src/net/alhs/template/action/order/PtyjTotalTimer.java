package net.alhs.template.action.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.TimerTask;
import net.sysmain.common.ConnectionManager;
import net.sysmain.util.GUID;

public class PtyjTotalTimer extends TimerTask 
{
	
	public void run() 
	{
		/**
		 * δ����Ӷ�����
		 */
		this.calcPtyjAndUpdate(0);      
		
		/**
		 * �Ѵ���Ӷ�����
		 */
		this.calcPtyjAndUpdate(1);
	}

	/**
	 * ͳ�Ƹ�ƽ̨��Ӷ��ͻ�ѡӶ��
	 * zt:0 (δ����)
	 * zt:1 (�Ѵ���)
	 * @param zt
	 */
	public void calcPtyjAndUpdate(int zt) 
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = null;

		try 
		{
			conn = ConnectionManager.getInstance().getConnection();
		    
			PreparedStatement p0 = conn.prepareStatement("insert into ptxxmx(guid,ptxxGuid,ddzs,ffze,ptyj,hxyj,lx,zt) values(?,?,?,?,?,?,?,?)");

			p0.addBatch("delete from ptxxmx where zt='"+zt+"'");
			
			/**
			 * ͳ�Ƹ�ƽ̨��Ӷ��ͻ�ѡӶ��
			 */
			sql = "SELECT b.ptxxGuid,COUNT(*) ddzs,SUM(a.ffxj) ffze,SUM(b.ptyj) ptyj,'1' AS lx FROM fwddzb a,ddhs b WHERE a.guid=b.guid AND b.ptxxGuid IS NOT NULL and b.ptyj>0 and b.sffy_pt"+(zt==0?"<2":"='2'")+" GROUP BY b.ptxxGuid " +
					"UNION " +
				  "SELECT b.ptxxGuid,COUNT(*) ddzs,SUM(a.ffxj) ffze,SUM(b.hxyj) ptyj,'2' AS lx FROM fwddzb a,ddhs b WHERE a.guid=b.guid AND b.ptxxGuid IS NOT NULL and b.hxyj>0 and b.sffy_hx"+(zt==0?"<2":"='2'")+" GROUP BY b.ptxxGuid ";
		    ps = conn.prepareStatement(sql);
		    rs = ps.executeQuery();
			while(rs.next())
			{
				String ptxxGuid = rs.getString("ptxxGuid");         //ƽ̨Guid
				int ddzs = rs.getInt("ddzs");
				double ffze = rs.getDouble("ffze");
				double yj = rs.getDouble("ptyj");                   //Ӷ��
				String type = rs.getString("lx");                   //����    1.ƽ̨Ӷ�� 2.��ѡӶ��
				
				p0.setString(1, new GUID().toString());
				p0.setString(2, ptxxGuid);
				p0.setInt(3, ddzs);
				p0.setDouble(4, ffze);
				p0.setDouble(5, type.equals("1")?yj:0.00);
				p0.setDouble(6, type.equals("2")?yj:0.00);
				p0.setString(7, type);
				p0.setString(8, zt+"");
				p0.addBatch();
			}
			
			p0.executeBatch();
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
