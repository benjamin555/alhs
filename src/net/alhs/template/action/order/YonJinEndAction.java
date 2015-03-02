package net.alhs.template.action.order;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * Ӷ�������ȷ�Ϻ�ִ����
 * @author Administrator
 *
 */
public class YonJinEndAction implements I_TemplateAction{
	
	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // ��װ�ػ�����
		String type = context.getReqParameter("yjcl_yjlx");  //Ӷ������ 1.ƽ̨    2.��ѡ
		
		String yjclGuid = context.getReqParameter("guid");   //Ӷ����Guid
		
		/**
		 * ��ѯ������ϸ�Ķ���
		 */
		String sql = "select ddGuid from yjclmx where zbGuid='"+yjclGuid+"'";
		Connection conn = context.getConn();
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		
		/**
		 * �޸��Ƿ�Ӷ����Ӷ����
		 */
		if(type.equals("1")){     //ƽ̨
			sql = "update ddhs set sffy_pt='2',fyrq_pt=now() where guid=?";
		}
		else{                     //��ѡ
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
