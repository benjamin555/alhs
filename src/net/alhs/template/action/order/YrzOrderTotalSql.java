package net.alhs.template.action.order;

import net.alhs.common.CommonSelect;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * 已入住订单统计sql存储session
 * @author Administrator
 *
 */
public class YrzOrderTotalSql implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		/**
		 * 权限控制，以及酒店，房型下拉加载
		 */
		new CommonSelect().execute(context);
		
		
		/**
		 * 存入session
		 */
		String querySql = context.getListObjectPara().getQuerySql();

		context.getRequest().getSession().setAttribute("totalSql", querySql);
		
		
		//System.out.println(querySql);
		
		return SUCCESS;
	}

	public String getErrorMessage() {
		return null;
	}
}
