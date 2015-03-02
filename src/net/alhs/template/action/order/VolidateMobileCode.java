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
 * 验证码确认
 * @author Administrator
 *
 */
public class VolidateMobileCode implements I_TemplateAction{
	private final static String MOBILE_VALID_CODE = "$_interval_code_$";

	public int execute(TemplateContext context) throws Exception {
		HttpServletRequest request = context.getRequest();
		String guid = context.getReqParameter("yzGuid");        //业主验证存储业主GUID
		String khly = context.getReqParameter("ddzb_ddsx");     //客户来源
		String xm = context.getReqParameter("ddzb_xm");
		String lxdh = context.getReqParameter("ddzb_lxdh");

		String code = context.getReqParameter("dynamiccode");   //验证码
		
		/**
		 * 单号
		 */
		new SerialNumberAction().execute(context);
		

		/**
		 * 预定单
		 */
		//String ydjzrq = context.getReqParameterValues("rdrq")[0];              //预定截止时间

		
		
		/**
		 * 非业主订单处理(姓名，电话需填其中一个)
		 */
		if(!khly.equals("YZ")){
			if(!xm.trim().equals("") || !lxdh.trim().equals("")){
				return SUCCESS;
			}
			else{
				request.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE, "alert(\"请填写姓名或联系电话\");parent.document.getElementById(\"_btn\").disabled=0;");	 
				return END;    //不提交订单数据
			}
		}

		/**
		 * 房间是否重复下单
		 */
		String houseGuid = context.getReqParameter("houseGuid");
		String [] rq = context.getReqParameterValues("rq");
		List list = new FangTai(context.getConn()).isHaveOrder(houseGuid, rq);
		if(list!=null && list.size()>0){    //有重复的日期的订单
			StringBuffer sb = new StringBuffer("日期：");
			for (int i = 0; i < list.size(); i++) {
				sb.append("[").append(list.get(i)).append("]");
			}

			request.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE, "alert('"+sb.toString()+"已存在该房间的订单')");	 
			return END;    //不提交订单数据
		}

		//已确认业主身份
		if(!guid.trim().equals("")){
			context.getTableFieldByAlias("ddzb.status").setFieldValue("4");        //订单状态：已订
			context.getTableFieldByAlias("ddzb.ddskzt").setFieldValue("2");        //订单收款状态：已担保
			context.getTableFieldByAlias("ddzb.zdqxsj_xs").setFieldValue("");      //自动取消时间（小时）
			context.getTableFieldByAlias("ddzb.zdqxsj_fz").setFieldValue("");      //自动取消时间（分钟）
			return SUCCESS;
		}


		//验证码为空
		if(code.trim().equals("")){
			if(xm.trim().equals("") || lxdh.trim().equals("")){
				request.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE, "alert(\"验证码为空，姓名和联系电话必须填写\");parent.document.getElementById(\"_btn\").disabled=0;");	 
				return END;    //不提交订单数据
			}
			else{
				return SUCCESS;
			}
		}
		else{

			//单向MD5加密
			code = new MD5().getMD5ofStr(code);
			Long t = (Long)request.getSession().getAttribute("$_interval_$");
			if(t != null)
			{
				long t1 = System.currentTimeMillis();
				if(t1 - t.longValue() > 1000 * 360)
				{
					request.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE, "alert(\"手机验证码已经过期，业主验证未通过\")");
					return SUCCESS;
				}
			}
			else
			{
				request.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE,"alert(\"手机验证码未获取，业主验证未通过\")");
				return SUCCESS;
			}
			String vCode = (String)request.getSession().getAttribute(MOBILE_VALID_CODE);

			if(!code.equals(vCode))
			{//输入比对错误

				if(xm.trim().equals("") || lxdh.trim().equals("")){
					request.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE, "alert(\"验证码错误，姓名和联系电话必须填写\");parent.document.getElementById(\"_btn\").disabled=0;");	 
					return END;    //不提交订单数据
				}
				else{
					request.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE,"alert(\"手机验证码错误，业主验证未通过\");");
					return SUCCESS;
				}

			}

			/**
			 * 验证通过
			 */
			String yzGuid = (String)request.getSession().getAttribute("yzGuid");

			ResultSet rs = context.getConn().createStatement().executeQuery("select name,personID,MobilePhone from yzzl where guid='"+yzGuid+"'");

			if(rs.next()){
				context.getTableFieldByAlias("ddzb.yzGuid").setFieldValue(yzGuid);                  //设置yzGuid的值
				context.getTableFieldByAlias("ddzb.xm").setFieldValue(rs.getString("name"));    
				context.getTableFieldByAlias("ddzb.sfz").setFieldValue(rs.getString("personID")); 
				context.getTableFieldByAlias("ddzb.lxdh").setFieldValue(rs.getString("MobilePhone"));

				context.getTableFieldByAlias("ddzb.status").setFieldValue("4");        //订单状态：已订
				context.getTableFieldByAlias("ddzb.ddskzt").setFieldValue("2");        //订单收款状态：已担保
				context.getTableFieldByAlias("ddzb.zdqxsj_xs").setFieldValue("");      //自动取消时间（小时）
				context.getTableFieldByAlias("ddzb.zdqxsj_fz").setFieldValue("");      //自动取消时间（分钟）
			}


			/**
			 * 清空会话
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
