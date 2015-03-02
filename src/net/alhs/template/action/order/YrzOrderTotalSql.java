package net.alhs.template.action.order;

import net.alhs.common.CommonSelect;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * ����ס����ͳ��sql�洢session
 * @author Administrator
 *
 */
public class YrzOrderTotalSql implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		/**
		 * Ȩ�޿��ƣ��Լ��Ƶ꣬������������
		 */
		new CommonSelect().execute(context);
		
		
		/**
		 * ����session
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
