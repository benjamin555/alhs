package net.alhs.template.action.fwgl;
import java.sql.Connection;
import java.sql.ResultSet;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.jbpm.external.TaskInstanceFactory;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;
/**
 * ���ó��ɸ���
 * @author Administrator
 *
 */
public class FreePay implements I_TemplateAction{
	public int execute(TemplateContext context) throws Exception {
		Operator op = Authentication.getUserFromSession(context.getRequest());
		Connection conn = context.getConn();

		String tempId = context.getReqParameter("temp_Id");
		String fylx = context.getReqParameter("fycl_fylx");      //��������
		String zbGuid = context.getReqParameter("zbGuid");       //����Guid
		String zjmxGuid = context.getReqParameter("zjmxGuid");

		if(tempId.equals("2341")){     //�������п�
			/**
			 * �޸ķ��ñ�ĸ���״̬��1���Ѹ��
			 */
			updateFwqtfyStatus(zjmxGuid, fylx, conn);

			/**
			 * �����Ƿ����(gzlzt=3(�����))
			 */
			isComplate(zbGuid, conn);

		}
		//�����˻�
		else{
			String yhzhGuid = context.getReqParameter("yhzhGuid");   //�����˺�Guid
			/**
			 * ���������˺�Guid��ѯ��ϸ
			 */
			String sql = "select guid,zjmxGuid from fwfyclmx where zbGuid='"+zbGuid+"' and yhzhGuid='"+yhzhGuid+"'";
			ResultSet rs = conn.createStatement().executeQuery(sql);
			while(rs.next()){
				String guid = rs.getString("guid");
				String fyGuid = rs.getString("zjmxGuid");  

				/**
				 * 1.�޸ĺϲ����µ�������ϸ�����ô�����ϸ���ĸ���ʱ��
				 */
				conn.createStatement().executeUpdate("update fwfyclmx set cn='"+op.getName()+"',cnsj=now() where guid='"+guid+"'");

				/**
				 * 2.�޸ķ��ñ�ĸ���״̬��1���Ѹ��
				 */
				updateFwqtfyStatus(fyGuid, fylx, conn);
			}

			isComplate(zbGuid, conn);
		}
		return SUCCESS;
	}

	/**
	 * ���ݷ��������޸��������õĸ���״̬
	 * @param zjmxGuid
	 * @param fylx
	 * @param conn
	 * @throws Exception
	 */
	private void updateFwqtfyStatus(String zjmxGuid,String fylx,Connection conn)throws Exception {
		String sql = "update fwqtfy set fkzt_"+fylx+"='1',fksj_"+fylx+"=now() where guid='"+zjmxGuid+"'";
		conn.createStatement().executeUpdate(sql);
	}

	/**
	 * �����Ƿ����
	 * @param zbGuid
	 * @param conn
	 * @throws Exception
	 */
	public void isComplate(String zbGuid,Connection conn)throws Exception {
		String sql = "select count(*) from fwfyclmx where zbGuid='"+zbGuid+"' and cnsj is null";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if(rs.next() && rs.getInt(1)==0){
			/**
			 * ���
			 */
			sql = "update fwfycld set gzlzt='2' where guid='"+zbGuid+"'";
			conn.createStatement().executeUpdate(sql);

			/**
			 * ����������
			 */
			sql = "select instanceGuid from fwfycld where guid='"+zbGuid+"'";
			rs = conn.createStatement().executeQuery(sql);
			if(rs.next()){
				TaskInstanceFactory taskFac = TaskInstanceFactory.getInstance();
				taskFac.setConnection(conn);
				taskFac.endToken(rs.getString("instanceGuid"));
			}
		}
	}

	public String getErrorMessage() {
		return null;
	}
}
