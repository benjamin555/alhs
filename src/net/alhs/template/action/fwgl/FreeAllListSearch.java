package net.alhs.template.action.fwgl;

import java.util.Date;

import net.alhs.api.Common;
import net.alhs.common.CommonSelect;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.chysoft.template.action.CommonSearch2Action;
import net.sysmain.util.StringTools;
/**
 * �����ܱ��ѯ
 * @author Administrator
 *
 */
public class FreeAllListSearch implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // ��װ�ػ�����

		String year = context.getReqParameter("nian");
		String month = context.getReqParameter("yue");

		String year1 = context.getReqParameter("nian1");
		String month1 = context.getReqParameter("yue1");

		String ym = StringTools.getDateByFormat(new Date(), "yyyy-MM");    //���·�1

		String ym1 = "";                                                   //���·�1

		String querySql = context.getListObjectPara().getQuerySql();

		querySql +=  (querySql.indexOf(" where ")==-1)?" where ":" and ";
		/**
		 * �������ѯ
		 */
		if(year!=null && !year.equals("") && month != null && !month.equals("") && year1!=null && !year1.equals("") && month1 != null && !month1.equals("")){
			ym = year+"-"+month;
			ym1 = year1+"-"+month1;
		}
		/**
		 * ֻ��һ������
		 */
		else if(year!=null && !year.equals("") && month != null && !month.equals(""))
		{
			ym = year+"-"+month;
		}
		else if(year1!=null && !year1.equals("") && month1 != null && !month1.equals("")){
			ym = year1+"-"+month1;
		}
	    
		if(ym1.equals("")){
			querySql += " zjmx.nyf = '"+ym+"'";
		}
		else{
			querySql += " zjmx.nyf>='"+ym+"' and zjmx.nyf<='"+ym1+"'";
		}
		
		//System.out.println(querySql);
		
		context.getListObjectPara().setTransitionSql(querySql);

		context.put("ym", ym);
		context.put("ym1", ym1);
		/**
		 * ���������
		 */
		new CommonSelect().addOption(context);


		return SUCCESS;
	}

	public String getErrorMessage() {
		return null;
	}
}
