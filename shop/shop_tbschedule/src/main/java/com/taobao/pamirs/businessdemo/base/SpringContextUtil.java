package com.taobao.pamirs.businessdemo.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * spring context获取BEAN
 * <p>
 * Company: 北京新视星空传媒广告有限公司
 * </p>
 * @version 1.0
 */
public class SpringContextUtil implements ApplicationContextAware {

	private final static Logger logger = LoggerFactory.getLogger(SpringContextUtil.class.getName());

	/**
	 * 全局ApplicationContext
	 */
	private static ApplicationContext applicationContext;

	/**
	 * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		logger.info("注入ApplicationContext到SpringContextHolder！");

		if (null != SpringContextUtil.applicationContext)
			logger.info("SpringContextHolder中的ApplicationContext被覆盖!");
		else
			logger.info("SpringContextHolder中的ApplicationContext为空，将初始化全局ApplicationContext！");
		SpringContextUtil.applicationContext = applicationContext;
	}

	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 */
	public static ApplicationContext getApplicationContext() {
		checkApplicationContext();
		return applicationContext;
	}

	/**
	 * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		checkApplicationContext();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型. 如果有多个Bean符合Class, 取出第一个.
	 */

	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> requiredType) {
		checkApplicationContext();
		return (T) applicationContext.getBeansOfType(requiredType);
	}

	/**
	 * 清除applicationContext静态变量.
	 */
	public static void cleanApplicationContext() {
		applicationContext = null;
	}

	private static void checkApplicationContext() {
		if (applicationContext == null) {
			throw new IllegalStateException("applicaitonContext未注入");
		}
	}
}
