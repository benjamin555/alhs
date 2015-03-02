package net.alhs.template.action.fwgl;
import java.sql.Connection;
import java.sql.ResultSet;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.jbpm.external.TaskInstanceFactory;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;
/**
 * 费用出纳付款
 * @author Administrator
 *
 */
public class FreePay implements I_TemplateAction{
	public int execute(TemplateContext context) throws Exception {
		Operator op = Authentication.getUserFromSession(context.getRequest());
		Connection conn = context.getConn();

		String tempId = context.getReqParameter("temp_Id");
		String fylx = context.getReqParameter("fycl_fylx");      //费用类型
		String zbGuid = context.getReqParameter("zbGuid");       //主表Guid
		String zjmxGuid = context.getReqParameter("zjmxGuid");

		if(tempId.equals("2341")){     //个人银行卡
			/**
			 * 修改费用表的付款状态：1（已付款）
			 */
			updateFwqtfyStatus(zjmxGuid, fylx, conn);

			/**
			 * 处理单是否完成(gzlzt=3(已完成))
			 */
			isComplate(zbGuid, conn);

		}
		//费用账户
		else{
			String yhzhGuid = context.getReqParameter("yhzhGuid");   //银行账号Guid
			/**
			 * 根据银行账号Guid查询明细
			 */
			String sql = "select guid,zjmxGuid from fwfyclmx where zbGuid='"+zbGuid+"' and yhzhGuid='"+yhzhGuid+"'";
			ResultSet rs = conn.createStatement().executeQuery(sql);
			while(rs.next()){
				String guid = rs.getString("guid");
				String fyGuid = rs.getString("zjmxGuid");  

				/**
				 * 1.修改合并单下的所有明细（费用处理明细）的付款时间
				 */
				conn.createStatement().executeUpdate("update fwfyclmx set cn='"+op.getName()+"',cnsj=now() where guid='"+guid+"'");

				/**
				 * 2.修改费用表的付款状态：1（已付款）
				 */
				updateFwqtfyStatus(fyGuid, fylx, conn);
			}

			isComplate(zbGuid, conn);
		}
		return SUCCESS;
	}

	/**
	 * 根据费用类型修改其它费用的付款状态
	 * @param zjmxGuid
	 * @param fylx
	 * @param conn
	 * @throws Exception
	 */
	private void updateFwqtfyStatus(String zjmxGuid,String fylx,Connection conn)throws Exception {
		String sql = "update fwqtfy set fkzt_"+fylx+"='1',fksj_"+fylx+"=now() where guid='"+zjmxGuid+"'";
		conn.createStatement().executeUpdate(sql);
	}

	/**
	 * 处理单是否完成
	 * @param zbGuid
	 * @param conn
	 * @throws Exception
	 */
	public void isComplate(String zbGuid,Connection conn)throws Exception {
		String sql = "select count(*) from fwfyclmx where zbGuid='"+zbGuid+"' and cnsj is null";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if(rs.next() && rs.getInt(1)==0){
			/**
			 * 完成
			 */
			sql = "update fwfycld set gzlzt='2' where guid='"+zbGuid+"'";
			conn.createStatement().executeUpdate(sql);

			/**
			 * 结束工作流
			 */
			sql = "select instanceGuid from fwfycld where guid='"+zbGuid+"'";
			rs = conn.createStatement().executeQuery(sql);
			if(rs.next()){
				TaskInstanceFactory taskFac = TaskInstanceFactory.getInstance();
				taskFac.setConnection(conn);
				taskFac.endToken(rs.getString("instanceGuid"));
			}
		}
	}

	public String getErrorMessage() {
		return null;
	}
}
