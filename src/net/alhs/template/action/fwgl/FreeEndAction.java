package net.alhs.template.action.fwgl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.alhs.template.yezhu.InsertYzsy;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysmain.util.GUID;
/**
 * 费用处理单提交后Action
 * @author Administrator
 *
 */
public class FreeEndAction implements I_TemplateAction{
	public int execute(TemplateContext context) throws Exception {
		/**
		 * 修改金额
		 */
		updateMoney(context);
		return SUCCESS;
	}

	/**
	 * 修改金额,本月合计
	 * @param context
	 * @throws Exception
	 */
	private void updateMoney(TemplateContext context) throws Exception{
		Connection conn = context.getConn();

		String type = context.getReqParameter("fycl_fylx"); //费用类型

		String sql = "update fwqtfy set "+type+"=?,byhj=IFNULL(glf,0)+IFNULL(df,0)+IFNULL(sf,0)+IFNULL(rqf,0)+IFNULL(wlf,0)+IFNULL(dsf,0)+IFNULL(wxf,0)+IFNULL(qtf,0) where guid=?";

		PreparedStatement ps = conn.prepareStatement(sql);

		String [] zjmxGuids = context.getReqParameterValues("zjmxGuids");
		String [] _jes = context.getReqParameterValues("je");


		if(zjmxGuids!=null && zjmxGuids.length>0){
			for (int i = 0; i < zjmxGuids.length; i++) {
				ps.setDouble(1, Double.parseDouble(_jes[i].trim().equals("")?"0":_jes[i]));
				ps.setString(2, zjmxGuids[i]);
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}
	public String getErrorMessage() {
		return null;
	}
}
