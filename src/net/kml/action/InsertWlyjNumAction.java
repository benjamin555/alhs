package net.kml.action;
/*
 * 新增物料的时候把物料预警数量添加到物料预警意见表
 */
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysmain.util.GUID;

public class InsertWlyjNumAction implements I_TemplateAction {

	public int execute(TemplateContext context) throws Exception {
		HttpServletRequest request = context.getRequest();
		String numStr = request.getParameter("prod_warncount")==null?
					  context.getFileUpload().getRequest().getParameter("prod_warncount"):
					  request.getParameter("prod_warncount");
		String guid = request.getParameter("guid")==null?
					  context.getFileUpload().getRequest().getParameter("guid"):
					  request.getParameter("guid");
		String sqr = request.getParameter("prod_gpersion")==null?
					  context.getFileUpload().getRequest().getParameter("prod_gpersion"):
					  request.getParameter("prod_gpersion");
		 	
		String sql = "insert into wlyj (guid, zbguid,yjsj,yjr,sl) values(?,?,now(),?,?)";
		PreparedStatement ps = context.getConn().prepareStatement(sql);
        ps.setString(1, new GUID().toString());
        ps.setString(2, guid);
        ps.setString(3, sqr);
        ps.setInt(4, Integer.parseInt(numStr));
		ps.executeUpdate();
		return SUCCESS;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
