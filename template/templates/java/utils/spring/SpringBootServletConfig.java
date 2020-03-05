package {service.namespace}.utils.spring;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.MultipartConfigElement;
import javax.transaction.UserTransaction;

import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import {service.namespace}.processor.OdataserviceServiceSingleProcessor;
import {service.namespace}.ui.service.VariablesServlet;

@Configuration
@EnableTransactionManagement
public class SpringBootServletConfig {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	UserTransaction userTransaction;
	
    @Bean
    public OdataserviceServiceSingleProcessor odataServiceSingleProcessor()  {
    	
    	return new OdataserviceServiceSingleProcessor(em, userTransaction);
    }
    
    @Bean
    public CommonsMultipartResolver multiPartResolver(){

        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        return resolver;
    }
    
	@Bean
	public ServletRegistrationBean<CXFNonSpringJaxrsServlet> getODataServletRegistrationBean() {
		
		ServletRegistrationBean<CXFNonSpringJaxrsServlet> odataServletRegistrationBean = new ServletRegistrationBean<>(new CXFNonSpringJaxrsServlet(), "/OdataserviceService.svc/*");
		
		Map<String, String> initParameters = new HashMap<String, String>();
		initParameters.put("javax.ws.rs.Application", "org.apache.olingo.odata2.core.rest.app.ODataApplication");
		initParameters.put("org.apache.olingo.odata2.service.factory", "{service.namespace}.OdataserviceServiceFactory");
		odataServletRegistrationBean.setInitParameters(initParameters);
		
		odataServletRegistrationBean.setMultipartConfig(new MultipartConfigElement(""));
		
		return odataServletRegistrationBean;
	}
 	
	@Bean
	public ServletRegistrationBean<VariablesServlet> getVariablesServletRegistrationBean() {
		
		return new ServletRegistrationBean<>(new VariablesServlet(), "/variables/*");
	}
}
