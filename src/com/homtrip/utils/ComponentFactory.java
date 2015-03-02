package com.homtrip.utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ComponentFactory implements ApplicationContextAware {
	private static Logger log =  LoggerFactory.getLogger(ComponentFactory.class);

	private static boolean initialized = false;

	private static ApplicationContext applicationContext = null;

	public static BeanFactory factory = null;
	static String webBasePath;

	public static Object getBean(String id) {
		try {
			if (applicationContext != null) {
				return applicationContext.getBean(id);
			}
			return factory.getBean(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static synchronized void destroy() {
		factory = null;
		applicationContext = null;
		initialized = false;
	}

	public void setApplicationContext(ApplicationContext context) {
		log.info("setApplicationContext");
		applicationContext = context;
		initialized = true;
	}

	public static void lookBeans(ConfigurableListableBeanFactory x) {
		System.out.println(" bean µÄÊýÁ¿£º" + x.getBeanDefinitionCount());
	}
}
