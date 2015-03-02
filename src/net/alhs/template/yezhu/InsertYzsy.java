package net.alhs.template.yezhu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import net.alhs.common.MsgTemplate;
import net.chysoft.common.ShutCutFactory;
import net.sysmain.common.ConnectionManager;
import net.sysmain.common.FormLogger;
import net.sysmain.util.GUID;
import net.sysmain.util.StringTools;

/**
 * 产生业主收益记录
 * @author Administrator
 *
 */
public class InsertYzsy{
	/**
	 * 其他费用
	 * @param zjmxGuid
	 * @param fylx
	 * @throws Exception
	 */
	public void insertFreeToYzsy (String zjmxGuid,String fylx) throws Exception {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement ps =null;
		ResultSet rs = null;
		try{
			String sql = "select a.*,c.name,c.userName,ifnull(c.money,0) as money,c.MobilePhone,d.houseGuid from fwqtfy a,house b,yzzl c,fwzjmx d where a.guid=d.guid and d.houseGuid=b.guid and b.yzGuid=c.guid and a.guid='"+zjmxGuid+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				String guid = new GUID().toString();
				String houseGuid = rs.getString("d.houseGuid");        //关联的Guid：房屋GUID
				String yzLoginId = rs.getString("c.userName");
				String yzName = rs.getString("c.name");
				String fksj = StringTools.getCurrentDateTime();
				double sr = rs.getDouble(fylx);                        //金额


				/**
				 * 修改付款时间，付款状态
				 */
				sql = "update fwqtfy set fkzt_"+fylx+"='1',fksj_"+fylx+"=? where guid=?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, fksj);
				ps.setString(2, zjmxGuid);
				ps.executeUpdate();


				if(sr>0){
					double jyje = rs.getDouble("money");                 //业主总结余金额

					YzsyBean bean = new YzsyBean();
					bean.setGuid(guid);
					bean.setGlGuid(houseGuid);
					bean.setYzLoginId(yzLoginId);
					bean.setYzName(yzName);
					bean.setRq(fksj);
					bean.setZfr("艾丽豪斯");
					bean.setZc(0);
					bean.setSr(sr);
					bean.setJy(jyje+sr);  //结余=总结余+房屋租金

					String km = "";
					if(fylx.equals("glf")){
						km = "4"; 
					}
					else if(fylx.equals("sf")){
						km = "5";
					}
					else if(fylx.equals("df")){
						km = "6";
					}
					else if(fylx.equals("rqf")){
						km = "7";
					}
					else if(fylx.equals("wlf")){
						km = "8";
					}
					else if(fylx.equals("dsf")){
						km = "9";
					}
					else if(fylx.equals("wxf")){
						km = "10";
					}
					else if(fylx.equals("qtf")){
						km = "11";
					}
					bean.setKm(km);
					bean.setFj_path("");
					bean.setDdGuid("");

					addRecode(bean);


					/**
					 * 发送短信
					 */

				}
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
	 * 增加业主收益（房屋租金）
	 * @param zjmxGuid
	 * @throws Exception
	 */
	public void insertZjToYzsy (String zjmxGuid) throws Exception {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement ps =null;
		ResultSet rs = null;
		try{
			String sql = "select a.*,c.name,c.userName,ifnull(c.money,0) as money,c.MobilePhone from fwzjmx a,house b,yzzl c where a.houseGuid=b.guid and b.yzGuid=c.guid and a.guid='"+zjmxGuid+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				String guid = new GUID().toString();
				String houseGuid = rs.getString("a.houseGuid");        //关联的Guid：房屋GUID
				String yzLoginId = rs.getString("c.userName");
				String yzName = rs.getString("c.name");
				String fksj = rs.getString("a.fksj");
				double sr = rs.getDouble("a.zjfk");                    //支付金额

				if(sr>0){

					double jyje = rs.getDouble("money");                 //业主总结余金额

					YzsyBean bean = new YzsyBean();
					bean.setGuid(guid);
					bean.setGlGuid(houseGuid);
					bean.setYzLoginId(yzLoginId);
					bean.setYzName(yzName);
					bean.setRq(fksj);
					bean.setZfr("艾丽豪斯");
					bean.setZc(0);
					bean.setSr(sr);
					bean.setJy(jyje+sr);  //结余=总结余+房屋租金
					bean.setKm("1");
					bean.setFj_path("");
					bean.setDdGuid("");

					addRecode(bean);

					/**
					 * 发送短信
					 */
					String zj = rs.getString("a.zj");               //租金
					String kf = rs.getString("a.kf");               //扣费
					String nyf = rs.getString("a.nyf");             //年月份
					String name = rs.getString("c.name");           //业主姓名
					String yzsj = rs.getString("c.MobilePhone");    //手机
					int id = 3;                                     //短信模版ID

					String [] values = new String []{name,nyf,zj,kf,sr+"",StringTools.getCurrentDateTime()};
					String content = MsgTemplate.getContent(id, values);

					new ShutCutFactory().sendMobileMessage(new String[]{yzsj}, content, -1, null, null);
					
					FormLogger.log("zjTime","增加业主收益，并发送短信成功！");
				}
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
	 * 增加业主收益（订单分成）
	 * @param ddGuid
	 * @throws Exception
	 */
	public void addOrderFencheng(String ddGuid) throws Exception {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement ps =null;
		ResultSet rs = null;
		try{
			String sql = "SELECT a.rzrq,a.ffxj,b.guid,c.name,c.userName,c.MobilePhone,ifnull(c.money,0) as money,d.fcje,d.ptyj,d.hxyj  FROM fwddzb a,house b,yzzl c,ddhs d WHERE a.houseGuid=b.guid AND b.yzGuid=c.guid AND a.guid=d.guid AND a.guid='"+ddGuid+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			if(rs.next()){
				String guid = new GUID().toString();
				String houseGuid = rs.getString("b.guid");             //房间Guid
				String yzLoginId = rs.getString("c.userName");
				String yzName = rs.getString("c.name");
				double fcje = rs.getDouble("d.fcje");                  //分成金额

				if(fcje>0){

					double jyje = rs.getDouble("money");                 //业主总结余金额

					YzsyBean bean = new YzsyBean();
					bean.setGuid(guid);
					bean.setGlGuid(houseGuid);
					bean.setYzLoginId(yzLoginId);
					bean.setYzName(yzName);
					bean.setRq(StringTools.getCurrentDateTime());
					bean.setZfr("艾丽豪斯");
					bean.setZc(0);
					bean.setSr(fcje);
					bean.setJy(jyje+fcje);  //结余=总结余+本次分成金额
					bean.setKm("2");
					bean.setFj_path("");
					bean.setDdGuid(ddGuid);

					addRecode(bean);

					/**
					 * 发送短信
					 */
					String yzsj = rs.getString("c.MobilePhone");                    //手机
					String rq = rs.getString("a.rzrq");                             //入住日期
					double ffxj = rs.getDouble("a.ffxj");                           //房费小计（减去优惠和折扣）
					double qtfy = rs.getDouble("d.ptyj")+rs.getDouble("d.hxyj");    //其它费用（平台佣金+惠选佣金）
					int id = 5;                                                     //短信模版ID
					String [] values = new String []{yzName,rq,ffxj+"",qtfy+"",fcje+"",jyje+""};
					String content = MsgTemplate.getContent(id, values);

					new ShutCutFactory().sendMobileMessage(new String[]{yzsj}, content, -1, null, null);
				}
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
	 * 增加业主收益（提现申请）
	 * @param txsqGuid
	 * @throws Exception
	 */
	public void insertTxsqToYzsy (String txsqGuid) throws Exception {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement ps =null;
		ResultSet rs = null;

		String sql = "select * from txsq where guid='"+txsqGuid+"'";
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();

		if(rs.next()){
			String guid = new GUID().toString();
			String yzLoginId = rs.getString("sqrguid");
			String yzName = rs.getString("sqr");
			String fksj = rs.getString("sqsj");
			double sr = rs.getDouble("ntxe");                         //提现金额
			String fj_path = rs.getString("cnfj");                    //附件路径

			double jyje = getMoney(yzLoginId);                        //业主总结余金额

			YzsyBean bean = new YzsyBean();
			bean.setGuid(guid);
			bean.setGlGuid(txsqGuid);
			bean.setYzLoginId(yzLoginId);
			bean.setYzName(yzName);
			bean.setRq(fksj);
			bean.setZfr("艾丽豪斯");
			bean.setZc(sr);
			bean.setSr(0);
			bean.setJy(jyje-sr);  //结余=总结余-提现金额
			bean.setKm("3");
			bean.setFj_path(fj_path);
			bean.setDdGuid("");

			addRecode(bean);
		}
	}

	/**
	 * 查询业主总收益
	 * @param loginId
	 * @return
	 * @throws Exception
	 */
	public double getMoney (String loginId) throws Exception {
		double money = 0;
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement ps =null;
		ResultSet rs = null;

		String sql = "select money from yzzl where userName=?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, loginId);
		rs = ps.executeQuery();

		if(rs.next()){
			money = rs.getDouble("money");
		}

		return money;
	}
	private void addRecode(YzsyBean bean) throws Exception {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement ps =null;

		try{
			String insertSql = "insert into yzsy (guid,glGuid,yzLoginId,yzName,rq,km,zfr,sr,zc,jy,pzfj,ddGuid) values(?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(insertSql);

			/**
			 * 添加业主收益记录
			 */
			ps.setString(1, bean.getGuid());
			ps.setString(2, bean.getGlGuid());
			ps.setString(3, bean.getYzLoginId());
			ps.setString(4, bean.getYzName());
			ps.setString(5, bean.getRq());
			ps.setString(6, bean.getKm());
			ps.setString(7, bean.getZfr());
			ps.setDouble(8, bean.getSr());          
			ps.setDouble(9, bean.getZc());          
			ps.setDouble(10, bean.getJy());    
			ps.setString(11, bean.getFj_path());
			ps.setString(12, bean.getDdGuid());    //订单Guid（订单收益才有）
			ps.executeUpdate();


			/**
			 * 修改业主的总结余
			 */

			conn.createStatement().executeUpdate("update yzzl set money="+bean.getJy()+" where userName='"+bean.getYzLoginId()+"'");

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
	class YzsyBean{
		private String guid;
		private String glGuid;
		private String yzLoginId;
		private String yzName;
		private String rq;
		private String km;
		private String zfr;
		private double sr;
		private double zc;
		private double jy;
		private String fj_path;
		private String ddGuid;

		public String getDdGuid() {
			return ddGuid;
		}
		public void setDdGuid(String ddGuid) {
			this.ddGuid = ddGuid;
		}
		public String getFj_path() {
			return fj_path;
		}
		public void setFj_path(String fj_path) {
			this.fj_path = fj_path;
		}
		public String getGlGuid() {
			return glGuid;
		}
		public void setGlGuid(String glGuid) {
			this.glGuid = glGuid;
		}
		public String getGuid() {
			return guid;
		}
		public void setGuid(String guid) {
			this.guid = guid;
		}
		public double getJy() {
			return jy;
		}
		public void setJy(double jy) {
			this.jy = jy;
		}
		public String getKm() {
			return km;
		}
		public void setKm(String km) {
			this.km = km;
		}
		public String getRq() {
			return rq;
		}
		public void setRq(String rq) {
			this.rq = rq;
		}
		public double getSr() {
			return sr;
		}
		public void setSr(double sr) {
			this.sr = sr;
		}
		public String getYzLoginId() {
			return yzLoginId;
		}
		public void setYzLoginId(String yzLoginId) {
			this.yzLoginId = yzLoginId;
		}
		public String getYzName() {
			return yzName;
		}
		public void setYzName(String yzName) {
			this.yzName = yzName;
		}
		public double getZc() {
			return zc;
		}
		public void setZc(double zc) {
			this.zc = zc;
		}
		public String getZfr() {
			return zfr;
		}
		public void setZfr(String zfr) {
			this.zfr = zfr;
		}
	}
}
