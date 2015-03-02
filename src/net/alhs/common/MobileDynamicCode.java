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
 * 短信动态的验证码
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
		 * 1、添加附件的javascript脚本
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
	 * 显示获取手机验证码的界面，输入界面
	 */
	public String doView() throws Exception
	{
		StringBuffer sb = new StringBuffer();

		sb.append("手机验证码<input id=\"").append(exec.getName()).append("__dynamiccode\" name=\"").append(exec.getName())
		.append("__dynamiccode\" size=4 style=\"border:1px solid #0000F0\"><a href=\"javascript:")
		.append(exec.getName()).append("_GetCode()\" style=\"text-decoration:none\"><font color=\"#0000F0\"> 获取</font></a>");

		return sb.toString();
	}

	/**
	 * 对动态验证的验证
	 */
	public void doPost() throws Exception
	{
		TemplateContext context = exec.getTemplatePara().getContext();
		String code = context.getReqParameter(exec.getName() + "__dynamiccode");

		if(code == null || code.equals(""))
		{
			throw new TemplateMessageException("手机验证码输入为空");
		}
		//单向MD5加密
		code = new MD5().getMD5ofStr(code);
		Long t = (Long)request.getSession().getAttribute("$_interval_$");
		if(t != null)
		{
			long t1 = System.currentTimeMillis();
			if(t1 - t.longValue() > 1000 * 360)
			{
				throw new TemplateMessageException("手机验证码已经过期，请重新获取");
			}
		}
		else
		{
			throw new TemplateMessageException("请先获取手机验证码");
		}
		String vCode = (String)request.getSession().getAttribute(MOBILE_VALID_CODE);

		if(!code.equals(vCode))
		{//输入比对错误
			throw new TemplateMessageException("手机验证码输入错误");
		}

		/**
		 * 清空会话
		 */
		request.getSession().removeAttribute("$_interval_$");
		request.getSession().removeAttribute(MOBILE_VALID_CODE);
	}

	/**
	 * 返回执行部件的对象
	 */
	public I_Component getComponent()
	{
		return exec;
	}

	/**
	 * 验证码存储的对象
	 */
	private final static String MOBILE_VALID_CODE = "$_interval_code_$";

	/**
	 * 返回一个验证码，并存在在会话中，有效时间为10分钟
	 * 获取验证码不能超过1分钟间隔
	 * @param request
	 */
	public static String getMobileDynamicCode(HttpServletRequest request)
	{
		Operator operator = Authentication.getUserFromSession(request);
		/**
		 * 1、判断是否为登录用户
		 */
		if(operator == null)
		{
			return "系统超时，请重新登录访问";
		}

		/**
		 * 2、获取时间的间隔，判断是否过于频繁
		 */
		Long t = (Long)request.getSession().getAttribute("$_interval_$");
		if(t != null)
		{
			long t1 = System.currentTimeMillis();
			if(t1 - t.longValue() < 1000 * 120)
			{
				return "获取手机验证码的间隔是2分钟，请稍候";
			}
		}

		/**
		 * 3、返回5位长度的验证码
		 */
		String code = (int)(Math.random() * 100000) + "";
		if(code.length() < 5) code = StringTools.getMultiChar("0", 5-code.length()) + code;

		String mobile = (String)operator.getAttribute("mobile");
		//System.out.println(mobile);

		/**
		 * 4、发送短信
		 */
		if(mobile == null || mobile.trim().equals(""))
		{
			return "当前用户的手机号码未设置，不能获取验证码";
		}
		try
		{//执行发送
			String msg = new ShutCutFactory().sendMobileMessage(new String[]{mobile}, "当前手机验证码："+code, -1, null, null);
			if(msg != null) return msg;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return ex.getMessage();
		}

		/**
		 * 5、会话中保存
		 */
		long tx = System.currentTimeMillis();
		request.getSession().setAttribute("$_interval_$", new Long(tx));
		//MD5单项加密
		request.getSession().setAttribute(MOBILE_VALID_CODE, new MD5().getMD5ofStr(code));

		String yzGuid = request.getParameter("yzGuid");         //业主验证,存储业主GUID
		if(yzGuid != null){
			request.getSession().setAttribute("yzGuid",yzGuid); //回话保存业主Guid，验证通过后关联到订单
		}


		/**
		 * 为空则在调用中无提示
		 */
		return "已经获取手机验证码，请等待短信发送";
	}

	/**
	 * **********************************添加的编译方法*******************************************
	 */
	public String getValidJavaScript(HashMap components) throws Exception
	{
		//获取执行部件对象
		ExecComponent com = (ExecComponent)components.get(this.getClass().getName());

		if(com == null) return "";
		/**
		 * 1、添加校验的脚本
		 */
		StringBuffer sb1 = new StringBuffer();
		sb1.append("    if(document.getElementById(\"").append(com.getName()).append("__dynamiccode\").value == \"\"){");
		sb1.append("alert(\"请输入手机验证码!\");document.getElementById(\"").append(com.getName()).append("__dynamiccode\").focus();return false;}");

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
