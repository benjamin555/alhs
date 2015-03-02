package net.alhs.template.yezhu;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.chysoft.common.ShutCutFactory;

/**
 * 提现申请 出纳确认后产生业主收益记录
 * @author Administrator
 *
 */
public class TxsqAddYzsy implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {

		String guid = context.getReqParameter("guid");
		
		/**
		 * 业主收益
		 */
		InsertYzsy iy = new InsertYzsy();
		iy.insertTxsqToYzsy(guid);
		
		
		/**
		 * 删除与关联的提醒
		 */
		new ShutCutFactory().deleteMessage(guid);
		
		return SUCCESS;
	}

	public String getErrorMessage() {
		return null;
	}
}
