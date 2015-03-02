package com.homtrip.service.impl;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
* @author 陈嘉镇
* @version 创建时间：2014-12-8 下午5:07:18
* @email benjaminchen555@gmail.com
*/
@Service
public class FreeMarkerService {
	private Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	@SuppressWarnings("rawtypes")
	public String populateTempalte(String template, Map<String,Object> map) {
		String content = null;
		try {
			Template tpl = freeMarkerConfigurer.getConfiguration().getTemplate(template);
			content = FreeMarkerTemplateUtils.processTemplateIntoString(tpl, map);
		} catch (IOException e) {
			log.error("封装FreeMarker内容出现异常", e);
		} catch (TemplateException e) {
			log.error("封装FreeMarker内容出现异常", e);
		}
		return content;
	}

}