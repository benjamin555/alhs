package net.alhs.template.action.order;

import java.sql.ResultSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import net.alhs.common.FangTai;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.business.template.action.SerialNumberAction;
import net.sysmain.common.I_CommonConstant;
import net.sysmain.util.MD5;
import net.sysmain.util.StringTools;
/**
 * ��֤��ȷ��
 * @author Administrator
 *
 */
public class VolidateMobileCode implements I_TemplateAction{
	private final static String MOBILE_VALID_CODE = "$_interval_code_$";

	public int execute(TemplateContext context) throws Exception {
		HttpServletRequest request = context.getRequest();
		String guid = context.getReqParameter("yzGuid");        //ҵ����֤�洢ҵ��GUID
		String khly = context.getReqParameter("ddzb_ddsx");     //�ͻ���Դ
		String xm = context.getReqParameter("ddzb_xm");
		String lxdh = context.getReqParameter("ddzb_lxdh");

		String code = context.getReqParameter("dynamiccode");   //��֤��
		
		/**
		 * ����
		 */
		new SerialNumberAction().execute(context);
		

		/**
		 * Ԥ����
		 */
		//String ydjzrq = context.getReqParameterValues("rdrq")[0];              //Ԥ����ֹʱ��

		
		
		/**
		 * ��ҵ����������(�������绰��������һ��)
		 */
		if(!khly.equals("YZ")){
			if(!xm.trim().equals("") || !lxdh.trim().equals("")){
				return SUCCESS;
			}
			else{
				request.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE, "alert(\"����д��������ϵ�绰\");parent.document.getElementById(\"_btn\").disabled=0;");	 
				return END;    //���ύ��������
			}
		}

		/**
		 * �����Ƿ��ظ��µ�
		 */
		String houseGuid = context.getReqParameter("houseGuid");
		String [] rq = context.getReqParameterValues("rq");
		List list = new FangTai(context.getConn()).isHaveOrder(houseGuid, rq);
		if(list!=null && list.size()>0){    //���ظ������ڵĶ���
			StringBuffer sb = new StringBuffer("���ڣ�");
			for (int i = 0; i < list.size(); i++) {
				sb.append("[").append(list.get(i)).append("]");
			}

			request.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE, "alert('"+sb.toString()+"�Ѵ��ڸ÷���Ķ���')");	 
			return END;    //���ύ��������
		}

		//��ȷ��ҵ�����
		if(!guid.trim().equals("")){
			context.getTableFieldByAlias("ddzb.status").setFieldValue("4");        //����״̬���Ѷ�
			context.getTableFieldByAlias("ddzb.ddskzt").setFieldValue("2");        //�����տ�״̬���ѵ���
			context.getTableFieldByAlias("ddzb.zdqxsj_xs").setFieldValue("");      //�Զ�ȡ��ʱ�䣨Сʱ��
			context.getTableFieldByAlias("ddzb.zdqxsj_fz").setFieldValue("");      //�Զ�ȡ��ʱ�䣨���ӣ�
			return SUCCESS;
		}


		//��֤��Ϊ��
		if(code.trim().equals("")){
			if(xm.trim().equals("") || lxdh.trim().equals("")){
				request.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE, "alert(\"��֤��Ϊ�գ���������ϵ�绰������д\");parent.document.getElementById(\"_btn\").disabled=0;");	 
				return END;    //���ύ��������
			}
			else{
				return SUCCESS;
			}
		}
		else{

			//����MD5����
			code = new MD5().getMD5ofStr(code);
			Long t = (Long)request.getSession().getAttribute("$_interval_$");
			if(t != null)
			{
				long t1 = System.currentTimeMillis();
				if(t1 - t.longValue() > 1000 * 360)
				{
					request.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE, "alert(\"�ֻ���֤���Ѿ����ڣ�ҵ����֤δͨ��\")");
					return SUCCESS;
				}
			}
			else
			{
				request.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE,"alert(\"�ֻ���֤��δ��ȡ��ҵ����֤δͨ��\")");
				return SUCCESS;
			}
			String vCode = (String)request.getSession().getAttribute(MOBILE_VALID_CODE);

			if(!code.equals(vCode))
			{//����ȶԴ���

				if(xm.trim().equals("") || lxdh.trim().equals("")){
					request.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE, "alert(\"��֤�������������ϵ�绰������д\");parent.document.getElementById(\"_btn\").disabled=0;");	 
					return END;    //���ύ��������
				}
				else{
					request.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE,"alert(\"�ֻ���֤�����ҵ����֤δͨ��\");");
					return SUCCESS;
				}

			}

			/**
			 * ��֤ͨ��
			 */
			String yzGuid = (String)request.getSession().getAttribute("yzGuid");

			ResultSet rs = context.getConn().createStatement().executeQuery("select name,personID,MobilePhone from yzzl where guid='"+yzGuid+"'");

			if(rs.next()){
				context.getTableFieldByAlias("ddzb.yzGuid").setFieldValue(yzGuid);                  //����yzGuid��ֵ
				context.getTableFieldByAlias("ddzb.xm").setFieldValue(rs.getString("name"));    
				context.getTableFieldByAlias("ddzb.sfz").setFieldValue(rs.getString("personID")); 
				context.getTableFieldByAlias("ddzb.lxdh").setFieldValue(rs.getString("MobilePhone"));

				context.getTableFieldByAlias("ddzb.status").setFieldValue("4");        //����״̬���Ѷ�
				context.getTableFieldByAlias("ddzb.ddskzt").setFieldValue("2");        //�����տ�״̬���ѵ���
				context.getTableFieldByAlias("ddzb.zdqxsj_xs").setFieldValue("");      //�Զ�ȡ��ʱ�䣨Сʱ��
				context.getTableFieldByAlias("ddzb.zdqxsj_fz").setFieldValue("");      //�Զ�ȡ��ʱ�䣨���ӣ�
			}


			/**
			 * ��ջỰ
			 */
			request.getSession().removeAttribute("$_interval_$");
			request.getSession().removeAttribute(MOBILE_VALID_CODE);
			request.getSession().removeAttribute("yzGuid");
		}

		return SUCCESS;
	}



	public String getErrorMessage() {
		return null;
	}
}
