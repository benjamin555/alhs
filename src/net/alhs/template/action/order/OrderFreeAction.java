package net.alhs.template.action.order;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.alhs.template.yezhu.InsertYzsy;
import net.sysmain.common.ConnectionManager;
/**
 * 订单相关费用计算
 * @author Administrator
 *
 */
public class OrderFreeAction{

	/**
	 * 计算订单相关费用信息（佣金，提成，分成，收益等）
	 * @param context
	 * @throws Exception
	 */
	public static void initOrderFree(String ddGuid) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try{
			conn = ConnectionManager.getInstance().getConnection();
			
			/*
			 * 先删除，再重新计算（修改平台相关信息，该平台下所有未处理的佣金重新计算）
			 */
			ps = conn.prepareStatement("delete from ddhs where guid='"+ddGuid+"'");
			ps.executeUpdate();
			
			/**
			 * 订单相关费用
			 */
			ps = conn.prepareStatement("select a.ywyid,a.ddsx,a.ffyj,a.jff,a.ffxj,b.fcbl,b.qtGuid,b.htms from fwddzb a,house b where a.houseGuid=b.guid and a.guid=? ");
			ps.setString(1, ddGuid);
			rs = ps.executeQuery();
			if(rs.next()){
				String ywyid = rs.getString("ywyid");             //业务员id
				String ddsx = rs.getString("ddsx");               //订单属性
				double ffyj = rs.getDouble("ffyj");               //房费原价（按日历价格）
				double jff = rs.getDouble("jff");                 //净房费  （确认房价）
				double ffxj = rs.getDouble("ffxj");               //房费小计（减去优惠和折扣）
				double fcbl = rs.getDouble("b.fcbl");             //房屋的分成比率
				String qtGuid = rs.getString("b.qtGuid");         //前台Guid
				String htms = rs.getString("b.htms");             //合同模式

				/**
				 * 查询业务提成系数
				 */
				//double ywtcxs = 0;           //业务提成系数
				//if(ywyid!=null && !ywyid.trim().equals("")){
					//rs = conn.createStatement().executeQuery("select ywtcxs from employee_archive where LoginId='"+ywyid+"'");
					//if(rs.next()){
						//ywtcxs = rs.getDouble("ywtcxs");
					//}
				//}
				
				/**
				 * 查询佣金比率，惠选佣金比例
				 */
				String ptxxGuid = null;       //平台信息Guid
				double yjbl = 0;              //佣金比率
				double hxyl = 0;              //惠选佣率
				String sffy = "";             //是否要付佣
				String sql = "select guid,yjl,hxyl,sffy from ptxx where qtGuid=? and ptmc=?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, qtGuid);
				ps.setString(2, ddsx);
				rs = ps.executeQuery();
				if(rs.next()){
					ptxxGuid = rs.getString("guid");
					yjbl = rs.getDouble("yjl");
					hxyl = rs.getDouble("hxyl");
					sffy = rs.getString("sffy");
				}

				double ptyj = jff*(yjbl/100);                    //平台佣金
				double hxyj = jff*(hxyl/100);                    //惠选佣金
				double syxj = ffxj-ptyj-hxyj;                    //收益小计（房费小计-佣金-惠选佣金）
				
				double syb = syxj/(ffyj==0?1:ffyj)*100;          //收益比（收益小计/房费原价*100）
				double fcje = syxj*(fcbl/100);                   //分成金额
				//double ywtcje = syxj*(ywtcxs/100);               //业务提成金额
				//double ddsy = syxj-fcje-ywtcje;                  //订单收益

				sql = "insert into ddhs(guid,ptxxGuid,yjl,ptyj,hxyl,hxyj,fcbl,fcje,ywtcxs,ywtcje,ddsy,syxj,syb,qrptyj,qrhxyj,sffy_pt,sffy_hx) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, ddGuid);
				ps.setString(2, ptxxGuid);
				ps.setDouble(3, yjbl);
				ps.setDouble(4, ptyj);
				ps.setDouble(5, hxyl);
				ps.setDouble(6, hxyj);
				ps.setDouble(7, fcbl);
				ps.setDouble(8, fcje);
				ps.setDouble(9, 0);
				ps.setDouble(10,0);
				ps.setDouble(11,0);
				ps.setDouble(12, syxj);
				ps.setDouble(13, syb);
				ps.setDouble(14, ptyj);
				ps.setDouble(15, hxyj);
				ps.setString(16, sffy.equals("1")?"0":"2");    //平台信息是否付佣=0（不付），则订单付佣状态=2（完成付佣）
				ps.setString(17, sffy.equals("1")?"0":"2");

				ps.executeUpdate();
				
				/**
				 * 奖金计算
				 */
				calcOrderBonus(ddGuid);
				
				/**
				 * 分成金额入业主账户（分成房）
				 */
				if(htms.equals("2")){
					InsertYzsy iy = new InsertYzsy();
					iy.addOrderFencheng(ddGuid);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
	}
	/**
	 * 计算奖金
	 * @param ddGuid
	 * @throws Exception
	 */
	public static void calcOrderBonus(String ddGuid) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try{
			conn = ConnectionManager.getInstance().getConnection();
			String sql = "select d.ddsx,h.qtGuid,s.syb,s.syxj from fwddzb d,house h,ddhs s where d.houseGuid=h.guid and d.guid=s.guid and d.guid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, ddGuid);
			rs = ps.executeQuery();
			if(rs.next()){
				String ddsx = rs.getString("ddsx");      //平台名称
				String qtGuid = rs.getString("qtGuid");  //酒店
				
				double syb = rs.getDouble("syb");        //订单的收益比
				double syxj = rs.getDouble("syxj");      //收益小计
				
				double tcbl = 0.00;                      //提成比率
				
				sql = "select * from tcmbmx where zbGuid = (select tcmbGuid from ptxx where qtGuid=? and ptmc=?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, qtGuid);
				ps.setString(2, ddsx);
				rs = ps.executeQuery();
				while(rs.next()){
					double syb1 = rs.getDouble("syb1");
					double syb2 = rs.getDouble("syb2");
					double bl = rs.getDouble("tcbl");
					
					if(syb1<syb && syb<=syb2){
				        tcbl = bl;
				        break;
					}
				}
				
				double bonus = syxj*(tcbl/100);         //奖金 
				
				/**
				 * 计算订单的提成比率，奖金，订单收益
				 */
				sql ="update ddhs set ywtcxs=?,ywtcje=?,ddsy=syxj-fcje-? where guid=?";
				ps = conn.prepareStatement(sql);
				ps.setDouble(1, tcbl);
				ps.setDouble(2, bonus);
				ps.setDouble(3, bonus);
				ps.setString(4, ddGuid);
				ps.executeUpdate();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
	}


	public String getErrorMessage() {
		return null;
	}
}
