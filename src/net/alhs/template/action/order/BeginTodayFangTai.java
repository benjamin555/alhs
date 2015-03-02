package net.alhs.template.action.order;

import java.util.Date;

import net.alhs.api.Common;
import net.alhs.common.CommonSelect;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.chysoft.template.action.CommonSearch2Action;
import net.sysmain.util.StringTools;
/**
 * 当日房态查询之前类
 * @author Administrator
 *
 */
public class BeginTodayFangTai implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象
		
		String begindate = context.getReqParameter("begindate");
		
		//System.out.println(begindate);
		
        String today = StringTools.getDateByFormat(new Date(), "yyyy-MM-dd");
		
		String querySql = context.getListObjectPara().getQuerySql();
		
		/**
		 * 默认查询当天
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
		 * 权限
		 */
		new CommonSearch2Action().execute(context);
		
		
		querySql = context.getListObjectPara().getQuerySql();
		
		//context.getListObjectPara().setTransitionSql(querySql+ " order by ddzb_sqsj desc");
	
		//context.getListObjectPara().setCountSql("select count(*) from(" + querySql + ") a");
		
		/**
		 * 添加下拉框
		 */
		new CommonSelect().addOption(context);
		
		
		return SUCCESS;
	}

	public String getErrorMessage() {
		return null;
	}
}
