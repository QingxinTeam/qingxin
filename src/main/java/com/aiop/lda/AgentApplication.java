package com.aiop.lda;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableAsync
@ServletComponentScan
public class AgentApplication extends WebMvcConfigurerAdapter  {
	private static Logger logger=LogManager.getLogger(AgentApplication.class.getName());
	
	 //favorPathExtension表示是否支持后缀匹配
	   @Override
	   public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
	         configurer.favorPathExtension(false);
	   }

	public static void main(String args[]) {
		try {
			SpringApplication.run(AgentApplication.class, args);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
		
	}
}
