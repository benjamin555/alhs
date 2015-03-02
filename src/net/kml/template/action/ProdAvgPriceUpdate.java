package net.kml.template.action;

/*
 * 修改物料的均价 功能模版225，1075
 */
import java.sql.Connection;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.kml.depot.StockDepotImpl;

public class ProdAvgPriceUpdate implements I_TemplateAction {
	public int execute(TemplateContext context) throws Exception {
		String prodGuid = context.getReqParameter("guid");
		String cxjs = context.getReqParameter("cxjs");
		if(cxjs==null || "".equals(cxjs))
		{
			return SUCCESS;
		}
		if(prodGuid==null || "".equals(prodGuid))
		{
			return SUCCESS;
		}
		Connection conn = context.getConn();
		new StockDepotImpl().avgPrice(prodGuid,conn);
		
		return SUCCESS;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
