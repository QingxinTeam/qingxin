package com.aiop.lda.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aiop.lda.util.ESConstants;
import com.aiop.lda.util.ElasticSearchHandler;
import com.aiop.lda.util.HttpClient;

public class LdaService {
	private static Logger logger=LogManager.getLogger(LdaService.class.getName());
	private static ElasticSearchHandler api;
	public static final String PER_INDEX_NAME="zxj_lda_per";
	
	public static final String FENCI_INDEX_NAME="zxj_lda_data";
	public static final String TASSIGN_INDEX_NAME="zxj_lda_assign";
	public static final String WORDMAP_INDEX_NAME="zxj_lda_wordmap";
	public static final String THETA_INDEX_NAME="zxj_lda_theta";
	public static final String PHI_INDEX_NAME="zxj_lda_phi";
	public static final String TWORDS_INDEX_NAME="zxj_lda_twords";
	public static final String MAX_THETA_INDEX_NAME="zxj_lda_max_theta";
	public static final String OPTION_INDEX_NAME="zxj_lda_option";
	public static final String LOG_INDEX_NAME="zxj_lda_log";
	public static final String WORD_INDEX_NAME="zxj_lda_word";
	
	static{
		BaseConfigProperties.loadConfigFile();
		api=ElasticSearchHandler.getInstance(BaseConfigProperties.ESLOG_HOST,BaseConfigProperties.ESLOG_CLUSTER);
	}
	
	public  static void deleteIndex() {
		String delurl="";
//		String[] deltables=new String[]{WORDMAP_INDEX_NAME,TASSIGN_INDEX_NAME,THETA_INDEX_NAME,MAX_THETA_INDEX_NAME,PHI_INDEX_NAME,TWORDS_INDEX_NAME};
		String[] deltables=new String[]{WORD_INDEX_NAME};
		for(String d: deltables){
			delurl = "http://" + BaseConfigProperties.ESLOG_HOST + ":9200/"+ d;
			logger.info(HttpClient.doDelete(delurl));
		}
		
	}
	
	public static void deleteData(String indexname,String id){
		api.deleteData(indexname, ESConstants.DATA, id);
	}
	
	
	public static List<Map<String, Object>> queryData(String index){
		 return api.queryEsIndex(index,ESConstants.DATA,null);
	}
	
	public static List<Map<String, Object>> queryData(String index,HashMap<String,String> param){
		 return api.queryEsIndex(index,ESConstants.DATA,param);
	}
	
	public static void updateindex(String index,String id,HashMap<String,Object> param){
		  api.upsertIndexMap(index,ESConstants.DATA,id,param);
	}
	
	public static List<Map<String, Object>> queryDataByTime(String index,HashMap<String,String> param,String startTime,String endTime){
		 return api.queryEsIndexByTime(index,ESConstants.DATA,param,startTime,endTime);
		
	}
	
	public static  void insertPLog(double p,int k) {
		List<String>ids=new ArrayList<String>();
		List<HashMap<String,Object>> insertMaps=new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("perplexity", p);
		paramMap.put("topicNum", k);
		ids.add(k+"");
		paramMap.put("crtTime", new Date());
		insertMaps.add(paramMap);
		
		try {
			api.bulkInsertMap(PER_INDEX_NAME, ESConstants.DATA, ids, insertMaps);
			logger.info("插入ES:" + PER_INDEX_NAME );
		} catch (Exception e) {
			logger.error("插入ES出错:" + PER_INDEX_NAME , e);
		}
	}
	
	public static void insertBatchLine(String indexname,List<String> lines){
		List<HashMap<String,Object>> insertMaps=new ArrayList<HashMap<String,Object>>();
		for(int i=0;i<lines.size();i++){
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("line", lines.get(i));
			paramMap.put("crtTime", new Date());
			insertMaps.add(paramMap);
		}
		
		try {
			api.bulkInsertMap(indexname, ESConstants.DATA, null, insertMaps);
			logger.info("插入ES:" + indexname+" size="+lines.size() );
		} catch (Exception e) {
			logger.error("插入ES出错:" + indexname , e);
		}
	}
	
	
	public static void insertBatchLineForMap(String indexname,List<HashMap<String,Object>> lines){
		for(int i=0;i<lines.size();i++){
			lines.get(i).put("crtTime", new Date());
		}
		
		try {
			api.bulkInsertMap(indexname, ESConstants.DATA, null, lines);
			logger.info("插入ES:" + indexname+" size="+lines.size() );
		} catch (Exception e) {
			logger.error("插入ES出错:" + indexname , e);
		}
	}
	
	
	public static void insertBatchLineForMap(String indexname,List<String>ids,List<HashMap<String,Object>> lines){
		for(int i=0;i<lines.size();i++){
			lines.get(i).put("crtTime", new Date());
		}
		
		try {
			api.bulkInsertMap(indexname, ESConstants.DATA, ids, lines);
			logger.info("插入ES:" + indexname+" size="+lines.size() );
		} catch (Exception e) {
			logger.error("插入ES出错:" + indexname , e);
		}
	}
	
	
	public static void insertBatchLineForIds(String indexname,List<String>ids,List<String> lines) {
		List<HashMap<String,Object>> insertMaps=new ArrayList<HashMap<String,Object>>();
		for(int i=0;i<lines.size();i++){
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("line", lines.get(i));
			paramMap.put("crtTime", new Date());
			paramMap.put("linenum",ids.get(i) );
			insertMaps.add(paramMap);
		}
		
		try {
			api.bulkInsertMap(indexname, ESConstants.DATA, ids, insertMaps);
			logger.info("插入ES:" + indexname+" size="+lines.size() );
		} catch (Exception e) {
			logger.error("插入ES出错:" + indexname , e);
		}
	}
	
	public static  void insertModelTAssign(double p,int k) {
		String indexname = "zxj_lda_tassion";
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("perplexity", p);
		paramMap.put("topicNum", k);
		try {
			api.insert(indexname, ESConstants.DATA, null, paramMap);
			logger.info("插入ES:" + indexname );
		} catch (IOException e) {
			logger.error("插入ES出错:" + indexname , e);
		}
	}
	
	public static void main(String[] args) {
		deleteIndex();
	}
	
}
