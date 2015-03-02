package net.alhs.template.action.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.chysoft.common.MemberNameProxy;
import net.sysmain.common.ConnectionManager;
import net.sysmain.common.FormLogger;
import net.sysmain.util.GUID;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;

/**
 * �����ֽ�ͳ��
 * @author Administrator
 *
 */
public class PersonalCashStatistics implements I_TemplateAction{
	
	public int execute(TemplateContext context) throws Exception {
		Operator op = Authentication.getUserFromSession(context.getRequest()); // ��װ�ػ�����

		String sklx = context.getReqParameter("ddsk_sklx");       //�տ�����
		
		if(!sklx.equals("�ֽ�")){
			return SUCCESS;
		}
		
		String _money = context.getReqParameter("ddsk_skje");
		double money = Double.parseDouble(_money.trim().equals("")?"0":_money);   //���

	    
		PersonalCashBean pcb = new PersonalCashBean();
		pcb.setXm(op.getName());
		pcb.setXmid(op.getLoginId());
		pcb.setBm((String)op.getAttribute("jiudian"));
		pcb.setBmid(op.getOrganizeId()+"");
		pcb.setMoney(money);
		
		initMoney(pcb);
    	
	
		return SUCCESS;
	}
	
	/**
	 * ��ʼ�����տ�ʱ���ã�
	 * @param pcb
	 * @throws Exception
	 */
	public void initMoney(PersonalCashBean pcb)throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn=ConnectionManager.getInstance().getConnection();
			
			String sql = "select count(*) from grxjtj where xmid='"+pcb.getXmid()+"'";
			
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			
			if(rs.next() && rs.getInt(1)>0){
				sql = "update grxjtj set xjye=ifnull(xjye,0)+"+pcb.getMoney()+" where xmid='"+pcb.getXmid()+"'";
			}
			else{
				sql = "insert into grxjtj(guid,xmid,xm,bm,bmid,xjye,ljjcw) values('"+new GUID().toString()+"','"+pcb.getXmid()+"','"+pcb.getXm()+"','"+pcb.getBm()+"','"+pcb.getBmid()+"',"+pcb.getMoney()+",0)";
			}
			
			FormLogger.log("xjtj", sql);
			
			conn.createStatement().executeUpdate(sql);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		finally
		{
			ConnectionManager.close(conn);
		}
	}
	public void receiveMoney(PersonalCashBean pcb,String jsr,String jsrid)throws Exception{
		Connection conn = null;

		try{
			/**
			 * �����˽�����
			 */
			conn=ConnectionManager.getInstance().getConnection();
			
			String sql = "update grxjtj set xjye=ifnull(xjye,0)-"+pcb.getMoney()+" where xmid='"+pcb.getXmid()+"'";
			
			FormLogger.log("xjtj", sql);
			
			conn.createStatement().executeUpdate(sql);
			
			/**
			 * �����˽���ʼ��
			 */
			MemberNameProxy.SimpleDepartment dept = MemberNameProxy.getInstance().getDepartmentByUserId(jsrid);
			pcb.setXm(jsr);
			pcb.setXmid(jsrid);
			pcb.setBm(dept.getName());
			pcb.setBmid(dept.getOrganizeId());
			
			initMoney(pcb);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		finally
		{
			ConnectionManager.close(conn);
		}
		
	}

	/**
	 * �����ۼƽ������ֽ�ת������ȷ��ʱ���ã�
	 * @param pcb
	 * @throws Exception
	 */
	public void calcCwMoney(PersonalCashBean pcb) throws Exception{
		Connection conn = null;

		try{
			conn=ConnectionManager.getInstance().getConnection();
			
			String sql = "update grxjtj set xjye=xjye-"+pcb.getMoney()+",ljjcw=ljjcw+"+pcb.getMoney()+" where xmid='"+pcb.getXmid()+"'";
			
			FormLogger.log("xjtj", sql);
			
			conn.createStatement().executeUpdate(sql);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		finally
		{
			ConnectionManager.close(conn);
		}
	}
	public String getErrorMessage() {
		return null;
	}
}
