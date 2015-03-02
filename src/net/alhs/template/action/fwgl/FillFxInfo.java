package net.alhs.template.action.fwgl;
/**
 * 添加房型模版时更新房型信息
 */

import net.alhs.api.HouseAction;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;

public class FillFxInfo implements I_TemplateAction {

	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象

		String type= context.getReqParameter("fwzb_fwzt");
		
		String guid = context.getReqParameter("guid");    //房屋GUID

		//房型模版
		
		if(type.equals("2")){

			//与房屋关联的房型Guid
			String fxGuid = context.getReqParameter("fxGuid");

			String hx = context.getReqParameter("fwzb_fx");

			String mj = context.getReqParameter("fwzb_fwmj");

			String cx = context.getReqParameterValues("c_120_258_wlxx_jc")[0];   //取第一个床型

			String jg = context.getReqParameterValues("c_120_258_wlxx_jg")[0];   //取第一个景观
			
			
			context.getConn().createStatement().execute("update fangxing set hx='"+hx+"',mj='"+mj+"',jg='"+jg+"',cx='"+cx+"' where guid='"+fxGuid+"'");
			
			//房型模版只有暂存状态
			context.getConn().createStatement().execute("update house set gzlzt='0' where guid='"+guid+"'");
		}

		
        /**
         * 同步房屋信息（新增，修改）
         */
		if(context.getTemplatePara().getEditType()==1){
			HouseAction.UpdateHouseToWeb(guid);
		}
		else{
            HouseAction.addHouseToWeb(guid,type.equals("2"));
		}



		return SUCCESS;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
