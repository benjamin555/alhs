package net.alhs.template.yezhu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysmain.common.ConnectionManager;
import net.sysplat.admin.eo.User;
import net.sysplat.admin.manager.UserManager;

/**
 * ����ҵ���˺�
 * @author Administrator
 *
 */
public class AddUserToWeb implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		/**
		 * ͬ��ҵ��ϵͳ���˺��Ƿ���Ч
		 */
		ConnectionManager.getInstance().getConnection("weboa.xml").createStatement().execute("update systemuser_sys set state='"+context.getReqParameter("yzzl_sfyx")+"' where loginId='"+context.getReqParameter("yzzl_userName")+"'");
		
		
		String sffpzh = context.getReqParameter("yzzl_sffpzh");  //�Ƿ�����˺�
		
		if(sffpzh==null || sffpzh.equals("")){
			return SUCCESS;
		}

		String guid = context.getReqParameter("guid");

		addUserToWeb(guid);

		return SUCCESS;
	}

	/**
	 * ���ҵ����ҵ��ϵͳ
	 * @param guid
	 */
	public void addUserToWeb(String guid){
		Connection conn = null;
		Connection webConn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "select * from yzzl";

		if(guid !=null && !guid.equals("")){
			sql += " where guid='"+guid+"'";
		}
		try{
			conn = ConnectionManager.getInstance().getConnection();
			webConn = ConnectionManager.getInstance().getConnection("weboa.xml");
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String name = rs.getString("name");
				String loginId = rs.getString("userName");
				String mobile = rs.getString("MobilePhone");
				String phone = rs.getString("dh");
				String address = rs.getString("homeAddress");
				String email = rs.getString("email");
				String sex  = rs.getString("sex");
				String personId = rs.getString("personID");
				String password = personId.substring(personId.length()-6);  //����Ϊ���֤��6λ

				User aUser= new User();
				aUser.setName(name);
				aUser.setLoginid(loginId);
				aUser.setPassword(password);
				aUser.setMobile(mobile);
				aUser.setOfficePhone(phone);
				aUser.setContact(address);
				aUser.setEmail(email);
				aUser.setSex(sex.equals("��")?1:0);
				aUser.setStatus(1);
				aUser.setDisplayMethod(1);
				aUser.setEnctype(1);
				

				UserManager um = UserManager.getInstance();
				um.setConnection(webConn);
				um.addUser(aUser, "0001O");
			}
			
			/**
			 * �����Ƿ�����˺�״̬
			 */
			conn.createStatement().executeUpdate("update yzzl set sffpzh='1' where guid='"+guid+"'");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			ConnectionManager.close(conn);
			ConnectionManager.close(webConn);
		}
		
	}
	public String getErrorMessage() {
		return null;
	}
}
