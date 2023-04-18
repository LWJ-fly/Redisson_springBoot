package test.redisson.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * 类描述：service工厂
 * @author 技匠
 */
@Component
public class ServiceFactory {
	
	public ServiceFactory(ServletContext servletContext) {
		appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
	}
	
	private static ApplicationContext appContext;
	
	public static <T> T getBean(Class<T> clazz) {
		return appContext.getBean(clazz);
	}
}
