package {app.id};

import java.util.HashMap;
import java.util.Map;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServlet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
	
	@Value("${proxy.urlMapping}")
	String urlMapping;
	
	@Value("${proxy.targetUrl}")
	String targetUrl;
	
	@Bean
	/**
	 * This bean registers the SAP proxy servlet.
	 * Spring boot does not use a web.xml
	 * @return
	 */
	public ServletRegistrationBean<HttpServlet> proxyServlet() {
		
		ServletRegistrationBean<HttpServlet> servRegBean = new ServletRegistrationBean<>();
		servRegBean.setServlet(new ProxyServlet());
		servRegBean.addUrlMappings(urlMapping);
		servRegBean.setLoadOnStartup(1);
		
		Map<String, String> initParameters = new HashMap<String, String>();
		initParameters.put("targetUrl", targetUrl);
		servRegBean.setInitParameters(initParameters);
		
		// this is necessary to support multipart
		servRegBean.setMultipartConfig(new MultipartConfigElement(""));
		
		return servRegBean;
	}
}