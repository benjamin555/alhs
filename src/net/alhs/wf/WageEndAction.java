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
 * 工资审批结束后的Action
 * 
 * @author hewei
 * 
 */
public class WageEndAction implements ActionHandler {

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


		String year = context.getReqParameter("gzzb_nian");    //年
		String month = context.getReqParameter("gzzb_yue");    //月

		/**
		 * 修改订单清理结算状态
		 */
		String sql = "update fwddzb set qljszt='1' where zcqx is null and ddzt>'2' and SUBSTRING(qlsj,1,7)=?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, year+"-"+month);
		ps.executeUpdate();


		/*
		 *工作流状态 
		 */
		ExpressionAction.getInstance().execute(executionContext);
	}
}
