package net.alhs.api;
import java.sql.Timestamp;
import java.util.Date;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;

/**
 * 订单数据提交后执行类
 * @author Administrator
 *
 */
public class OrderEndAction implements I_TemplateAction {

	public  int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest());  // 封装回话对象

		if(context.getTemplatePara().getEditType() == 1){

			String houseGuid = context.getReqParameter("fwzb_guid");              //房间GUID

			String _itemid = context.getReqParameter("itemid");
			int itemid = Integer.parseInt(_itemid);                               //订单itemid

			String seller = context.getReqParameter("ddzb_xm");                   //顾客姓名

			String _price = context.getReqParameter("ddzb_jg");
			double price = Double.parseDouble(_price);                            //价格

			String _amount = context.getReqParameter("ddzb_ffxj");                
			double amount = Double.parseDouble(_amount);                          //房费总计

			String _fee = context.getReqParameter("ddzb_qtfy");                
			double fee = Double.parseDouble(_fee);                                //其他费用

			String note = context.getReqParameter("ddzb_bz");                     //备注

			String status = context.getReqParameter("ddzb_ddzt");                 //订单状态

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
