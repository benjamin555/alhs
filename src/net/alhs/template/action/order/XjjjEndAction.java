package net.alhs.template.action.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.chysoft.common.ShutCutFactory;
import net.chysoft.common.eo.ShutMessageBean;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;
/**
 * �ֽ𽻽���ش���
 * @author Administrator
 *
 */
public class XjjjEndAction implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {

		String jjzt = context.getReqParameter("xjjj_jjzt");                    //����״̬


		//�ݴ�
		if(jjzt==null || jjzt.equals("")){
			return SUCCESS;
		}
		//������(ȷ��)
		else if(jjzt.equals("1")){
            confirm(context);
		}
		//����
		else if(jjzt.equals("2")){
           accept(context);
		}
		//�ܾ�
		else if(jjzt.equals("4")){
           refused(context);
		}
		//����ȷ��
		else if(jjzt.equals("3")){
           financialConfirm(context);
		}
		
		
		
		return SUCCESS;
	}

	/**
	 * ȷ��
	 * @param context
	 * @throws Exception
	 */
	public void confirm(TemplateContext context)throws Exception{
		/**
		 * 1.�������� 
		 */
		createMessage(context);
		
		/**
		 * 2.���̲��루�����ˣ�
		 */
		String jsrid = context.getReqParameter("xjjj_jsrid");    //������id
		String xjjjGuid = context.getReqParameter("guid");
		
		context.getConn().createStatement().executeUpdate("insert into lccy(guid,userid) values('"+xjjjGuid+"','"+jsrid+"')");
		

	}
	/**
	 * ���ܴ���
	 * @param context
	 * @throws Exception
	 */
	public void accept(TemplateContext context)throws Exception{
		Operator op = Authentication.getUserFromSession(context.getRequest()); // ��װ�ػ�����
		Connection conn = context.getConn();
		PreparedStatement ps = null;
		
		String xjjjGuid = context.getReqParameter("guid");
		String [] ddskGuids = context.getReqParameterValues("ddskGuids");
		String sqrGuid = context.getReqParameter("xjjj_sqrguid");  
		String jsrid = context.getReqParameter("xjjj_jsrid");           //������id
		String jsr = context.getReqParameter("xjjj_jsr");               //������
		String _zje = context.getReqParameter("xjjj_zje");              //���

		/**
		 * �Ѵ���ͨ�����̲�����ƣ�
		 */
		insertLccy(context, ddskGuids, sqrGuid);

		/**
		 * �޸���������
		 */
		
		String sql = "update fwddsk set zhjsr=? where guid=?";
		ps = conn.prepareStatement(sql);
		if(ddskGuids!=null && ddskGuids.length>0){
			for (int i = 0; i < ddskGuids.length; i++) {
                 ps.setString(1, jsrid);
                 ps.setString(2, ddskGuids[i]);
                 ps.addBatch();
			}
			ps.executeBatch();
		}
		
		/**
		 * ɾ������
		 */
		removeMessage(xjjjGuid);

		/**
		 * ����տ���еĽ���״̬��jjzt=0�����ٴν��ӣ���
		 */
		changeStatus(context, ddskGuids,"0");
		
		
		/**
		 * �����ֽ�ͳ��
		 */
		double zje = Double.parseDouble(_zje.trim().equals("")?"0":_zje);
		PersonalCashBean pcb = new PersonalCashBean();
		pcb.setXmid(sqrGuid);
		pcb.setMoney(zje);
		
		PersonalCashStatistics pc = new PersonalCashStatistics();
		pc.receiveMoney(pcb, jsr, jsrid);
		
		
		/**
		 * ��¼��־
		 */
	}
	
	/**
	 * �ܾ�
	 * @param context
	 * @throws Exception
	 */
	public void refused(TemplateContext context)throws Exception{
		/**
		 *  1.����տ���еĽ���״̬��jjzt=0�����ٴν��ӣ���
		 */
		changeStatus(context, context.getReqParameterValues("ddskGuids"),"0");
		
		/**
		 *  2.ɾ������
		 */
		removeMessage(context.getReqParameter("guid"));
		
	}
	
	/**
	 * ����ȷ��
	 * @param context
	 * @throws Exception
	 */
	public void financialConfirm(TemplateContext context)throws Exception{
		Operator op = Authentication.getUserFromSession(context.getRequest()); // ��װ�ػ�����
		Connection conn = context.getConn();
		PreparedStatement ps = null;

		String xjjjGuid = context.getReqParameter("guid");
		String [] ddskGuids = context.getReqParameterValues("ddskGuids");
		String sqrGuid = context.getReqParameter("xjjj_sqrguid");   
		String jsrid = context.getReqParameter("xjjj_jsrid");    //������id
		String _zje = context.getReqParameter("xjjj_zje");       //���
		

		/**
		 * 1.1�޸���������
		 */
		String sql = "update fwddsk set zhjsr=? where guid=?";
		ps = conn.prepareStatement(sql);
		if(ddskGuids!=null && ddskGuids.length>0){
			for (int i = 0; i < ddskGuids.length; i++) {
                 ps.setString(1, jsrid);
                 ps.setString(2, ddskGuids[i]);
                 ps.addBatch();
			}
			ps.executeBatch();
		}
		
		/**
		 * 1.2ɾ������
		 */
		removeMessage(xjjjGuid);
		
		
		/**
		 * 2.�޸Ľ�������Ĳ���ȷ���ˣ�����ȷ��ʱ��
		 */
	    sql = "update xjjjb set cwclr=?,cwqrsj=now() where guid=?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, op.getName());
		ps.setString(2, xjjjGuid);
		ps.executeUpdate();
		
		/**
		 * 3.�޸Ķ����տ���Ĳ���ȷ���ˣ�����ȷ��ʱ��
		 */
		sql = "update fwddsk set cwclr=?,cwqrsj=now() where guid=?";
		ps = conn.prepareStatement(sql);
		
		if(ddskGuids!=null && ddskGuids.length>0){
			for (int i = 0; i < ddskGuids.length; i++) {
                 ps.setString(1, op.getName());
                 ps.setString(2, ddskGuids[i]);
                 ps.addBatch();
			}
			ps.executeBatch();
		}
		
		/**
		 * 4.1.�����������
		 *   2.�Ѵ���ͨ�����̲�����ƣ�
		 */
		insertLccy(context, ddskGuids, jsrid);

		insertLccy(context, ddskGuids, sqrGuid);
		
		/**
		 * 5.�����տ� jjzt='2' ��ʾ���
		 */
		changeStatus(context, ddskGuids, "2");
		
		/**
		 * 6.�����Ƿ����
		 */
		if(ddskGuids!=null && ddskGuids.length>0){
			OrderIsComplate oc = new OrderIsComplate();
			for (int i = 0; i < ddskGuids.length; i++) {
				oc.isComplate(context, ddskGuids[i]);
			}
		}
		
		/**
		 * 7.�ۼƽ�����
		 */
		double zje = Double.parseDouble(_zje.trim().equals("")?"0":_zje);
		PersonalCashBean pcb = new PersonalCashBean();
		pcb.setXmid(sqrGuid);
		pcb.setMoney(zje);
		
		PersonalCashStatistics pc = new PersonalCashStatistics();
		pc.calcCwMoney(pcb);
	}
	
	
	/**
	 * ������Ϣ
	 * @param context
	 * @throws Exception
	 */
	public void createMessage(TemplateContext context)throws Exception{
		Operator op = Authentication.getUserFromSession(context.getRequest()); // ��װ�ػ�����
		String xjjjGuid = context.getReqParameter("guid");   
		String dh = context.getTableFieldByAlias("xjjj.jjdh").getFieldValue();        //����
		
		//System.out.println(dh+"!!!!");
		String jsrid = context.getReqParameter("xjjj_jsrid");    //������id
		
		String content = "�ֽ𽻽ӵ�["+dh+"]�����ȷ��";
		String url="/engine/gettemplate.jsp?temp_Id=2269&edittype=1&guid="+xjjjGuid+"";
		
		ShutMessageBean smb = new ShutMessageBean();
		smb.setSender(op.getName());
		smb.setContent(content);
		smb.setUrl(url);
		smb.setTagId(xjjjGuid);

		
		ShutCutFactory fac = new ShutCutFactory();
		fac.createMessage(smb,new String[]{jsrid});

	}
	
	/**
	 * ɾ����Ϣ
	 * @param context
	 * @throws Exception
	 */
	public void removeMessage(String guid)throws Exception{
		ShutCutFactory fac = new ShutCutFactory();
		fac.deleteMessage(guid);
	}
	
	/**
	 * ���̲���
	 * @param context
	 * @param ddskGuids
	 * @param userid
	 * @throws Exception
	 */
	public void insertLccy(TemplateContext context,String [] ddskGuids,String userid)throws Exception{
		Connection conn = context.getConn();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "insert into lccy(guid,userid) values(?,?)";
		ps = conn.prepareStatement(sql);
		if(ddskGuids!=null && ddskGuids.length>0){
			for (int i = 0; i < ddskGuids.length; i++) {
				rs = conn.createStatement().executeQuery("select count(*) from lccy where guid='"+ddskGuids[i]+"' and userid='"+userid+"'");
				if(rs.next() && rs.getInt(1)>0){
					continue;
				}
				ps.setString(1, ddskGuids[i]);
				ps.setString(2, userid);
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}
	
	/**
	 * �ı��տ���jjzt 0�ɽ��ӣ�δѡ��  1���ɽ��ӣ���ѡ�� 2����ȷ�ϣ���ɣ�
	 * @param context
	 * @param ddskGuids
	 * @throws Exception
	 */
    public void changeStatus(TemplateContext context,String [] ddskGuids,String status)throws Exception{
    	Connection conn = context.getConn();
		PreparedStatement ps = null;
		String sql = "update fwddsk set jjzt=? where guid=?";  //jjzt 0�ɽ��ӣ�δѡ��  1���ɽ��ӣ���ѡ�� 2����ȷ�ϣ���ɣ�
		ps = conn.prepareStatement(sql);
		
		if(ddskGuids!=null && ddskGuids.length>0){
			for (int i = 0; i < ddskGuids.length; i++) {
				 ps.setString(1, status);
                 ps.setString(2, ddskGuids[i]);
                 ps.addBatch();
			}
			ps.executeBatch();
		}
	}
	
	
	public String getErrorMessage() {
		return null;
	}
}
