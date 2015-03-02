package net.alhs.common; 

import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.business.engine.common.I_Compile;
import net.business.engine.common.I_Component;
import net.business.engine.common.I_ExecObject;
import net.business.engine.common.TemplateContext;
import net.business.engine.control.ExecComponent;
import net.chysoft.common.ShutCutFactory;
import net.sysmain.common.exception.TemplateMessageException;
import net.sysmain.util.MD5;
import net.sysmain.util.StringTools;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;

/**
 * ���Ŷ�̬����֤��
 * @author Owner
 *
 */
public class MobileDynamicCode implements I_ExecObject,I_Compile
{
	private HttpServletRequest request = null;
	ExecComponent exec = null;

	public void setParameter(ServletContext context, HttpServletRequest request, HttpServletResponse response)
	{
		this.request = request;
		exec = (ExecComponent) this.request.getAttribute(EXEC_COMPONENT);


		/**
		 * 1����Ӹ�����javascript�ű�
		 */
		StringBuffer sb = new StringBuffer();
		String fn = exec.getName() + "_clb";

		sb.append("var ").append(fn).append(" = function(xmlhttp)\r\n");
		sb.append("{\r\n");
		sb.append("    var str = xmlhttp.responseText;\r\n");
		sb.append("    if(str != null && str != \"\") alert(str);\r\n");
		sb.append("}\r\n");

		sb.append("function ").append(exec.getName()).append("_GetCode()\r\n{\r\n");
		sb.append("    new chysoftXMLHttp(\"/innet/ext/mcode.jsp\", ").append(fn).append(").submit();\r\n}");

		this.exec.addScriptCode(sb.toString());
	}

	/**
	 * ��ʾ��ȡ�ֻ���֤��Ľ��棬�������
	 */
	public String doView() throws Exception
	{
		StringBuffer sb = new StringBuffer();

		sb.append("�ֻ���֤��<input id=\"").append(exec.getName()).append("__dynamiccode\" name=\"").append(exec.getName())
		.append("__dynamiccode\" size=4 style=\"border:1px solid #0000F0\"><a href=\"javascript:")
		.append(exec.getName()).append("_GetCode()\" style=\"text-decoration:none\"><font color=\"#0000F0\"> ��ȡ</font></a>");

		return sb.toString();
	}

	/**
	 * �Զ�̬��֤����֤
	 */
	public void doPost() throws Exception
	{
		TemplateContext context = exec.getTemplatePara().getContext();
		String code = context.getReqParameter(exec.getName() + "__dynamiccode");

		if(code == null || code.equals(""))
		{
			throw new TemplateMessageException("�ֻ���֤������Ϊ��");
		}
		//����MD5����
		code = new MD5().getMD5ofStr(code);
		Long t = (Long)request.getSession().getAttribute("$_interval_$");
		if(t != null)
		{
			long t1 = System.currentTimeMillis();
			if(t1 - t.longValue() > 1000 * 360)
			{
				throw new TemplateMessageException("�ֻ���֤���Ѿ����ڣ������»�ȡ");
			}
		}
		else
		{
			throw new TemplateMessageException("���Ȼ�ȡ�ֻ���֤��");
		}
		String vCode = (String)request.getSession().getAttribute(MOBILE_VALID_CODE);

		if(!code.equals(vCode))
		{//����ȶԴ���
			throw new TemplateMessageException("�ֻ���֤���������");
		}

		/**
		 * ��ջỰ
		 */
		request.getSession().removeAttribute("$_interval_$");
		request.getSession().removeAttribute(MOBILE_VALID_CODE);
	}

	/**
	 * ����ִ�в����Ķ���
	 */
	public I_Component getComponent()
	{
		return exec;
	}

	/**
	 * ��֤��洢�Ķ���
	 */
	private final static String MOBILE_VALID_CODE = "$_interval_code_$";

	/**
	 * ����һ����֤�룬�������ڻỰ�У���Чʱ��Ϊ10����
	 * ��ȡ��֤�벻�ܳ���1���Ӽ��
	 * @param request
	 */
	public static String getMobileDynamicCode(HttpServletRequest request)
	{
		Operator operator = Authentication.getUserFromSession(request);
		/**
		 * 1���ж��Ƿ�Ϊ��¼�û�
		 */
		if(operator == null)
		{
			return "ϵͳ��ʱ�������µ�¼����";
		}

		/**
		 * 2����ȡʱ��ļ�����ж��Ƿ����Ƶ��
		 */
		Long t = (Long)request.getSession().getAttribute("$_interval_$");
		if(t != null)
		{
			long t1 = System.currentTimeMillis();
			if(t1 - t.longValue() < 1000 * 120)
			{
				return "��ȡ�ֻ���֤��ļ����2���ӣ����Ժ�";
			}
		}

		/**
		 * 3������5λ���ȵ���֤��
		 */
		String code = (int)(Math.random() * 100000) + "";
		if(code.length() < 5) code = StringTools.getMultiChar("0", 5-code.length()) + code;

		String mobile = (String)operator.getAttribute("mobile");
		//System.out.println(mobile);

		/**
		 * 4�����Ͷ���
		 */
		if(mobile == null || mobile.trim().equals(""))
		{
			return "��ǰ�û����ֻ�����δ���ã����ܻ�ȡ��֤��";
		}
		try
		{//ִ�з���
			String msg = new ShutCutFactory().sendMobileMessage(new String[]{mobile}, "��ǰ�ֻ���֤�룺"+code, -1, null, null);
			if(msg != null) return msg;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return ex.getMessage();
		}

		/**
		 * 5���Ự�б���
		 */
		long tx = System.currentTimeMillis();
		request.getSession().setAttribute("$_interval_$", new Long(tx));
		//MD5�������
		request.getSession().setAttribute(MOBILE_VALID_CODE, new MD5().getMD5ofStr(code));

		String yzGuid = request.getParameter("yzGuid");         //ҵ����֤,�洢ҵ��GUID
		if(yzGuid != null){
			request.getSession().setAttribute("yzGuid",yzGuid); //�ػ�����ҵ��Guid����֤ͨ�������������
		}


		/**
		 * Ϊ�����ڵ���������ʾ
		 */
		return "�Ѿ���ȡ�ֻ���֤�룬��ȴ����ŷ���";
	}

	/**
	 * **********************************��ӵı��뷽��*******************************************
	 */
	public String getValidJavaScript(HashMap components) throws Exception
	{
		//��ȡִ�в�������
		ExecComponent com = (ExecComponent)components.get(this.getClass().getName());

		if(com == null) return "";
		/**
		 * 1�����У��Ľű�
		 */
		StringBuffer sb1 = new StringBuffer();
		sb1.append("    if(document.getElementById(\"").append(com.getName()).append("__dynamiccode\").value == \"\"){");
		sb1.append("alert(\"�������ֻ���֤��!\");document.getElementById(\"").append(com.getName()).append("__dynamiccode\").focus();return false;}");

		return sb1.toString();
	}

	public String getCompileTopHtml(HashMap arg0) throws Exception
	{
		return "";
	}

	public String getCompileTailHtml(HashMap arg0) throws Exception
	{
		return "";
	}

	public String getFormElements(HashMap arg0) throws Exception
	{
		return "";
	}

	public int getFormEncodingType()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public String getCompileAlertMessage()
	{
		// TODO Auto-generated method stub
		return "";
	}
}
