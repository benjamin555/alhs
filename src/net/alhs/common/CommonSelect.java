package net.alhs.common;

import java.sql.ResultSet;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.chysoft.template.action.CommonSearch2Action;
import net.chysoft.template.action.CommonSearchAction;

/**
 * ͨ��ѡ�񣨾Ƶ꣬��԰������ �ȵȣ�
 * @author Administrator
 *
 */
public class CommonSelect implements I_TemplateAction
{
	
	public int execute(TemplateContext context) throws Exception {
		/**
		 * ʱ����������
		 */
		//��ס���ڣ�����begindate���� - ����enddate����
		String t1 = context.getReqParameter("begindate"); 
		String t2 = context.getReqParameter("enddate");
		
		//�˷����ڣ�����t1���� - ����t2����
		String t3 = context.getReqParameter("t1");
		String t4 = context.getReqParameter("t2");
		
		//�������ڣ�����t5���� - ����t6����
		String t5 = context.getReqParameter("qlrq1");
		String t6 = context.getReqParameter("qlrq2");
 
		t1 = (t1!=null && t1.trim().equals(""))?null:t1;
		t2 = (t2!=null && t2.trim().equals(""))?null:t2;
		t3 = (t3!=null && t3.trim().equals(""))?null:t3;
		t4 = (t4!=null && t4.trim().equals(""))?null:t4;
		t5 = (t5!=null && t5.trim().equals(""))?null:t5;
		t6 = (t6!=null && t6.trim().equals(""))?null:t6;
		
		String sql = context.getListObjectPara().getQuerySql();
		
		String whereOrAnd = (sql.indexOf("where")==-1)?" where ":" and ";
		
		/**
		 * ��ס���ڲ�ѯ
		 */
		if((t1!=null && t2==null) || (t1==null && t2!=null)){
			sql += whereOrAnd+"ddzb.rzrq='"+(t1==null?t2:t1)+"'";
		}
		if(t1!=null && t2!=null){
			sql += whereOrAnd+"ddzb.rzrq>='"+t1+"' and ddzb.rzrq<='"+t2+"'";
		}
		/**
		 * �˷����ڲ�ѯ
		 */
		if((t3!=null && t4==null) || (t3==null && t4!=null)){
			sql += whereOrAnd+"ddzb.tfrq='"+(t3==null?t4:t3)+"'";
		}
		if(t3!=null && t4!=null){
			sql += whereOrAnd+"ddzb.tfrq>='"+t3+"' and ddzb.tfrq<='"+t4+"'";
		}
		
		/**
		 * ��ס���ں��˷�����ͬʱ��ѯ
		 */
		if(t1!=null && t2!=null && t3!=null && t4!=null){
			sql += whereOrAnd+"ddzb.rzrq>='"+t1+"' and ddzb.rzrq<='"+t2+"' and ddzb.tfrq>='"+t3+"' and ddzb.tfrq<='"+t4+"'";
		}
		
		/**
		 * �������ڲ�ѯ
		 */
		if((t5!=null && t6==null) || (t5==null && t6!=null)){
			sql += whereOrAnd+"ddzb.qlsj>='"+(t5==null?t6:t5)+" 00:00:00' and ddzb.qlsj<='"+(t5==null?t6:t5)+" 23:59:59'";
		}
		if(t5!=null && t6!=null){
			sql += whereOrAnd+"ddzb.qlsj>='"+t5+" 00:00:00' and ddzb.qlsj<='"+t6+" 23:59:59'";
		}
		
		context.getListObjectPara().setTransitionSql(sql);
		
		
		
			/**
			 * Ȩ��
			 */
		    if(context.getTemplate().getVariable("itemId")!=null){
		    	new CommonSearch2Action().execute(context);   //û�����̲���
		    }
		
			
		
		
		
		/**
		 * ��������
		 */
		addOption(context);
		
		return SUCCESS;
	}
	
	/**
	 * ���������
	 * @param context
	 * @throws Exception
	 */
	public void addOption(TemplateContext context)throws Exception{
		String jiudian = context.getReqParameter("jd");
		String huayuan = context.getReqParameter("hy");
		String fangxing = context.getReqParameter("fx");
		String huxing = context.getReqParameter("hx");
		String jingguan = context.getReqParameter("jg");
		String chuangxing = context.getReqParameter("cx");
		
		StringBuffer sqlBuffer = new StringBuffer();  //sql 
		
		String sql=context.getListObjectPara().getBaseSql();
		int fromIndex = sql.indexOf("from");
		String selectToFrom = sql.substring(6, fromIndex);
		
		//ǰ̨��������Ϣ���в�ѯ��
		if(sql.indexOf("qtmc")!=-1){
			sqlBuffer.append(sql.replaceAll(selectToFrom, " distinct qtxx.qtmc,'1' as type ")).append(" union ");
		}
		//ǰ̨��ǰ̨���ı�
		else if(sql.indexOf("qtzx.mc")!=-1){
			sqlBuffer.append(sql.replaceAll(selectToFrom, " distinct qtzx.mc,'1' as type ")).append(" union ");
		}
		//��԰
		if(sql.indexOf("fwzb.xqhy")!=-1){
			sqlBuffer.append(sql.replaceAll(selectToFrom, " distinct fwzb.xqhy,'2' as type ")).append(" union ");
		}
		//����
		if(sql.indexOf("fwzb.fangxing")!=-1){
			sqlBuffer.append(sql.replaceAll(selectToFrom, " fwzb.fangxing,'3' as type ")).append(" union ");
		}
		//����
		if(sql.indexOf("fxxx.hx")!=-1){
			sqlBuffer.append(sql.replaceAll(selectToFrom, " fxxx.hx,'4' as type ")).append(" union ");
		}
	    //����
		if(sql.indexOf("fxxx.jg")!=-1){
			sqlBuffer.append(sql.replaceAll(selectToFrom, " fxxx.jg,'5' as type ")).append(" union ");
		}
		//����
		if(sql.indexOf("fxxx.cx")!=-1){
			sqlBuffer.append(sql.replaceAll(selectToFrom, " fxxx.cx,'6' as type ")).append(" union ");
		}

		StringBuffer qt_option = new StringBuffer("<select class=style_box name=jd><option></option>");
		StringBuffer hy_option = new StringBuffer("<select class=style_box name=hy><option></option>");
		StringBuffer fx_option = new StringBuffer("<select class=style_box name=fx><option></option>");
		StringBuffer hx_option = new StringBuffer("<select class=style_box name=hx><option></option>");
		StringBuffer jg_option = new StringBuffer("<select class=style_box name=jg><option></option>");
		StringBuffer cx_option = new StringBuffer("<select class=style_box name=cx><option></option>");
		
		//System.out.println(sqlBuffer.toString().substring(0,sqlBuffer.length()-6));
		ResultSet rs0 = context.getConn().createStatement().executeQuery(sqlBuffer.substring(0,sqlBuffer.length()-6));
		
		while(rs0.next())
		{
			String type = rs0.getString(2);
			String value = rs0.getString(1);
			
			StringBuffer option = new StringBuffer();
			option.append("<option value='").append(value).append("'");
			
            //�Ƶ�
			if(type.equals("1")){
				option.append((jiudian!=null && jiudian.equals(value))?" selected >":">").append(value).append("</option>");
				qt_option.append(option);
			}
			//��԰
			else if(type.equals("2")){
				option.append((huayuan!=null && huayuan.equals(value))?" selected >":">").append(value).append("</option>");
				hy_option.append(option);
			}
			//����
			else if(type.equals("3")){
				option.append((fangxing!=null && fangxing.equals(value))?" selected >":">").append(value).append("</option>");
				fx_option.append(option);
			}
			//����
			else if(type.equals("4")){
				option.append((huxing!=null && huxing.equals(value))?" selected >":">").append(value).append("</option>");
				hx_option.append(option);
			}
            //����
			else if(type.equals("5")){
				option.append((jingguan!=null && jingguan.equals(value))?" selected >":">").append(value).append("</option>");
				jg_option.append(option);
			}
			//����
			else if(type.equals("6")){
				option.append((chuangxing!=null && chuangxing.equals(value))?" selected >":">").append(value).append("</option>");
				cx_option.append(option);
			}
		}
		
		context.put("jiudian", qt_option.append("</select>").toString());
		context.put("huayuan", hy_option.append("</select>").toString());
		context.put("fangxing", fx_option.append("</select>").toString());
		context.put("huxing", hx_option.append("</select>").toString());
		context.put("jingguan", jg_option.append("</select>").toString());
		context.put("chuangxing", cx_option.append("</select>").toString());

	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
