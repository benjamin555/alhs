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
         * 统计
         */
		monthFreeTotal();
	}

	/**
	 * 统计业主月租费用总表
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
			 * 统计总租金，总扣费
			 */
			sql = "SELECT a.houseGuid,b.ksrq,b.jsrq,SUM(a.zj) zj,b.yzcdzfy FROM fwzjmx a,house b WHERE a.houseGuid=b.guid AND b.fwzt='1' GROUP BY a.houseGuid";
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while(rs.next())
			{
				String guid = rs.getString("houseGuid");         //房屋Guid
				String begindate = rs.getString("ksrq");
				String enddate = rs.getString("jsrq");
				double zj = rs.getDouble("zj");                  //总租金
				double zkf = rs.getDouble("yzcdzfy");            //总扣费

				AddHouseFreeDetail fd = new AddHouseFreeDetail();
				LinkedHashMap monthList = fd.getMonthList(begindate.substring(0,10), enddate.substring(0,10));
				
				HouseMonthFree free = new HouseMonthFree();
				free.setGuid(guid);
				free.setYfs(monthList.size());   //月份数
				free.setZzj(zj);                 //总租金
				free.setZkf(zkf);                //总扣费
				
				map.put(guid, free);
			}
			
			/**
			 * 统计已扣费，已付租金，未扣费用，未付租金
			 */
			sql = "SELECT a.houseGuid,SUM(a.kf) kf,SUM(a.zj) yfzj FROM fwzjmx a,house b WHERE a.houseGuid=b.guid AND b.fwzt='1' AND a.fkzt='1' GROUP BY a.houseGuid";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next())
			{
				String guid = rs.getString("houseGuid");         //房屋Guid
				double kf = rs.getDouble("kf");                  //已扣费
				double yfzj = rs.getDouble("yfzj");              //已付租金

				HouseMonthFree free = (HouseMonthFree)map.get(guid);
				if(free==null){
					continue;
				}
				free.setYkfy(kf);                     //已扣费
				free.setYfzj(yfzj);                   //已付租金
				free.setWkfy(free.getZkf()-kf);       //未扣费用（总扣费-已扣费）
				free.setWfzj(free.getZzj()-yfzj);     //未付租金（总租金-已付租金）
				free.setYzj(free.getZzj()/(free.getYfs()==0?1:free.getYfs()));
			}
			
			/**
			 * 统计订单数量，订单金额，订单收益
			 */
			sql = "SELECT a.houseGuid,COUNT(*) dds,SUM(a.ffxj) ddje,SUM(b.ddsy) ddsy FROM fwddzb a,ddhs b WHERE a.guid=b.guid AND IFNULL(a.noshow,'0') <> '3' AND a.ddsx<> 'YZ' GROUP BY a.houseGuid";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next())
			{
				String guid = rs.getString("houseGuid");         //房屋Guid
				int ddsl = rs.getInt("dds");                     //订单数量
				double ddje = rs.getDouble("ddje");              //订单金额
				double ddsy = rs.getDouble("ddsy");              //订单收益

				HouseMonthFree free = (HouseMonthFree)map.get(guid);
				if(free==null){
					continue;
				}
				free.setDds(ddsl);
				free.setDdje(ddje);
				free.setDdsy(ddsy);
				free.setDdjj(ddje/(ddsl==0?1:ddsl));   //订单均价（总金额/订单数量）
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
		private String guid = null;   //房屋Guid
		private int yfs = 0;          //月份数
		private double yzj = 0;       //月租金（总租金/总月份）
		private double zzj = 0;       //总租金
		private double zkf = 0;       //总扣费
		private double ykfy = 0;      //已扣费用
		private double yfzj = 0;      //已付租金
		private double wkfy = 0;      //未扣费用
		private double wfzj = 0;      //未付租金
		private int dds = 0;          //订单数
		private double ddje = 0;      //订单金额
		private double ddjj = 0;      //订单均价
		private double ddsy = 0;      //订单收益
		
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
