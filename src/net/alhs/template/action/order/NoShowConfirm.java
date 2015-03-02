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
 * noshow 确认
 * @author Administrator
 *
 */
public class NoShowConfirm implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		Connection conn = context.getConn();
		
		
		String guid = context.getReqParameter("guid");
		
		String zt = context.getReqParameter("zt");
		
		/**
		 * 确认未入住（noshow）
		 */
		if(zt.equals("1")){
			conn.createStatement().executeUpdate("update fwddzb set noshow='1' where guid='"+guid+"'");
		}
		
		/**
		 * 确认已入住
		 */
		else{
			conn.createStatement().executeUpdate("update fwddzb set noshow=null,ddzt='2' where guid='"+guid+"'");
			/**
			 * 订单核算
			 */
			OrderFreeAction.initOrderFree(guid);
		}
		
		return SUCCESS;
	}
   
	
	public String getErrorMessage() {
		return null;
	}
}
