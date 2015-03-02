package net.alhs.template.action.order;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * 佣金处理出纳确认后执行类
 * @author Administrator
 *
 */
public class YonJinEndAction implements I_TemplateAction{
	
	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象
		String type = context.getReqParameter("yjcl_yjlx");  //佣金类型 1.平台    2.惠选
		
		String yjclGuid = context.getReqParameter("guid");   //佣金处理Guid
		
		/**
		 * 查询处理明细的订单
		 */
		String sql = "select ddGuid from yjclmx where zbGuid='"+yjclGuid+"'";
		Connection conn = context.getConn();
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		
		/**
		 * 修改是否付佣，付佣日期
		 */
		if(type.equals("1")){     //平台
			sql = "update ddhs set sffy_pt='2',fyrq_pt=now() where guid=?";
		}
		else{                     //惠选
			sql = "update ddhs set sffy_hx='2',fyrq_hx=now() where guid=?";
		}
		
		ps = conn.prepareStatement(sql);
		while(rs.next()){
			ps.setString(1, rs.getString("ddGuid"));
			ps.addBatch();
		}
        
		ps.executeBatch();
		
		return SUCCESS;
	}

	public String getErrorMessage() {
		return null;
	}
}
