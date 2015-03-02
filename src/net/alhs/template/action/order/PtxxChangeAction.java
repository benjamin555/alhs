package net.alhs.template.action.order;

import java.sql.Connection;
import java.sql.Statement;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * 修改平台相关信息，对未处理佣金的订单重新处理
 * @author Administrator
 *
 */
public class PtxxChangeAction implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		Connection conn = context.getConn();
		Statement st = conn.createStatement();
		
		String guid = context.getReqParameter("guid");           //平台Guid 
		String sffy = context.getReqParameter("ptxx_sffy");      //是否付佣
		
		if(context.getTemplatePara().getEditType()==1){   //修改
			if(sffy.equals("0")){                         //不要付佣
				st.addBatch("update ddhs set sffy_pt='2' where sffy_pt<2 and ptxxGuid='"+guid+"'");
				st.addBatch("update ddhs set sffy_hx='2' where sffy_hx<2 and ptxxGuid='"+guid+"'");
				st.executeBatch();
			}
		}
		return SUCCESS;
	}

	
	public String getErrorMessage() {
		return null;
	}
}
