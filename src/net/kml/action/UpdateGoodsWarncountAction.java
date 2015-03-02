package net.kml.action;
/*
 * 新增物料的时候把物料预警数量添加到物料预警意见表
 */
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysmain.util.GUID;

public class UpdateGoodsWarncountAction implements I_TemplateAction {

	public int execute(TemplateContext context) throws Exception {
		HttpServletRequest request = context.getRequest();
		
		String numStr = context.getReqParameter("wlyj_sl");
					  
		String guid = context.getReqParameter("wlyj_zbguid");
	 	
		String sql = "update goods set warncount=? where guid=?";
		PreparedStatement ps = context.getConn().prepareStatement(sql);
		ps.setInt(1, Integer.parseInt(numStr));
        ps.setString(2, guid);     
		ps.executeUpdate();
		
		sql = "update goods set yjbl=ifnull(count,0)/warncount where guid=?";
		ps=context.getConn().prepareStatement(sql);
		ps.setString(1, guid);
		ps.executeUpdate();
		return SUCCESS;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
