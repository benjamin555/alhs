package net.alhs.template.yezhu;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.chysoft.common.ShutCutFactory;

/**
 * �������� ����ȷ�Ϻ����ҵ�������¼
 * @author Administrator
 *
 */
public class TxsqAddYzsy implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {

		String guid = context.getReqParameter("guid");
		
		/**
		 * ҵ������
		 */
		InsertYzsy iy = new InsertYzsy();
		iy.insertTxsqToYzsy(guid);
		
		
		/**
		 * ɾ�������������
		 */
		new ShutCutFactory().deleteMessage(guid);
		
		return SUCCESS;
	}

	public String getErrorMessage() {
		return null;
	}
}
