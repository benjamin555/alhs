package net.alhs.template.action.fwgl;
/**
 * 房屋布置意见提交
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;

public class EndfwbzyjAction implements I_TemplateAction {

	public int execute(TemplateContext context) throws Exception {
		Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象
		String guid = context.getReqParameter("guid");
		int type = Integer.parseInt(context.getReqParameter("id"));
		String bz =  context.getReqParameter("remark");
		String sqlStr = null; 

		switch (type) {
		case 1:  sqlStr=" zx=?,zxsj=?,zxfzryj=? "; break;
		case 2:  sqlStr=" dq=?,dqsj=?,dqfzryj=? "; break;
		case 3:  sqlStr=" js=?,jssj=?,jsfzryj=? "; break;
		case 4:  sqlStr=" zs=?,zssj=?,zsfzryj=? "; break;
		case 5:  sqlStr=" wd=?,wdsj=?,wdfzryj=? "; break;
		case 6:  sqlStr=" xhp=?,xhpsj=?,xhpfzryj=? "; break;
		case 7:  sqlStr=" fjz=?,fjzsj=?,fjzfzryj=? "; break;
		case 8:  sqlStr=" hjz=?,hjzsj=?,hjzfzryj=? "; break;
		case 9:  sqlStr=" wa=?,wasj=?,wafzryj=? "; break;
		}  
		String sql = "update fwrz set"+sqlStr+" where guid=?";
		Connection conn = context.getConn();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, op.getName());
		pstmt.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
		pstmt.setString(3, bz);
		pstmt.setString(4, guid);
		
		pstmt.executeUpdate();
		return SUCCESS;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
