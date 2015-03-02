package net.alhs.template.yezhu;

import java.sql.Connection;
import java.util.ArrayList;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.chysoft.common.ShutCutFactory;
import net.chysoft.common.eo.ShutMessageBean;
import net.sysplat.admin.eo.*;
import net.sysmain.common.ConnectionManager;
import net.sysplat.access.Authentication;
import net.sysplat.access.ResourceReader;
import net.sysplat.common.Operator;
/**
 * �������뷢�Ͷ���Ϣ
 * @author Administrator
 *
 */
public class TxsqSendMessage implements I_TemplateAction{
	
	private int groupId = 0;   //�û���Id

	public int execute(TemplateContext context) throws Exception {
		String tempId = context.getReqParameter("temp_Id");
		if(tempId.equals("2303")){         //����ȷ��(���ѻ��)
			this.groupId=4;
		}
		else if(tempId.equals("2291")){    //���ȷ��(���ѳ���)
			this.groupId=11;
		}

		createMessage(context);
		
		return SUCCESS;
	}
	/**
	 * ������Ϣ
	 * @param context
	 * @throws Exception
	 */
	public void createMessage(TemplateContext context)throws Exception{
		Connection conn = null;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			Operator op = Authentication.getUserFromSession(context.getRequest()); // ��װ�ػ�����
			String txsqGuid = context.getReqParameter("guid");   
			String dh = context.getReqParameter("txsq_sqdh");        //����
			
			ResourceReader rr = new ResourceReader();
			rr.setConnection(conn);
			ArrayList userList = rr.getUserGroupMembers(this.groupId);         //���Ա
			
			String content = "�������뵥["+dh+"]�����ȷ��";
			String url="/engine/gettemplate.jsp?temp_Id=2289";
			
			ShutMessageBean smb = new ShutMessageBean();
			smb.setSender(op.getName());
			smb.setContent(content);
			smb.setUrl(url);
			smb.setTagId(txsqGuid);
			
			ShutCutFactory fac = new ShutCutFactory();
			fac.setConnection(conn);
			
			//��ɾ����������һ����ȷ����
			fac.deleteMessage(txsqGuid);
			
			for (int i = 0; i < userList.size(); i++) {
			    User user = (User)userList.get(i);
			    fac.createMessage(smb,new String[]{user.getLoginid()});
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			ConnectionManager.close(conn);
		}
	}
	
	public String getErrorMessage() {
		return null;
	}
}
