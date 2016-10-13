package com.welisson.examples;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {
	
	private static final String CONFIG_LOCATION = "com.welisson.examples.config";
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		
		System.out.println("***** Initializing Application for"+ servletContext.getServerInfo()+" *****");
		
		//Create ApplicationContext
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.setConfigLocation(CONFIG_LOCATION);
		
		//Add the servlet mapping and make it initialize automatically
		DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
		ServletRegistration.Dynamic servlet = servletContext.addServlet("mvc-dispatcher", dispatcherServlet);
		
		servlet.addMapping("/");
		servlet.setAsyncSupported(true);
		servlet.setLoadOnStartup(1);
		
	}
	
}
