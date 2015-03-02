package net.alhs.template.action.order;

import net.alhs.api.Common;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * ��ʾ����ʱ������
 * @author Administrator
 *
 */
public class ShowOrderDateArea implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // ��װ�ػ�����
		
		StringBuffer sb = new StringBuffer();
		
		String _rqqj = (String)context.get("F_rzsjqj");
		
		if(_rqqj!=null && _rqqj.length()>1){
			
			String [] qj = _rqqj.split(";");
			
			for (int i = 0; i < qj.length; i++) {
				  String [] area = qj[i].split(",");
				  sb.append("<TR>");
		          sb.append("<TD class=form_td_left noWrap>").append(i+1).append("����ס���ڣ�").append("</TD>");
		          sb.append("<TD class=form_td_left02 noWrap>").append(area[0]).append("</TD>");
		          sb.append("<TD class=form_td_left noWrap>�˷����ڣ�</TD>");
		          sb.append("<TD class=form_td_left02 noWrap>").append(Common.getSpecifiedDay(area[1],1)).append("</TD>");
		          sb.append("<TD class=form_td_left noWrap>������</TD>");
		          sb.append("<TD class=form_td_left02 colspan=3 noWrap>").append(area[2]).append("</TD>");
		          sb.append("</TD></TR>");
			}
		}
		
		context.put("timeArea", sb.toString());
		
		return SUCCESS;
	}

	public String getErrorMessage() {
		return null;
	}
}
