package test.redisson.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * 类描述：service工厂
 * @author 技匠
 */
@Component
public class BeanFactory {
	
	public BeanFactory(ServletContext servletContext, Environment environment) {
		appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		ev = environment;
	}
	
	private static ApplicationContext appContext;
	private static Environment ev;
	
	public static <T> T getBean(Class<T> clazz) {
		return appContext.getBean(clazz);
	}
	
	public static <T> T getProperty(String name, Class<T> clazz) {
		return ev.getProperty(name, clazz);
	}
}
