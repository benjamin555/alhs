package net.alhs.template.action.fwgl;
/**
 * ��ӷ���ģ��ʱ���·�����Ϣ
 */

import net.alhs.api.HouseAction;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;

public class FillFxInfo implements I_TemplateAction {

	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // ��װ�ػ�����

		String type= context.getReqParameter("fwzb_fwzt");
		
		String guid = context.getReqParameter("guid");    //����GUID

		//����ģ��
		
		if(type.equals("2")){

			//�뷿�ݹ����ķ���Guid
			String fxGuid = context.getReqParameter("fxGuid");

			String hx = context.getReqParameter("fwzb_fx");

			String mj = context.getReqParameter("fwzb_fwmj");

			String cx = context.getReqParameterValues("c_120_258_wlxx_jc")[0];   //ȡ��һ������

			String jg = context.getReqParameterValues("c_120_258_wlxx_jg")[0];   //ȡ��һ������
			
			
			context.getConn().createStatement().execute("update fangxing set hx='"+hx+"',mj='"+mj+"',jg='"+jg+"',cx='"+cx+"' where guid='"+fxGuid+"'");
			
			//����ģ��ֻ���ݴ�״̬
			context.getConn().createStatement().execute("update house set gzlzt='0' where guid='"+guid+"'");
		}

		
        /**
         * ͬ��������Ϣ���������޸ģ�
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
