package com.homtrip.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.sysplat.access.Authentication;
import net.sysplat.access.Parameters;
import net.sysplat.common.I_ResourceConstant;
import net.sysplat.common.LanguageOfSysplat;

/**
* @author 陈嘉镇
* @version 创建时间：2015-2-7 上午11:36:27
* @email benjaminchen555@gmail.com
*/
public class AuthFilter implements Filter{

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
			ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		if(!Authentication.isLoginAuth(httpServletRequest))
		{
		    try
		    {
		    	httpServletRequest.setAttribute(I_ResourceConstant.ALERT_MESSAGE, LanguageOfSysplat.AUTHENTICATION[LanguageOfSysplat.getIndex()][2]);
		    	httpServletRequest.setAttribute(I_ResourceConstant.ALERT_URL, new String[]{ "<a target=\"_top\" href=\"" + httpServletRequest.getContextPath() + Parameters.getLogonEntrance() + 
		        					 "\">" + LanguageOfSysplat.GLOBAL_INFO[LanguageOfSysplat.getIndex()][1] + "</a>"});
		    	httpServletRequest.getRequestDispatcher("/sysplat/message.jsp").forward(httpServletRequest, response);
		        return;
		    }
		    catch(Exception ex)
		    {
		        ex.printStackTrace();
		    }
		}
		filterChain.doFilter(httpServletRequest, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
