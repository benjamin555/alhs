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
 * 现金交接相关处理
 * @author Administrator
 *
 */
public class XjjjEndAction implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {

		String jjzt = context.getReqParameter("xjjj_jjzt");                    //交接状态


		//暂存
		if(jjzt==null || jjzt.equals("")){
			return SUCCESS;
		}
		//交接中(确定)
		else if(jjzt.equals("1")){
            confirm(context);
		}
		//接受
		else if(jjzt.equals("2")){
           accept(context);
		}
		//拒绝
		else if(jjzt.equals("4")){
           refused(context);
		}
		//财务确认
		else if(jjzt.equals("3")){
           financialConfirm(context);
		}
		
		
		
		return SUCCESS;
	}

	/**
	 * 确定
	 * @param context
	 * @throws Exception
	 */
	public void confirm(TemplateContext context)throws Exception{
		/**
		 * 1.增加提醒 
		 */
		createMessage(context);
		
		/**
		 * 2.流程参与（接收人）
		 */
		String jsrid = context.getReqParameter("xjjj_jsrid");    //接收人id
		String xjjjGuid = context.getReqParameter("guid");
		
		context.getConn().createStatement().executeUpdate("insert into lccy(guid,userid) values('"+xjjjGuid+"','"+jsrid+"')");
		

	}
	/**
	 * 接受处理
	 * @param context
	 * @throws Exception
	 */
	public void accept(TemplateContext context)throws Exception{
		Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象
		Connection conn = context.getConn();
		PreparedStatement ps = null;
		
		String xjjjGuid = context.getReqParameter("guid");
		String [] ddskGuids = context.getReqParameterValues("ddskGuids");
		String sqrGuid = context.getReqParameter("xjjj_sqrguid");  
		String jsrid = context.getReqParameter("xjjj_jsrid");           //接收人id
		String jsr = context.getReqParameter("xjjj_jsr");               //接收人
		String _zje = context.getReqParameter("xjjj_zje");              //金额

		/**
		 * 已处理（通过流程参与控制）
		 */
		insertLccy(context, ddskGuids, sqrGuid);

		/**
		 * 修改最后接收人
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
		 * 删除提醒
		 */
		removeMessage(xjjjGuid);

		/**
		 * 清空收款表中的交接状态（jjzt=0（可再次交接））
		 */
		changeStatus(context, ddskGuids,"0");
		
		
		/**
		 * 个人现金统计
		 */
		double zje = Double.parseDouble(_zje.trim().equals("")?"0":_zje);
		PersonalCashBean pcb = new PersonalCashBean();
		pcb.setXmid(sqrGuid);
		pcb.setMoney(zje);
		
		PersonalCashStatistics pc = new PersonalCashStatistics();
		pc.receiveMoney(pcb, jsr, jsrid);
		
		
		/**
		 * 记录日志
		 */
	}
	
	/**
	 * 拒绝
	 * @param context
	 * @throws Exception
	 */
	public void refused(TemplateContext context)throws Exception{
		/**
		 *  1.清空收款表中的交接状态（jjzt=0（可再次交接））
		 */
		changeStatus(context, context.getReqParameterValues("ddskGuids"),"0");
		
		/**
		 *  2.删除提醒
		 */
		removeMessage(context.getReqParameter("guid"));
		
	}
	
	/**
	 * 财务确认
	 * @param context
	 * @throws Exception
	 */
	public void financialConfirm(TemplateContext context)throws Exception{
		Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象
		Connection conn = context.getConn();
		PreparedStatement ps = null;

		String xjjjGuid = context.getReqParameter("guid");
		String [] ddskGuids = context.getReqParameterValues("ddskGuids");
		String sqrGuid = context.getReqParameter("xjjj_sqrguid");   
		String jsrid = context.getReqParameter("xjjj_jsrid");    //接收人id
		String _zje = context.getReqParameter("xjjj_zje");       //金额
		

		/**
		 * 1.1修改最后接收人
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
		 * 1.2删除提醒
		 */
		removeMessage(xjjjGuid);
		
		
		/**
		 * 2.修改交接主表的财务确认人，财务确认时间
		 */
	    sql = "update xjjjb set cwclr=?,cwqrsj=now() where guid=?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, op.getName());
		ps.setString(2, xjjjGuid);
		ps.executeUpdate();
		
		/**
		 * 3.修改订单收款项的财务确认人，财务确认时间
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
		 * 4.1.财务参与流程
		 *   2.已处理（通过流程参与控制）
		 */
		insertLccy(context, ddskGuids, jsrid);

		insertLccy(context, ddskGuids, sqrGuid);
		
		/**
		 * 5.订单收款 jjzt='2' 表示完成
		 */
		changeStatus(context, ddskGuids, "2");
		
		/**
		 * 6.订单是否完成
		 */
		if(ddskGuids!=null && ddskGuids.length>0){
			OrderIsComplate oc = new OrderIsComplate();
			for (int i = 0; i < ddskGuids.length; i++) {
				oc.isComplate(context, ddskGuids[i]);
			}
		}
		
		/**
		 * 7.累计交财务
		 */
		double zje = Double.parseDouble(_zje.trim().equals("")?"0":_zje);
		PersonalCashBean pcb = new PersonalCashBean();
		pcb.setXmid(sqrGuid);
		pcb.setMoney(zje);
		
		PersonalCashStatistics pc = new PersonalCashStatistics();
		pc.calcCwMoney(pcb);
	}
	
	
	/**
	 * 创建消息
	 * @param context
	 * @throws Exception
	 */
	public void createMessage(TemplateContext context)throws Exception{
		Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象
		String xjjjGuid = context.getReqParameter("guid");   
		String dh = context.getTableFieldByAlias("xjjj.jjdh").getFieldValue();        //单号
		
		//System.out.println(dh+"!!!!");
		String jsrid = context.getReqParameter("xjjj_jsrid");    //接收人id
		
		String content = "现金交接单["+dh+"]需接受确认";
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
	 * 删除消息
	 * @param context
	 * @throws Exception
	 */
	public void removeMessage(String guid)throws Exception{
		ShutCutFactory fac = new ShutCutFactory();
		fac.deleteMessage(guid);
	}
	
	/**
	 * 流程参与
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
	 * 改变收款表的jjzt 0可交接（未选择）  1不可交接（已选择） 2财务确认（完成）
	 * @param context
	 * @param ddskGuids
	 * @throws Exception
	 */
    public void changeStatus(TemplateContext context,String [] ddskGuids,String status)throws Exception{
    	Connection conn = context.getConn();
		PreparedStatement ps = null;
		String sql = "update fwddsk set jjzt=? where guid=?";  //jjzt 0可交接（未选择）  1不可交接（已选择） 2财务确认（完成）
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
