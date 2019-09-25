package com.aiop.lda.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aiop.lda.util.HttpClient;
import com.aiop.lda.util.SQLUtils;
import com.alibaba.fastjson.JSONObject;

public class ReportRequest {
	private static final Logger logger = LoggerFactory.getLogger(ReportRequest.class);
	private static String esHost="";
	private static Map<String, String>sqls=null;
	
	static{
		BaseConfigProperties.loadConfigFile();
		sqls=SQLUtils.getSQLList();
		try {
			esHost = BaseConfigProperties.ESLOG_HOST;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Object queryPer(){
		String httpurl="http://"+esHost+":9200/zxj_lda_per/_search";
		String param=sqls.get("queryPer");
		String s= HttpClient.doPost(httpurl,param);
		JSONObject jsonObject=JSONObject.parseObject(s);
		JSONObject resultObj=new JSONObject();
		if(jsonObject!=null && jsonObject.containsKey("aggregations") && jsonObject.getJSONObject("aggregations").containsKey("table_name_term")){
			JSONObject aggObject=jsonObject.getJSONObject("aggregations").getJSONObject("table_name_term");
			List<JSONObject> alist=(List<JSONObject>) aggObject.get("buckets");
			
			List<JSONObject> results=new ArrayList<JSONObject>();
			
			
			for(JSONObject o : alist){
				JSONObject tmp= new JSONObject();
				Long topicNum=o.getLongValue("key");
				Integer perplexity=o.getJSONObject("max_read").getIntValue("value");
				tmp.put("label", topicNum);
				tmp.put("value", perplexity);
				results.add(tmp);
			}
			Collections.sort(results, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					return o1.getIntValue("label")-o2.getIntValue("label");
				}
			});
			if(results.size()>50){
				results=results.subList(0, 50);
			}
			
			
			////////////////////二次转化/////////////////
			List<String> legends=new ArrayList<String>();
			legends.add("最佳分类选择");
			
			List<String> cates=new ArrayList<String>();
			List<Integer> datas=new ArrayList<Integer>();
			
			for(JSONObject o : results){
				cates.add(o.getString("label"));
				datas.add(o.getInteger("value"));
			}
			JSONObject series= new JSONObject();
			series.put("data", datas);
			series.put("type", "line");
			series.put("name", "困惑度");
			resultObj.put("legends", legends);
			resultObj.put("dataset", series);
			resultObj.put("category", cates);
		}
		return resultObj;
	}
	
	
	@SuppressWarnings("unchecked")
	public static Object queryNum(){
		List<Map<String, Object>> result=LdaService.queryData(LdaService.TWORDS_INDEX_NAME);
		Collections.sort(result, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (int)o1.get("topic")-(int)o2.get("topic");
			}
		});
		
		String httpurl="http://"+esHost+":9200/zxj_lda_max_theta/_search";
		String param=sqls.get("queryNum");
		String s= HttpClient.doPost(httpurl,param);
		JSONObject jsonObject=JSONObject.parseObject(s);
		JSONObject resultObj=new JSONObject();
		if(jsonObject!=null && jsonObject.containsKey("aggregations") && jsonObject.getJSONObject("aggregations").containsKey("table_name_term")){
			JSONObject aggObject=jsonObject.getJSONObject("aggregations").getJSONObject("table_name_term");
			List<JSONObject> alist=(List<JSONObject>) aggObject.get("buckets");
			List<JSONObject> results=new ArrayList<JSONObject>();
			
			for(JSONObject o : alist){
				JSONObject tmp= new JSONObject();
				int topicNum=o.getIntValue("key");
				Integer count=o.getIntValue("doc_count");
				tmp.put("label", result.get(topicNum-1).get("topicname"));
				tmp.put("value", count);
				results.add(tmp);
			}
			if(results.size()>50){
				results=results.subList(0, 50);
			}
			
			
			////////////////////二次转化/////////////////
			List<String> legends=new ArrayList<String>();
			legends.add("分类占比");
			
			List<String> cates=new ArrayList<String>();
			List<JSONObject> datas=new ArrayList<JSONObject>();
			
			for(JSONObject o : results){
				cates.add(o.getString("label"));
				JSONObject t= new JSONObject();
				t.put("name", o.getString("label"));
				t.put("value",o.getInteger("value"));
				datas.add(t);
			}
			resultObj.put("legends", legends);
			resultObj.put("dataset", datas);
			resultObj.put("category", cates);
		}
		return resultObj;
	}
	
	
	@SuppressWarnings("unchecked")
	public static Object queryFeel(){
		String httpurl="http://"+esHost+":9200/zxj_lda_log/_search";
		String param=sqls.get("queryFeel");
		String s= HttpClient.doPost(httpurl,param);
		JSONObject jsonObject=JSONObject.parseObject(s);
		JSONObject resultObj=new JSONObject();
		if(jsonObject!=null && jsonObject.containsKey("aggregations") && jsonObject.getJSONObject("aggregations").containsKey("table_name_term")){
			JSONObject aggObject=jsonObject.getJSONObject("aggregations").getJSONObject("table_name_term");
			List<JSONObject> alist=(List<JSONObject>) aggObject.get("buckets");
			
			List<JSONObject> results=new ArrayList<JSONObject>();
			
			
			for(JSONObject o : alist){
				JSONObject tmp= new JSONObject();
				String topicNum=o.getString("key");
				Integer count=o.getIntValue("doc_count");
				tmp.put("label", topicNum);
				tmp.put("value", count);
				results.add(tmp);
			}
			if(results.size()>50){
				results=results.subList(0, 50);
			}
			
			
			////////////////////二次转化/////////////////
			List<String> legends=new ArrayList<String>();
			legends.add("分类占比");
			
			List<String> cates=new ArrayList<String>();
			List<JSONObject> datas=new ArrayList<JSONObject>();
			
			for(JSONObject o : results){
				cates.add(o.getString("label"));
				JSONObject t= new JSONObject();
				t.put("name", o.getString("label"));
				t.put("value",o.getInteger("value"));
				datas.add(t);
			}
			resultObj.put("legends", legends);
			resultObj.put("dataset", datas);
			resultObj.put("category", cates);
		}
		return resultObj;
	}
	
	

}
