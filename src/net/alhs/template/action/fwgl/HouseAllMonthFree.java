package net.alhs.template.action.fwgl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimerTask;
import net.alhs.api.AddHouseFreeDetail;
import net.sysmain.common.ConnectionManager;

public class HouseAllMonthFree extends TimerTask 
{
	private Map map = new HashMap();

	public void run() 
	{
        /**
         * ͳ��
         */
		monthFreeTotal();
	}

	/**
	 * ͳ��ҵ����������ܱ�
	 *
	 */
	public void monthFreeTotal() 
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = null;

		try 
		{
			/**
			 * ͳ��������ܿ۷�
			 */
			sql = "SELECT a.houseGuid,b.ksrq,b.jsrq,SUM(a.zj) zj,b.yzcdzfy FROM fwzjmx a,house b WHERE a.houseGuid=b.guid AND b.fwzt='1' GROUP BY a.houseGuid";
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while(rs.next())
			{
				String guid = rs.getString("houseGuid");         //����Guid
				String begindate = rs.getString("ksrq");
				String enddate = rs.getString("jsrq");
				double zj = rs.getDouble("zj");                  //�����
				double zkf = rs.getDouble("yzcdzfy");            //�ܿ۷�

				AddHouseFreeDetail fd = new AddHouseFreeDetail();
				LinkedHashMap monthList = fd.getMonthList(begindate.substring(0,10), enddate.substring(0,10));
				
				HouseMonthFree free = new HouseMonthFree();
				free.setGuid(guid);
				free.setYfs(monthList.size());   //�·���
				free.setZzj(zj);                 //�����
				free.setZkf(zkf);                //�ܿ۷�
				
				map.put(guid, free);
			}
			
			/**
			 * ͳ���ѿ۷ѣ��Ѹ����δ�۷��ã�δ�����
			 */
			sql = "SELECT a.houseGuid,SUM(a.kf) kf,SUM(a.zj) yfzj FROM fwzjmx a,house b WHERE a.houseGuid=b.guid AND b.fwzt='1' AND a.fkzt='1' GROUP BY a.houseGuid";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next())
			{
				String guid = rs.getString("houseGuid");         //����Guid
				double kf = rs.getDouble("kf");                  //�ѿ۷�
				double yfzj = rs.getDouble("yfzj");              //�Ѹ����

				HouseMonthFree free = (HouseMonthFree)map.get(guid);
				if(free==null){
					continue;
				}
				free.setYkfy(kf);                     //�ѿ۷�
				free.setYfzj(yfzj);                   //�Ѹ����
				free.setWkfy(free.getZkf()-kf);       //δ�۷��ã��ܿ۷�-�ѿ۷ѣ�
				free.setWfzj(free.getZzj()-yfzj);     //δ����������-�Ѹ����
				free.setYzj(free.getZzj()/(free.getYfs()==0?1:free.getYfs()));
			}
			
			/**
			 * ͳ�ƶ�����������������������
			 */
			sql = "SELECT a.houseGuid,COUNT(*) dds,SUM(a.ffxj) ddje,SUM(b.ddsy) ddsy FROM fwddzb a,ddhs b WHERE a.guid=b.guid AND IFNULL(a.noshow,'0') <> '3' AND a.ddsx<> 'YZ' GROUP BY a.houseGuid";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next())
			{
				String guid = rs.getString("houseGuid");         //����Guid
				int ddsl = rs.getInt("dds");                     //��������
				double ddje = rs.getDouble("ddje");              //�������
				double ddsy = rs.getDouble("ddsy");              //��������

				HouseMonthFree free = (HouseMonthFree)map.get(guid);
				if(free==null){
					continue;
				}
				free.setDds(ddsl);
				free.setDdje(ddje);
				free.setDdsy(ddsy);
				free.setDdjj(ddje/(ddsl==0?1:ddsl));   //�������ۣ��ܽ��/����������
			}
			
			doInsert();
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
	private void doInsert(){
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = null;
		try 
		{
			
			sql = "delete from fwyzzb";
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			
			sql = "insert into fwyzzb(guid,yfs,yzj,zzj,ykfy,yfzj,wkfy,wfzj,dds,ddje,ddjj,ddsy) values(?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			Iterator it = map.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry pairs = (Map.Entry)it.next();
				HouseMonthFree free = (HouseMonthFree)pairs.getValue();
				ps.setString(1, free.getGuid());
				ps.setInt(2, free.getYfs());
				ps.setDouble(3, free.getYzj());
				ps.setDouble(4, free.getZzj());
				ps.setDouble(5, free.getYkfy());
				ps.setDouble(6, free.getYfzj());
				ps.setDouble(7, free.getWkfy());
				ps.setDouble(8, free.getWfzj());
				ps.setInt(9, free.getDds());
				ps.setDouble(10, free.getDdje());
				ps.setDouble(11, free.getDdjj());
				ps.setDouble(12, free.getDdsy());
				ps.addBatch();
			}
			
			ps.executeBatch();
			
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
	private class HouseMonthFree{
		private String guid = null;   //����Guid
		private int yfs = 0;          //�·���
		private double yzj = 0;       //����������/���·ݣ�
		private double zzj = 0;       //�����
		private double zkf = 0;       //�ܿ۷�
		private double ykfy = 0;      //�ѿ۷���
		private double yfzj = 0;      //�Ѹ����
		private double wkfy = 0;      //δ�۷���
		private double wfzj = 0;      //δ�����
		private int dds = 0;          //������
		private double ddje = 0;      //�������
		private double ddjj = 0;      //��������
		private double ddsy = 0;      //��������
		
		public double getDdje() {
			return ddje;
		}
		public void setDdje(double ddje) {
			this.ddje = ddje;
		}
		public double getDdjj() {
			return ddjj;
		}
		public void setDdjj(double ddjj) {
			this.ddjj = ddjj;
		}
		public int getDds() {
			return dds;
		}
		public void setDds(int dds) {
			this.dds = dds;
		}
		public double getDdsy() {
			return ddsy;
		}
		public void setDdsy(double ddsy) {
			this.ddsy = ddsy;
		}
		public String getGuid() {
			return guid;
		}
		public void setGuid(String guid) {
			this.guid = guid;
		}
		public double getWfzj() {
			return wfzj;
		}
		public void setWfzj(double wfzj) {
			this.wfzj = wfzj;
		}
		public double getWkfy() {
			return wkfy;
		}
		public void setWkfy(double wkfy) {
			this.wkfy = wkfy;
		}
		public int getYfs() {
			return yfs;
		}
		public void setYfs(int yfs) {
			this.yfs = yfs;
		}
		public double getYfzj() {
			return yfzj;
		}
		public void setYfzj(double yfzj) {
			this.yfzj = yfzj;
		}
		public double getYkfy() {
			return ykfy;
		}
		public void setYkfy(double ykfy) {
			this.ykfy = ykfy;
		}
		public double getYzj() {
			return yzj;
		}
		public void setYzj(double yzj) {
			this.yzj = yzj;
		}
		public double getZzj() {
			return zzj;
		}
		public void setZzj(double zzj) {
			this.zzj = zzj;
		}
		public double getZkf() {
			return zkf;
		}
		public void setZkf(double zkf) {
			this.zkf = zkf;
		}
	}
}
