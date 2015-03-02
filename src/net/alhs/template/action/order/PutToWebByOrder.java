package net.alhs.template.action.order;


import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * 订单通用同步类
 * @author Administrator
 *
 */
public class PutToWebByOrder implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		  
		String guid = context.getReqParameter("ids");       //订单Guid 删除操作（查询模版）
		
		String ddGuid = context.getReqParameter("guid");    //订单Guid 编辑模版
		
		//System.out.println("ids:"+guid);
		//System.out.println("guid:"+ddGuid);
		
		if(guid == null && ddGuid ==null){
			return SUCCESS;
		}
		else if(guid!=null && !guid.equals(""))
		{
			net.alhs.api.CalendarToWebThread.addOrder(guid);
			
			return SUCCESS;
		}
		else if(ddGuid!=null && !ddGuid.equals(""))
		{
            net.alhs.api.CalendarToWebThread.addOrder(ddGuid);
			
			return SUCCESS;
		}
		return SUCCESS;
	}

	public String getErrorMessage() {
		return null;
	}
}
