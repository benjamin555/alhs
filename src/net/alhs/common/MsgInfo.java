package net.alhs.common;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import net.alhs.api.HouseBean;
import net.chysoft.common.ShutCutFactory;
import net.sysmain.common.ConnectionManager;

/**
 * 发送订单相关短信
 * @author Administrator
 *
 */
public class MsgInfo{

	/**
	 * 发送订单短信（已订 预定 锁定）
	 * @param ddGuid
	 * @throws Exception
	 */
	public static void sendOrderMsg(String ddGuid) throws Exception {
		Connection conn = null; 
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select houseGuid,status,sqdh,xm,lxdh,rzrq,tfrq,rzts,zdqxsj_xs,zdqxsj_fz,ffxj from fwddzb where guid=?";
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, ddGuid);
			rs = ps.executeQuery();
			if(rs.next()){
				String houseGuid = rs.getString("houseGuid");
				String ddzt = rs.getString("status");                                          //订单状态（4：已订 5：预定 6：锁定）
				String sqdh = rs.getString("sqdh");                                            //单号
				String xm = rs.getString("xm");                                                //业主姓名
				String lxdh = rs.getString("lxdh");                                            //联系电话
				String begindate = rs.getString("rzrq");                                       //入住日期
				String enddate = rs.getString("tfrq");                                         //退房日期
				String rzts = rs.getString("rzts");                                            //天数
				String zdqxsj_xs = rs.getString("zdqxsj_xs");
				String zdqxsj_fz = rs.getString("zdqxsj_fz"); 
				double zje = rs.getDouble("ffxj");                                             //总金额

				HouseBean house = getHouseInfo(houseGuid);                                     //房间信息

				/**
				 * 发送短信(已订 预定 锁定)
				 */
				int id = 8;         //短信模版ID 
				String [] values  = null;
				if(ddzt.equals("4")){
					id = 7;
					values = new String []{xm,sqdh,house.getHomeaddress(),house.getFangxing(),begindate,enddate,rzts,zje+"",house.getQtmc(),house.getJtjs()};
				}
				else if(ddzt.equals("5") || ddzt.equals("6")){
					String ydOrSd = ddzt.equals("5")?"预定":"锁定";
					String qxsj = begindate+" "+zdqxsj_xs+":"+zdqxsj_fz;
					values = new String []{xm,ydOrSd,sqdh,house.getFjjs(),house.getFangxing(),begindate,enddate,rzts,zje+"",ydOrSd,qxsj,house.getQtmc(),house.getJtjs()};
				}

				if(values != null && values.length>0){
					if(lxdh!=null && !lxdh.trim().equals("")){    //电话号码为空不发送
						String content = MsgTemplate.getContent(id, values);
						new ShutCutFactory().sendMobileMessage(new String[]{lxdh.trim()}, content, -1, null, null);
					}
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			ConnectionManager.close(conn);
		}
	}
	/**
	 * 发送订单地址短信
	 * @param ddGuid
	 * @throws Exception
	 */
	public static void sendOrderAddressMsg(String ddGuid) throws Exception {
		Connection conn = null; 
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select houseGuid,xm,lxdh from fwddzb where guid=?";
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, ddGuid);
			rs = ps.executeQuery();
			if(rs.next()){
				String houseGuid = rs.getString("houseGuid");
				String xm = rs.getString("xm");                                                //业主姓名
				String lxdh = rs.getString("lxdh");                                            //联系电话

				HouseBean house = getHouseInfo(houseGuid);                                     //房间信息
				/**
				 *  发送短信(地址)
				 */
				int msgId = 13;      //短信模版ID 
				String [] vs = new String[]{xm,house.getQtmc(),house.getJtjs()};
				if(house.getQtmc().indexOf("碧桂园")!=-1){
					msgId = 14;
				}
				else if(house.getQtmc().indexOf("双月湾")!=-1){
					msgId = 15;
				}

				if(lxdh!=null && !lxdh.trim().equals("")){    //电话号码为空不发送
					String content = MsgTemplate.getContent(msgId, vs);
					new ShutCutFactory().sendMobileMessage(new String[]{lxdh.trim()}, content, -1, null, null);
				}

			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			ConnectionManager.close(conn);
		}
	}
	/**
	 * 获取与短信内容中的房屋信息
	 * @param houseGuid
	 * @return
	 * @throws Exception
	 */
	public static HouseBean getHouseInfo(String houseGuid) throws Exception {
		Connection conn = null; 
		PreparedStatement ps = null;
		String sql = "SELECT CONCAT(h.sf,h.cs,h.qu,IFNULL(h.jd,''),IFNULL(h.xqhy,'')) simpleAddress,xxdz,fangxing,q.mc,q.dh1 FROM house h,qtjzx q WHERE h.qtGuid = q.guid AND h.guid=?";
		HouseBean house = new HouseBean();
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, houseGuid);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				house.setHomeaddress(rs.getString("xxdz"));    //房屋详细地址
				house.setFjjs(rs.getString("simpleAddress"));  //房屋简单地址
				house.setFangxing(rs.getString("fangxing"));   //房型
				house.setQtmc(rs.getString("mc"));             //前台名
				house.setJtjs(rs.getString("dh1"));            //前台电话
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			ConnectionManager.close(conn);
		}

		return house;
	}
}
