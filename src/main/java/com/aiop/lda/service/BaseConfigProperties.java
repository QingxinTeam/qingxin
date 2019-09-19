package com.aiop.lda.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
	
public class BaseConfigProperties {
	private static Logger logger=LogManager.getLogger(BaseConfigProperties.class.getName());
	public  static String ESLOG_HOST=null;
	public  static String ESLOG_CLUSTER=null;
	public  static List<String> KAFKA_HOSTS=new ArrayList<String>();
	public  static List<String> ZOOKEEPER_HOSTS=new ArrayList<String>();
	public static List<String> HBASE_MASTER_HOSTS=new ArrayList<String>();
	public static List<String> HBASE_SLAVE_HOSTS=new ArrayList<String>();
	public static String SPARK_MASTER=null;
	public static Integer INDEX_CLEAR_DATE=null;
	
	public static void loadConfigFile(){
		logger.info("*********************** loadConfigFile start !!!");
		loadEsLogConfigFile();
	}
	
	
	private static void loadEsLogConfigFile(){
		try {
			Properties properties = new Properties();
	    	InputStream resourceAsStream_es = BaseConfigProperties.class.getClassLoader().getResourceAsStream("es_m.conf");
			properties.load(resourceAsStream_es);
			INDEX_CLEAR_DATE = Integer.valueOf(properties.getProperty("index_clear_date"));
			ESLOG_HOST = properties.getProperty("es_Hosts");
			ESLOG_CLUSTER = properties.getProperty("cluster_name");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	

}
