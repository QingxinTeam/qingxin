package com.aiop.lda.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class HttpClient {
	private static final Logger logger = LogManager.getLogger(HttpClient.class.getName());
	
	public static String doGet(String httpurl){
		HttpURLConnection connection=null;
		InputStream is=null;
		BufferedReader br =null;
		String result ="";
	    try {
			URL url =new URL(httpurl);
			connection=(HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(60000);
			connection.connect();
			if(connection.getResponseCode()==200){
				is = connection.getInputStream();
				br=new BufferedReader(new InputStreamReader(is,"UTF-8"));
				StringBuffer sbf=new StringBuffer();
				String temp=null;
				while((temp=br.readLine())!=null){
					sbf.append(temp);
					sbf.append("\r\n");
				}
				result=sbf.toString();
			}
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	    return result;
	}
	
	/**
	 * 
	 * @param httpurl
	 * @param param name1=value1&name2=value2
	 * @return
	 */
	public static String doPost(String httpurl,String paramjson){
		try {
			CloseableHttpClient httpClient=HttpClients.createDefault();
			HttpPost httpPost=new HttpPost(httpurl);
			httpPost.addHeader(HTTP.CONTENT_TYPE,"application/json");
			StringEntity stringEntity=new StringEntity(paramjson);
			httpPost.setEntity(stringEntity);
			HttpResponse response=httpClient.execute(httpPost);
			int status=response.getStatusLine().getStatusCode();
			if(status>=200 && status<300){
				HttpEntity entity=response.getEntity();
				return EntityUtils.toString(entity);
			}
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		
	    return "";
	}
	
	
	public static String doDelete(String httpurl){
		HttpURLConnection connection=null;
		InputStream is=null;
		BufferedReader br =null;
		String result ="";
	    try {
			URL url =new URL(httpurl);
			connection=(HttpURLConnection) url.openConnection();
			connection.setRequestMethod("DELETE");
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(60000);
			connection.connect();
			if(connection.getResponseCode()==200){
				is = connection.getInputStream();
				br=new BufferedReader(new InputStreamReader(is,"UTF-8"));
				StringBuffer sbf=new StringBuffer();
				String temp=null;
				while((temp=br.readLine())!=null){
					sbf.append(temp);
					sbf.append("\r\n");
				}
				result=sbf.toString();
			}
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	    return result;
	}
}
 