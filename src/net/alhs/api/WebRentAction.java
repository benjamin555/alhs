package net.alhs.api;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Calendar;
import java.util.TimerTask;

import net.alhs.common.MsgTemplate;
import net.alhs.common.SendMobile;
import net.alhs.template.yezhu.InsertYzsy;
import net.chysoft.common.ShutCutFactory;
import net.sysmain.common.ConnectionManager;
import net.sysmain.common.FormLogger;
import net.sysmain.util.StringTools;
/**
 * 租金同步的接口类
 * @author Administrator
 *
 */
public class WebRentAction extends TimerTask
{

	/**
	 * 配置文件
	 */
	//private static String xml = "homtrip.xml";


	/**
	 * 抓取web的订单数据，存储OA表中
	 * @throws Exception
	 */

	public static void putInfoToWeb()
	{
		Connection conn = null;
		Connection webConn = null; 
		PreparedStatement ps = null;
		//PreparedStatement webPs = null;
		//PreparedStatement webPs1 = null;
		ResultSet rs = null;


		try{


			FormLogger.log("zjTime","定时器已经进入，执行时间：["+StringTools.getCurrentDateTime()+"]!");

			//webConn  = ConnectionManager.getInstance().getConnection(xml);
			conn = ConnectionManager.getInstance().getConnection();
			//System.out.println(System.currentTimeMillis());
			/**
			 * 本月没有处理的信息
			 */
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH)+1;
			String monthFirstDay = year + "-" + ((month>9)?"":"0") + month + "-01";
			String sql = "select guid,zjfk,username,nyf,zj,kf,houseGuid from fwzjmx where fksj<now() and fksj>'" + monthFirstDay + "' and sffs=0 limit 0,1000";
			String userName = null;
			String guid = null;
			double zjfk = 0d;
			//double money = 0d;

			//long cTime = System.currentTimeMillis()/1000;   //当前时间到秒的长整数
			String nyf = null;
			rs = conn.createStatement().executeQuery(sql);
			ps = conn.prepareStatement("Update fwzjmx set fkzt='1',sffs=1 where guid=?");
			//webPs = webConn.prepareStatement("Insert into ht_finance_record(username,bank,amount,balance,addtime,reason,note,editor) values(?,?,?,?,?,?,?,?)");
			//webPs1 = webConn.prepareStatement("update ht_member set money=? where username=?");
			while(rs.next())
			{
				userName = rs.getString("username");
				//if(userName == null) continue;     //会员名称为空
				guid = rs.getString("guid");
				zjfk = rs.getDouble("zjfk");
				nyf = rs.getString("nyf");

				FormLogger.log("zjTime","有租金未付：["+userName+"],租金："+zjfk+"!");
				/**
				 * 获取会员的剩余金额
				 */
				/**
					String mSql = "select money from ht_member where username='" +  userName + "'";
					Statement webStmt = webConn.createStatement();
					ResultSet rsMoney = webStmt.executeQuery(mSql);
					if(rsMoney.next()) money = rsMoney.getDouble("money");
					money = money + zjfk;
					ConnectionManager.close(webStmt);


					/**
				 * 添加网站资金记录
				 */
				/**
					webPs.setString(1, userName);
					webPs.setString(2, "");    //银行信息
					webPs.setDouble(3, zjfk);
					webPs.setDouble(4, money);
					webPs.setDouble(5, cTime);
					webPs.setString(6, nyf + "租金");
					webPs.setString(7, "");
					webPs.setString(8, "OA系统");
					webPs.execute();

					/**
				 * 修改会员表中的金额
				 */
				/**
					webPs1.setDouble(1, money);
					webPs1.setString(2, userName);
					webPs1.execute();

					/**
				 * 修改租金支付的状态为1
				 */
				ps.setString(1, guid);
				ps.execute();

				/**
				 * 增加业主收益记录并发送短信
				 */
				InsertYzsy iy = new InsertYzsy();
				iy.insertZjToYzsy(guid);

				FormLogger.log("zjTime","本次租金推送完毕!");

				//休眠20毫秒，防止线程独占
				Thread.sleep(20);
			}
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}

		finally
		{
			ConnectionManager.close(conn);
			//ConnectionManager.close(webConn);
		}			
	}
	public void run() {
		this.putInfoToWeb();
	}
}
