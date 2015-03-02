package net.alhs.template.action.order;

import java.util.Date;

import net.alhs.api.Common;
import net.alhs.common.CommonSelect;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.chysoft.template.action.CommonSearch2Action;
import net.sysmain.util.StringTools;
/**
 * ���շ�̬��ѯ֮ǰ��
 * @author Administrator
 *
 */
public class BeginTodayFangTai implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // ��װ�ػ�����
		
		String begindate = context.getReqParameter("begindate");
		
		//System.out.println(begindate);
		
        String today = StringTools.getDateByFormat(new Date(), "yyyy-MM-dd");
		
		String querySql = context.getListObjectPara().getQuerySql();
		
		/**
		 * Ĭ�ϲ�ѯ����
		 */
		if(begindate!=null)
		{
			today = begindate;
		}
		querySql +=  (querySql.indexOf(" where ")==-1)?" where ":" and ";
		
		querySql += "('"+today+"' between rzqj.rzrq and rzqj.tfrq or rzqj.tfrq='"+Common.getSpecifiedDay(today,-1)+"') and (ddzb.zcqx IS NULL OR ddzb.zcqx='0') ";
		
		querySql = querySql.replaceFirst("select ", "select distinct ");
		
		context.put("date", today);
		
		//System.out.println(querySql);
		
		context.getListObjectPara().setTransitionSql(querySql);
		
		/**
		 * Ȩ��
		 */
		new CommonSearch2Action().execute(context);
		
		
		querySql = context.getListObjectPara().getQuerySql();
		
		//context.getListObjectPara().setTransitionSql(querySql+ " order by ddzb_sqsj desc");
	
		//context.getListObjectPara().setCountSql("select count(*) from(" + querySql + ") a");
		
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
