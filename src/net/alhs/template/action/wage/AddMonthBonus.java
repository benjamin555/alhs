package net.alhs.template.action.wage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import net.sysmain.common.ConnectionManager;
import net.sysmain.util.GUID;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;
/**
 * 生成月奖金
 * @author Administrator
 *
 */
public class AddMonthBonus{

	/**
	 * 生成月介绍奖
	 * @param context
	 * @throws Exception
	 */

	private static String zbGuid = null;
	private static String date = null;
	private static String bh = null;
	private static String bonusType = null;

	public static void addMonthBonus(HttpServletRequest request) throws Exception{
		/**
		 * 初始化
		 */
		zbGuid = request.getParameter("zbGuid");          //奖金主表Guid	
		bh = request.getParameter("bh");                  //奖金的编号
		date = request.getParameter("date");              //年月份
		bonusType = request.getParameter("jjlx");         //奖金类型

		if(bonusType.equals("jsj") || bonusType.equals("xsj")){
			addBonus1(request);
		}
		else if(bonusType.equals("zlj") || bonusType.equals("dxj")){
			addBonus2(request);
		}
		else if(bonusType.equals("klj") || bonusType.equals("wxj") || bonusType.equals("zxj")){
			addBonus3(request);
		}
		else if(bonusType.equals("qjj")){
			addQingjieBonus(request);
		}
	}
	/**
	 * 介绍奖，销售奖：按个人发分配，介绍奖是订单来源为介绍来客的所有关联业务的（可选择所有人），销售奖为出去介绍奖部分，关联业务只能选择业务员
	 * @param request
	 * @throws Exception
	 */
	public static void addBonus1(HttpServletRequest request) throws Exception {
		Operator operator = Authentication.getUserFromSession(request);

		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps0 = null;
		ResultSet rs = null;

		String newGuid = new GUID().toString();

		try{
			conn = ConnectionManager.getInstance().getConnection();
			/*
			 * 所有已入住，介绍来客订单
			 */

			int count = 0;
			double ddsyTotal = 0.00;
			double bonusTotal = 0.00;

			String sql0 = "insert into jjyb(guid,zbGuid,gzlzt,sqr,sqrguid,sqsj,nian,yue,bh,jjlx,dds,ddsyze,jjze) values(?,?,'0',?,?,now(),?,?,?,?,?,?,?)";
			ps0 = conn.prepareStatement(sql0);

			Statement stat = conn.createStatement();

			/**
			 * 介绍奖的订单来源为介绍来客，销售奖为去除介绍来客部分
			 */
			String ddly = bonusType.equals("jsj")?"a.ddsx='JS'":"a.ddsx <> 'JS'";

			String sql = "select ywyid,ywy,COUNT(*) dds,SUM(syxj) ddsy,SUM(ywtcje) bonus from fwddzb a,ddhs b where a.guid=b.guid and a.ddzt>'1' and a.zcqx is null and "+ddly+" and SUBSTRING(a.rzrq,1,7)=? group by ywyid having ywyid is not null";
			ps = conn.prepareStatement(sql);
			ps.setString(1, date);
			rs = ps.executeQuery();
			while(rs.next()){
				String ywyid = rs.getString("ywyid");
				String ywy = rs.getString("ywy");
				int dds = rs.getInt("dds");
				double ddsy = rs.getDouble("ddsy");
				double bonus = rs.getDouble("bonus");

				count+=dds;
				ddsyTotal+=ddsy;
				bonusTotal+=bonus;

				stat.addBatch("insert into jjybmx(guid,zbGuid,loginId,ygxm,dds,ddsy,jiangjin) values('"+new GUID().toString()+"','"+newGuid+"','"+ywyid+"','"+ywy+"',"+dds+","+ddsy+","+bonus+")");
			}

			ps0.setString(1, newGuid);
			ps0.setString(2, zbGuid);
			ps0.setString(3, operator.getName());
			ps0.setString(4, operator.getLoginId());
			ps0.setString(5, date.substring(0, 4));
			ps0.setString(6, date.substring(5));
			ps0.setString(7, bh+"_"+bonusType.toUpperCase().substring(0, 2));  //编号为奖金编号+类型
			ps0.setString(8, bonusType);
			ps0.setInt(9, count);
			ps0.setDouble(10, ddsyTotal);
			ps0.setDouble(11,bonusTotal);

			ps0.executeUpdate();
			stat.executeBatch();

		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
	}
	/**
	 * 自来奖：按奖金累加，再按比率分成（个人系数/总系数）    自来奖：自来客订单
	 * 店销奖：按订单收益累加，再按比率分成（个人系数/总系数）
	 * @param request
	 * @throws Exception
	 */
	public static void addBonus2(HttpServletRequest request) throws Exception {
		Operator operator = Authentication.getUserFromSession(request);

		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps0 = null;
		ResultSet rs = null;
		ResultSet rs0 = null;

		String newGuid = new GUID().toString();

		try{
			conn = ConnectionManager.getInstance().getConnection();

			int count = 0;                  //总订单数
			double ddsyTotal = 0.00;        //总订单收益
			double bonusTotal = 0.00;       //总奖金

			String sql0 = "insert into jjyb(guid,zbGuid,gzlzt,sqr,sqrguid,sqsj,nian,yue,bh,jjlx,dds,ddsyze,jjze) values(?,?,'0',?,?,now(),?,?,?,?,?,?,?)";
			ps0 = conn.prepareStatement(sql0);

			Statement stat = conn.createStatement();
			Statement stat0 = conn.createStatement();

			/**
			 * 订单来源：自来奖（自来客-奖金累加）店销奖(除去自来客订单-订单收益累加)
			 */
			String ddly = bonusType.equals("zlj")?" b.ddsx='ZL'":" b.ddsx <> 'ZL'";

			String sql = "select a.qtGuid,COUNT(*) dds ,SUM(c.syxj) ddsy,SUM(c.ywtcje) bonus from house a,fwddzb b,ddhs c where b.houseGuid=a.guid and b.guid=c.guid and b.ddzt>'1' and b.zcqx is null and "+ddly+" and SUBSTRING(b.rzrq,1,7)=? group by a.qtGuid";

			ps = conn.prepareStatement(sql);
			ps.setString(1, date);
			rs = ps.executeQuery();
			while(rs.next()){
				String qtGuid = rs.getString("qtGuid");
				int dds = rs.getInt("dds");
				double ddsy = rs.getDouble("ddsy");
				double bonus = rs.getDouble("bonus");            //奖金（自来奖）

				double jjl = 0.00;                               //奖金率                        

				/**
				 * 店销奖
				 */
				if(bonusType.equals("dxj")){
					jjl = 0.005;                                 //酒店奖金率(默认，之后可以设定)--店销奖
					bonus = ddsy*jjl;                            //酒店总奖金(按订单收益累加，乘以奖金率)--店销奖
				}
				
				String jjyb_jiudian_guid = new GUID().toString();
				/**
				 * 奖金月表_酒店
				 */
				stat0.addBatch("insert into jjyb_jiudian(guid,zbGuid,dds,ddsy,jiangjin,qtGuid,jjl) values('"+jjyb_jiudian_guid+"','"+newGuid+"',"+dds+","+ddsy+","+bonus+",'"+qtGuid+"',"+jjl+")");

				double zxs = 0.00;      //奖金的酒店总系数
				sql = "select sum("+bonusType+"xs) xs from employee_archive where "+bonusType+"='1' and qtGuid='"+qtGuid+"'";
				rs0 = conn.createStatement().executeQuery(sql);
				if(rs0.next()){
					zxs = rs0.getDouble("xs");
				}
				
				//有此奖项的员工（按酒店分）
				sql = "select name,loginId,"+bonusType+"xs jjxs from employee_archive where "+bonusType+"='1' and qtGuid='"+qtGuid+"'";
				rs0 = conn.createStatement().executeQuery(sql);
				while(rs0.next()){
					String name = rs0.getString("name");
					String loginId = rs0.getString("loginId");
					double jjxs = rs0.getDouble("jjxs");

					double jiangjin = jjxs/(zxs==0?1:zxs)*bonus;  //个人奖金（系数/酒店员工总系数(累计)*奖金）

					/**
					 * 奖金月表明细_个人
					 */
					stat.addBatch("insert into jjybmx(guid,zbGuid,jjyb_jd_Guid,loginId,ygxm,dds,ddsy,jiangjin) values('"+new GUID().toString()+"','"+newGuid+"','"+jjyb_jiudian_guid+"','"+loginId+"','"+name+"',"+dds+","+ddsy+","+jiangjin+")");
				}
				count+=dds;
				ddsyTotal+=ddsy;
				bonusTotal+=bonus;
			}

			ps0.setString(1, newGuid);
			ps0.setString(2, zbGuid);
			ps0.setString(3, operator.getName());
			ps0.setString(4, operator.getLoginId());
			ps0.setString(5, date.substring(0, 4));
			ps0.setString(6, date.substring(5));
			ps0.setString(7, bh+"_"+bonusType.toUpperCase().substring(0, 2));  //编号为奖金编号+类型
			ps0.setString(8, bonusType);
			ps0.setInt(9, count);
			ps0.setDouble(10, ddsyTotal);
			ps0.setDouble(11,bonusTotal);

			ps0.executeUpdate();
			stat0.executeBatch();
			stat.executeBatch();

		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
	}

	/**
	 * 客来奖：按奖金累加，再按比率分成（个人系数/总系数） 客来：客服订单
	 * 网销奖，总销奖：按订单收益累加，乘以奖金率，再按比率分成（个人系数/总系数） 网销奖：家游网
	 * @param request
	 * @throws Exception
	 */
	public static void addBonus3(HttpServletRequest request) throws Exception {
		Operator operator = Authentication.getUserFromSession(request);

		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps0 = null;
		ResultSet rs = null;
		ResultSet rs0 = null;

		String newGuid = new GUID().toString();

		try{
			conn = ConnectionManager.getInstance().getConnection();

			int count = 0;                  //总订单数
			double ddsyTotal = 0.00;        //总订单收益
			double bonusTotal = 0.00;       //总奖金

			String sql0 = "insert into jjyb(guid,zbGuid,gzlzt,sqr,sqrguid,sqsj,nian,yue,bh,jjlx,dds,ddsyze,jjze) values(?,?,'0',?,?,now(),?,?,?,?,?,?,?)";
			ps0 = conn.prepareStatement(sql0);

			Statement stat = conn.createStatement();
			Statement stat0 = conn.createStatement();

       		String ddly = "";                     //总销奖：所有
			if(bonusType.equals("klj")){
				ddly = "and b.ddsx='KF'";         //客来奖：客服订单
			}
			else if(bonusType.equals("wxj")){
				ddly = "and b.ddsx='HT'";         //网销奖：家游网
			}
			String sql = "select COUNT(*) dds ,SUM(c.syxj) ddsy,SUM(c.ywtcje) bonus from house a,fwddzb b,ddhs c where b.houseGuid=a.guid and b.guid=c.guid and b.ddzt>'1' and b.zcqx is null "+ddly+" and SUBSTRING(b.rzrq,1,7)=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, date);
			rs = ps.executeQuery();
			while(rs.next()){
				int dds = rs.getInt("dds");                 //订单数
				double ddsy = rs.getDouble("ddsy");         //月收益

				double jjl = 0.005;                         //酒店奖金率(默认，之后可以设定)--网销奖，总销奖
				double bonus = ddsy*jjl;                    //酒店总奖金(按订单收益累加，乘以奖金率)--网销奖，总销奖
				
				if(bonusType.equals("klj")){                //客来奖：按奖金累加，无奖金率
					bonus = rs.getDouble("bonus");
					jjl = 0;
				}

				String jjyb_jiudian_guid = new GUID().toString();
				/**
				 * 奖金月表_酒店
				 */
				stat0.addBatch("insert into jjyb_jiudian(guid,zbGuid,dds,ddsy,jiangjin,jjl) values('"+jjyb_jiudian_guid+"','"+newGuid+"',"+dds+","+ddsy+","+bonus+","+jjl+")");


				double zxs = 0.00;         //奖金的酒店总系数
				sql = "select sum("+bonusType+"xs) xs from employee_archive where "+bonusType+"='1'";
				rs0 = conn.createStatement().executeQuery(sql);
				if(rs0.next()){
					zxs = rs0.getDouble("xs");
				}
				//有该奖项的员工
				sql = "select name,loginId,"+bonusType+"xs jjxs from employee_archive where "+bonusType+"='1'";
				rs0 = conn.createStatement().executeQuery(sql);
				while(rs0.next()){
					String name = rs0.getString("name");
					String loginId = rs0.getString("loginId");
					double jjxs = rs0.getDouble("jjxs");

					double jiangjin = jjxs/(zxs==0?1:zxs)*bonus;  //个人奖金（系数/酒店员工总系数(累计)*奖金）

					/**
					 * 奖金月表明细_个人
					 */
					stat.addBatch("insert into jjybmx(guid,zbGuid,jjyb_jd_Guid,loginId,ygxm,dds,ddsy,jiangjin) values('"+new GUID().toString()+"','"+newGuid+"','"+jjyb_jiudian_guid+"','"+loginId+"','"+name+"',"+dds+","+ddsy+","+jiangjin+")");
				}
				count+=dds;
				ddsyTotal+=ddsy;
				bonusTotal+=bonus;
			}

			ps0.setString(1, newGuid);
			ps0.setString(2, zbGuid);
			ps0.setString(3, operator.getName());
			ps0.setString(4, operator.getLoginId());
			ps0.setString(5, date.substring(0, 4));
			ps0.setString(6, date.substring(5));
			ps0.setString(7, bh+"_"+bonusType.toUpperCase().substring(0, 2));  //编号为奖金编号+类型
			ps0.setString(8, bonusType);
			ps0.setInt(9, count);
			ps0.setDouble(10, ddsyTotal);
			ps0.setDouble(11,bonusTotal);

			ps0.executeUpdate();
			stat0.executeBatch();
			stat.executeBatch();

		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
	}
	/**
	 * 清洁奖
	 * @param request
	 * @throws Exception
	 */
	public static void addQingjieBonus(HttpServletRequest request) throws Exception {
		Operator operator = Authentication.getUserFromSession(request);

		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps0 = null;
		ResultSet rs = null;

		String newGuid = new GUID().toString();

		try{
			conn = ConnectionManager.getInstance().getConnection();

			double bonusTotal = 0.00;  //总奖金

			String sql0 = "insert into jjyb(guid,zbGuid,gzlzt,sqr,sqrguid,sqsj,nian,yue,bh,jjlx,jjze) values(?,?,'0',?,?,now(),?,?,?,?,?)";
			ps0 = conn.prepareStatement(sql0);

			Statement stat = conn.createStatement();

			/**
			 * 所有qljszt=0（未结算）订单(员工属性：正式工，临时工)
			 */
			String sql = "select ddzb.qlr as qlr,ddzb.qlrguid as qlrGuid,ea.ygsx as ygsx,IF(ea.ygsx='2',fxxx.qjf_zsg,fxxx.qjf_zdg) as qlf from fwddzb ddzb,house fwzb,fangxing fxxx,qtjzx qtzx,employee_archive ea  where ddzb.houseGuid=fwzb.guid and fwzb.fxGuid=fxxx.guid and fwzb.qtGuid=qtzx.guid and ddzb.qlrguid=ea.LoginId and ddzb.zcqx is null and ddzb.ddzt>'2' and ddzb.qljszt='0' and ea.ygsx>1 and SUBSTRING(ddzb.qlsj,1,7)=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, date);
			rs = ps.executeQuery();
			while(rs.next()){
				String ywyid = rs.getString("qlrguid");
				String ywy = rs.getString("qlr");
				double bonus = rs.getDouble("qlf");

				bonusTotal+=bonus;

				stat.addBatch("insert into jjybmx(guid,zbGuid,loginId,ygxm,jiangjin) values('"+new GUID().toString()+"','"+newGuid+"','"+ywyid+"','"+ywy+"',"+bonus+")");
			}

			ps0.setString(1, newGuid);
			ps0.setString(2, zbGuid);
			ps0.setString(3, operator.getName());
			ps0.setString(4, operator.getLoginId());
			ps0.setString(5, date.substring(0, 4));
			ps0.setString(6, date.substring(5));
			ps0.setString(7, bh+"_"+bonusType.toUpperCase().substring(0, 2));  //编号为奖金编号+类型
			ps0.setString(8, bonusType);
			ps0.setDouble(9, bonusTotal);

			ps0.executeUpdate();
			stat.executeBatch();

		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
	}
	
	/**
	 * 拓房奖
	 * @param request
	 * @throws Exception
	 */
	public static void addTuoFangBonus(HttpServletRequest request) throws Exception {
		Operator operator = Authentication.getUserFromSession(request);

		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps0 = null;
		ResultSet rs = null;

		String newGuid = new GUID().toString();

		try{
			conn = ConnectionManager.getInstance().getConnection();

			int fwsl = 0;              //房屋总数
			double bonusTotal = 0.00;  //总奖金

			String sql0 = "insert into jjyb(guid,zbGuid,gzlzt,sqr,sqrguid,sqsj,nian,yue,bh,jjlx,dds,jjze) values(?,?,'0',?,?,now(),?,?,?,?,?,?)";
			ps0 = conn.prepareStatement(sql0);

			Statement stat = conn.createStatement();

			/**
			 * 查询当月有拓房奖的（按合同日期退后2个月）
			 */
			String sql = "select COUNT(*) fwsl,SUM(tfjj) bonus,ywy,ywyid from house where tfjzq is not null and issale='1' and SUBSTRING(DATE_ADD(tfjzq,interval 2 month),1,7)=? group by ywyid";
			ps = conn.prepareStatement(sql);
			ps.setString(1, date);
			rs = ps.executeQuery();
			while(rs.next()){
			    int sl = rs.getInt("fwsl");
				String ywyid = rs.getString("ywyid");
				String ywy = rs.getString("ywy");
				double bonus = rs.getDouble("bonus");

				bonusTotal+=bonus;
				fwsl+=sl;

				stat.addBatch("insert into jjybmx(guid,zbGuid,loginId,ygxm,jiangjin) values('"+new GUID().toString()+"','"+newGuid+"','"+ywyid+"','"+ywy+"',"+bonus+")");
			}

			ps0.setString(1, newGuid);
			ps0.setString(2, zbGuid);
			ps0.setString(3, operator.getName());
			ps0.setString(4, operator.getLoginId());
			ps0.setString(5, date.substring(0, 4));
			ps0.setString(6, date.substring(5));
			ps0.setString(7, bh+"_"+bonusType.toUpperCase().substring(0, 2));  //编号为奖金编号+类型
			ps0.setString(8, bonusType);
			ps0.setInt(9, fwsl);
			ps0.setDouble(10, bonusTotal);

			ps0.executeUpdate();
			stat.executeBatch();

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
