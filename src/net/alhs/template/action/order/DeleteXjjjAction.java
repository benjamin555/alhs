package net.alhs.template.action.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.business.template.action.SerialNumberAction;
import net.chysoft.common.ShutCutFactory;
import net.chysoft.common.eo.ShutMessageBean;
import net.jbpm.external.eo.SimpleTaskInfo;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;
/**
 * 删除现金交接，及明细
 * @author Administrator
 *
 */
public class DeleteXjjjAction implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		Connection conn = context.getConn();
		
		
		String guid = context.getReqParameter("ids");
		
		if(guid==null)
		{
			/**
			 * 单号（暂存 确定）
			 */
			new SerialNumberAction().execute(context);
			
			return SUCCESS;
		}

		if(context.getTemplatePara().getTemplate().getAccessMethod()=='e'){ 
			deleteByXjjjmxGuid(conn, guid);
		}
		else{
			deleteByXjjjGuid(conn, guid);
		}
		
		return SUCCESS;
	}
    public void deleteByXjjjmxGuid(Connection conn,String guid)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select ddskGuid from xjjjmx where guid=?";
		ps= conn.prepareStatement(sql);
		ps.setString(1, guid);
		rs = ps.executeQuery();
		if(rs.next()){
			String ddskGuid = rs.getString("ddskGuid");
			conn.createStatement().executeUpdate("update fwddsk set jjzt='0' where guid='"+ddskGuid+"'");
		}
    }
    public void deleteByXjjjGuid(Connection conn,String guid)throws Exception{
    	PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select ddskGuid from xjjjmx where zbGuid=?";
		ps= conn.prepareStatement(sql);
		ps.setString(1, guid);
		rs = ps.executeQuery();
		while(rs.next()){
			String ddskGuid = rs.getString("ddskGuid");
			conn.createStatement().executeUpdate("update fwddsk set jjzt='0' where guid='"+ddskGuid+"'");
		}
    }
	
	public String getErrorMessage() {
		return null;
	}
}
