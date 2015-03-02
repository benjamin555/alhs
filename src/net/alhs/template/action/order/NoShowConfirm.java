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
 * noshow ȷ��
 * @author Administrator
 *
 */
public class NoShowConfirm implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		Connection conn = context.getConn();
		
		
		String guid = context.getReqParameter("guid");
		
		String zt = context.getReqParameter("zt");
		
		/**
		 * ȷ��δ��ס��noshow��
		 */
		if(zt.equals("1")){
			conn.createStatement().executeUpdate("update fwddzb set noshow='1' where guid='"+guid+"'");
		}
		
		/**
		 * ȷ������ס
		 */
		else{
			conn.createStatement().executeUpdate("update fwddzb set noshow=null,ddzt='2' where guid='"+guid+"'");
			/**
			 * ��������
			 */
			OrderFreeAction.initOrderFree(guid);
		}
		
		return SUCCESS;
	}
   
	
	public String getErrorMessage() {
		return null;
	}
}
