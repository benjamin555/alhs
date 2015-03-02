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
 * 提现申请发送短消息
 * @author Administrator
 *
 */
public class TxsqSendMessage implements I_TemplateAction{
	
	private int groupId = 0;   //用户组Id

	public int execute(TemplateContext context) throws Exception {
		String tempId = context.getReqParameter("temp_Id");
		if(tempId.equals("2303")){         //提现确认(提醒会计)
			this.groupId=4;
		}
		else if(tempId.equals("2291")){    //会计确认(提醒出纳)
			this.groupId=11;
		}

		createMessage(context);
		
		return SUCCESS;
	}
	/**
	 * 创建消息
	 * @param context
	 * @throws Exception
	 */
	public void createMessage(TemplateContext context)throws Exception{
		Connection conn = null;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象
			String txsqGuid = context.getReqParameter("guid");   
			String dh = context.getReqParameter("txsq_sqdh");        //单号
			
			ResourceReader rr = new ResourceReader();
			rr.setConnection(conn);
			ArrayList userList = rr.getUserGroupMembers(this.groupId);         //组成员
			
			String content = "提现申请单["+dh+"]需接受确认";
			String url="/engine/gettemplate.jsp?temp_Id=2289";
			
			ShutMessageBean smb = new ShutMessageBean();
			smb.setSender(op.getName());
			smb.setContent(content);
			smb.setUrl(url);
			smb.setTagId(txsqGuid);
			
			ShutCutFactory fac = new ShutCutFactory();
			fac.setConnection(conn);
			
			//先删，再提醒下一步的确认人
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
