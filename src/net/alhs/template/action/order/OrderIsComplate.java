package net.alhs.template.action.order;

import java.sql.Connection;
import java.sql.ResultSet;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;

/**
 * 现金交接财务，非现金财务确认 提交后订单是否完成
 * @author Administrator
 *
 */
public class OrderIsComplate implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象

		String ddskGuid = context.getReqParameter("guid");

		isComplate(context, ddskGuid);


		return SUCCESS;
	}

	/**
	 * 该订单所有收款财务已确认，则订单完成（1,订单已收款 2,订单状态完成）
	 * @param context
	 * @param ddzbGuid
	 * @throws Exception
	 */
	public void isComplate (TemplateContext context,String ddskGuid) throws Exception {
		/**
		 *   1. skzt='2'(非现金已收款)
		 *   2. jjzt='2'(现金已转交财务)
		 */
		if(ddskGuid==null && ddskGuid.trim().equals("")){
			throw new Exception("该订单的主键不存在");
		}

		Connection conn = context.getConn();
		ResultSet rs = null;

		String sql = "select a.zbGuid,b.ddzt,b.yk from fwddsk a,fwddzb b where a.zbGuid=b.guid and a.guid='"+ddskGuid+"'";
		rs = conn.createStatement().executeQuery(sql);
		if(rs.next()){
			//订单主表Guid
			String ddzbGuid = rs.getString("zbGuid");
			String ddzt = rs.getString("ddzt");
		    double yk = rs.getDouble("yk");
		    
			sql = "select count(*) from fwddsk where zbGuid='"+ddzbGuid+"' and skzt <> '2' and jjzt <> '2'";
			ResultSet rs0 = conn.createStatement().executeQuery(sql);
			if(rs0.next() && rs0.getInt(1)==0){
				/*
				 * cwskzt='0'（财务收款状态：已收款）
				 * ddzt='4'  （订单状态：已完成）
				 */

				
				/**
				 * 订单状态为已退房，余款为0 订单自动完成
				 */
				if(ddzt.equals("3") && yk==0){
					sql = "update fwddzb set cwskzt='0',ddzt='4' where guid='"+ddzbGuid+"'";
			    }
				else{
					sql = "update fwddzb set cwskzt='0' where guid='"+ddzbGuid+"'";
				}
				conn.createStatement().executeUpdate(sql);
			}
		}
	}

	public String getErrorMessage() {
		return null;
	}
}
