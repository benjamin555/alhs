package net.alhs.template.action.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;

/**
 * �����տ������� ���㶩�����
 * @author Administrator
 *
 */
public class CalcOrderMoney implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		Connection conn = context.getConn();
		
		String guid = context.getReqParameter("zbGuid");                          //����Guid
		
		String _money = context.getReqParameter("ddsk_skje");
		double money = Double.parseDouble(_money.trim().equals("")?"0":_money);   //���
		
		String tempId = context.getReqParameter("temp_Id");                       //ģ��ID
		
		if(tempId.equals("2238")){               //�տ�
			/**
			 * �ۼ��տ�
			 */
			addDdsk(conn, guid, money);
			
			/**
			 * ͳ�Ƹ����ֽ�
			 */
			new PersonalCashStatistics().execute(context);
		}
		else if(tempId.equals("2260")){          //�������
			addFwfy(conn, guid, money);
		}


		return SUCCESS;
	}
	/**
	 * �����տ�ۼ����տ���
	 * @param conn
	 * @param guid
	 * @param money
	 * @throws Exception
	 */
    public void addDdsk(Connection conn,String guid,double money)throws Exception{
    	PreparedStatement ps = null;
    	String sql = "update fwddzb set ysk=ifnull(ysk,0)+?,yk=ifnull(ysk,0)-ifnull(zje,0) where guid=?";
    	ps = conn.prepareStatement(sql);
    	ps.setDouble(1, money);
    	ps.setString(2, guid);
		ps.executeUpdate();
    }
    /**
     * ������ã��ۼ��ܽ����
     * @param conn
     * @param guid
     * @param money
     * @throws Exception
     */
    public void addFwfy(Connection conn,String guid,double money)throws Exception{
    	PreparedStatement ps = null;
    	String sql = "update fwddzb set qtfy=ifnull(qtfy,0)+?,zje=ifnull(zje,0)+?,yk=ysk-ifnull(zje,0) where guid=?";
    	ps = conn.prepareStatement(sql);
    	ps.setDouble(1, money);
    	ps.setDouble(2, money);
    	ps.setString(3, guid);
		ps.executeUpdate();
    }
	
	public String getErrorMessage() {
		return null;
	}
}
