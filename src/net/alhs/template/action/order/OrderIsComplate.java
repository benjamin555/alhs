package net.alhs.template.action.order;

import java.sql.Connection;
import java.sql.ResultSet;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;

/**
 * �ֽ𽻽Ӳ��񣬷��ֽ����ȷ�� �ύ�󶩵��Ƿ����
 * @author Administrator
 *
 */
public class OrderIsComplate implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // ��װ�ػ�����

		String ddskGuid = context.getReqParameter("guid");

		isComplate(context, ddskGuid);


		return SUCCESS;
	}

	/**
	 * �ö��������տ������ȷ�ϣ��򶩵���ɣ�1,�������տ� 2,����״̬��ɣ�
	 * @param context
	 * @param ddzbGuid
	 * @throws Exception
	 */
	public void isComplate (TemplateContext context,String ddskGuid) throws Exception {
		/**
		 *   1. skzt='2'(���ֽ����տ�)
		 *   2. jjzt='2'(�ֽ���ת������)
		 */
		if(ddskGuid==null && ddskGuid.trim().equals("")){
			throw new Exception("�ö���������������");
		}

		Connection conn = context.getConn();
		ResultSet rs = null;

		String sql = "select a.zbGuid,b.ddzt,b.yk from fwddsk a,fwddzb b where a.zbGuid=b.guid and a.guid='"+ddskGuid+"'";
		rs = conn.createStatement().executeQuery(sql);
		if(rs.next()){
			//��������Guid
			String ddzbGuid = rs.getString("zbGuid");
			String ddzt = rs.getString("ddzt");
		    double yk = rs.getDouble("yk");
		    
			sql = "select count(*) from fwddsk where zbGuid='"+ddzbGuid+"' and skzt <> '2' and jjzt <> '2'";
			ResultSet rs0 = conn.createStatement().executeQuery(sql);
			if(rs0.next() && rs0.getInt(1)==0){
				/*
				 * cwskzt='0'�������տ�״̬�����տ
				 * ddzt='4'  ������״̬������ɣ�
				 */

				
				/**
				 * ����״̬Ϊ���˷������Ϊ0 �����Զ����
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
