package net.alhs.wf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import net.business.engine.TableObject;
import net.business.engine.common.TemplateContext;
import net.jbpm.action.common.ExpressionAction;
import net.jbpm.external.WorkFlowSmartForm;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

/**
 * 奖金月表结束Action
 * 
 * @author hewei
 * 
 */
public class BonusEndAction implements ActionHandler {

	private static final long serialVersionUID = 1L;

	public void execute(ExecutionContext executionContext) throws Exception {
		Token token = executionContext.getToken();
		WorkFlowSmartForm wfsf = (WorkFlowSmartForm) token.getAssignment();
		TemplateContext context = wfsf.getTemplateContext();
		TableObject[] tables = context.getTables();
		if (tables == null) {
			throw new Exception("数据读取失败");
		}

		Connection conn = context.getConn();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		//String actionName = executionContext.getTransition().getName(); // 取路由名称

			String guid = context.getReqParameter("guid");      //奖金月表Guid
			String jjlx = context.getReqParameter("jjyb_jjlx"); //奖金类型
			String je = context.getReqParameter("jjyb_jjze");   //奖金总额

			/**
			 * 修改奖金主表该奖金项的总额
			 */
			String sql = "update jiangjin set "+jjlx+"=?," +
					"jjhj=ifnull(tfj,0)+ifnull(dmj,0)+ifnull(zlj,0)+ifnull(xsj,0)+ifnull(wxj,0)+ifnull(jsj,0)+ifnull(fxj,0)+ifnull(mxj,0)+ifnull(klj,0)+ifnull(dxj,0)+ifnull(zxj,0)+ifnull(qjj,0) " +
					"where guid=(select zbGuid from jjyb where guid=?)";
			ps = conn.prepareStatement(sql);
			ps.setDouble(1, Double.parseDouble(je));
			ps.setString(2, guid);
			ps.executeUpdate();
			
			
			/**
			 * 修改奖金明细个人奖金
			 */
			Statement stat = conn.createStatement();
			sql = "select b.guid,jiangjin from jjybmx a,employee_archive b where a.loginId=b.LoginId and a.zbGuid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, guid);
			rs = ps.executeQuery();
			while(rs.next()){
				String eaGuid = rs.getString("guid");     //员工Guid
				double bonus = rs.getDouble("jiangjin");  //奖金
				
			    String setValue =jjlx+"="+bonus;  
			    //自来奖，客来奖，店销奖，网销奖，总销奖 跟出勤率有关
			    if(jjlx.equals("zlj") || jjlx.equals("klj") || jjlx.equals("dxj") || jjlx.equals("wxj") || jjlx.equals("zxj")){
			    	setValue +="*cql";
			    }
				
				stat.addBatch("update jiangjinmx set "+setValue+"," +
						" jjhj=ifnull(tfj,0)+ifnull(dmj,0)+ifnull(zlj,0)+ifnull(xsj,0)+ifnull(wxj,0)+ifnull(jsj,0)+ifnull(fxj,0)+ifnull(mxj,0)+ifnull(klj,0)+ifnull(dxj,0)+ifnull(zxj,0)+ifnull(qjj,0) " +
						" where zbGuid=(select zbGuid from jjyb where guid='"+guid+"')" +
						" and eaGuid='"+eaGuid+"' ");
				/**
				 * 累计到工资明细
				 */
				stat.addBatch("update gzmx set sfgz=ifnull(sfgz,0)+"+bonus+" where eaGuid='"+eaGuid+"' and " +
						" zbGuid =(select gzGuid from jiangjin where guid=(select zbGuid from jjyb where guid='"+guid+"'))");
			}
			stat.executeBatch();
			
			ExpressionAction.getInstance().execute(executionContext);
		}
	}
