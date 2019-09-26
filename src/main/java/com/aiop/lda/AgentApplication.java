package com.aiop.lda;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ServletComponentScan
public class AgentApplication extends SpringBootServletInitializer  {
	private static Logger logger=LogManager.getLogger(AgentApplication.class.getName());
	
	  @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return application.sources(AgentApplication.class);
	    }

	public static void main(String args[]) {
		try {
			SpringApplication.run(AgentApplication.class, args);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
		
	}
}
