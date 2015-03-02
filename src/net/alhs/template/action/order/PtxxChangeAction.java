package net.alhs.template.action.order;

import java.sql.Connection;
import java.sql.Statement;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * �޸�ƽ̨�����Ϣ����δ����Ӷ��Ķ������´���
 * @author Administrator
 *
 */
public class PtxxChangeAction implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		Connection conn = context.getConn();
		Statement st = conn.createStatement();
		
		String guid = context.getReqParameter("guid");           //ƽ̨Guid 
		String sffy = context.getReqParameter("ptxx_sffy");      //�Ƿ�Ӷ
		
		if(context.getTemplatePara().getEditType()==1){   //�޸�
			if(sffy.equals("0")){                         //��Ҫ��Ӷ
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
