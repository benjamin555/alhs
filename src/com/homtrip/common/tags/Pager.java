package com.homtrip.common.tags;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.homtrip.common.Page;
import com.homtrip.service.impl.FreeMarkerService;
import com.homtrip.utils.ComponentFactory;



/**
* @author 陈嘉镇
* @version 创建时间：2014-11-19 下午3:21:29
* @email benjaminchen555@gmail.com
*/
@SuppressWarnings({"serial","rawtypes"})
public class Pager extends TagSupport  {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private String html;
	private String formId;
	
	@Override
	public int doEndTag() throws JspException {
		render();
		try {
			pageContext.getOut().write(html);
		} catch (IOException e) {
			logger.error("error.",e);
		} 
		return EVAL_PAGE;
	}

	
	protected void render() {
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			String contextPath = request.getContextPath();
			Page p = (Page) request.getAttribute("page");
			String path = contextPath+url;
			render(path, p);
	        
	}

	@SuppressWarnings("unchecked")
	protected void render(String path, Page p) {
		// 获取页面输出流，并输出字符串
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("url", path);
		map.put("formId", getFormId()); 
		try {
			map.putAll(BeanUtils.describe(p));
		} catch (IllegalAccessException e) {
			logger.error("error.",e);
		} catch (InvocationTargetException e) {
			logger.error("error.",e);
		} catch (NoSuchMethodException e) {
			logger.error("error.",e);
		}
		FreeMarkerService freeMarkerService = (FreeMarkerService) ComponentFactory.getBean("freeMarkerService");
		 html = freeMarkerService.populateTempalte("pager.ftl", map );
		logger.debug("html:{}",html);
	}

	
	private String url;
 


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHtml() {
		return html;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

}
