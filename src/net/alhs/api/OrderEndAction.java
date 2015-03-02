package net.alhs.api;
import java.sql.Timestamp;
import java.util.Date;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;

/**
 * ���������ύ��ִ����
 * @author Administrator
 *
 */
public class OrderEndAction implements I_TemplateAction {

	public  int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest());  // ��װ�ػ�����

		if(context.getTemplatePara().getEditType() == 1){

			String houseGuid = context.getReqParameter("fwzb_guid");              //����GUID

			String _itemid = context.getReqParameter("itemid");
			int itemid = Integer.parseInt(_itemid);                               //����itemid

			String seller = context.getReqParameter("ddzb_xm");                   //�˿�����

			String _price = context.getReqParameter("ddzb_jg");
			double price = Double.parseDouble(_price);                            //�۸�

			String _amount = context.getReqParameter("ddzb_ffxj");                
			double amount = Double.parseDouble(_amount);                          //�����ܼ�

			String _fee = context.getReqParameter("ddzb_qtfy");                
			double fee = Double.parseDouble(_fee);                                //��������

			String note = context.getReqParameter("ddzb_bz");                     //��ע

			String status = context.getReqParameter("ddzb_ddzt");                 //����״̬

			OrderBean order = new OrderBean();
			order.setItemid(itemid);
			order.setHouseGuid(houseGuid);
			order.setSeller(seller);
			order.setPrice(price);
			order.setAmount(amount);
			order.setFee(fee);
			order.setNote(note);
			order.setStatus(Integer.parseInt(status));
			order.setUpdatetime(new Timestamp(new Date().getTime()).toString());

			OrderAction.updateOrderToWeb(order);

		}

		return SUCCESS;
	}
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
