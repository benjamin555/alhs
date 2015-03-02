package net.alhs.template.action.calender;
/**
 * 房型模版列表设置初始化价格
 */

import net.alhs.common.AdjustPrice;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;

public class InitPrice implements I_TemplateAction {

	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象
		
		String guid = context.getReqParameter("houseGuid");    //房屋GUID
 

        String _p1 = context.getReqParameter("fwjg_djr");
        String _p2 = context.getReqParameter("fwjg_xjr");
        String _p3 = context.getReqParameter("fwjg_wjm");
        String _p4 = context.getReqParameter("fwjg_wjt");
        String _p5 = context.getReqParameter("fwjg_djm");
        String _p6 = context.getReqParameter("fwjg_djt");
        String _p7 = context.getReqParameter("fwjg_zr");
        String _p8 = context.getReqParameter("fwjg_gzr");
 
        double p1 = Double.parseDouble(_p1);
        double p2 = Double.parseDouble(_p2);
        double p3 = Double.parseDouble(_p3);
        double p4 = Double.parseDouble(_p4);
        double p5 = Double.parseDouble(_p5);
        double p6 = Double.parseDouble(_p6);
        double p7 = Double.parseDouble(_p7);
        double p8 = Double.parseDouble(_p8);

        
        double [] price = {p1,p2,p3,p4,p5,p6,p7,p8};
        
        /**
         * 初始化价格
         */
        AdjustPrice.initPrice(guid, price);

		return SUCCESS;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
