package net.alhs.template.action.calender;
/**
 * 调价申请单批量调价
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import net.alhs.common.AdjustPrice;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;

public class TjsqEndAction implements I_TemplateAction {

	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象
		HashMap map = new HashMap();
		
		String tjsqGuid = context.getReqParameter("guid");    //调价申请GUID
		
		String sql = "select houseGuid,qrtj_djr,qrtj_xjr,qrtj_wjm,qrtj_wjt,qrtj_djm,qrtj_djt,qrtj_zr,qrtj_gzr from tjsqmx where zbGuid='"+tjsqGuid+"'";
		
		Connection conn = context.getConn();
		
		PreparedStatement ps = conn.prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()){
			String houseGuid = rs.getString("houseGuid");
			double p1 = rs.getDouble("qrtj_djr");
			double p2 = rs.getDouble("qrtj_xjr");
			double p3 = rs.getDouble("qrtj_wjm");
			double p4 = rs.getDouble("qrtj_wjt");
			double p5 = rs.getDouble("qrtj_djm");
			double p6 = rs.getDouble("qrtj_djt");
			double p7 = rs.getDouble("qrtj_zr");
			double p8 = rs.getDouble("qrtj_gzr");

			double [] price = {p1,p2,p3,p4,p5,p6,p7,p8};
			
			ps = conn.prepareStatement("select fxGuid from house where guid='"+houseGuid+"'");
			ResultSet rs0 = ps.executeQuery();
			if(rs0.next()){
				map.put(rs0.getString("fxGuid"), price);
			}
		}
        
        /**
         * 调价
         */
        AdjustPrice.adjustPrice(map);

		return SUCCESS;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
