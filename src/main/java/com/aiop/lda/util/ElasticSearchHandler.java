package com.aiop.lda.util;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
public class ElasticSearchHandler {
	private static final Logger logger = LogManager.getLogger(ElasticSearchHandler.class.getName());
    private static String[] esHosts = null;
	private static Integer es_Port= 9300;
	private final static String defaultClusterName="elasticsearch";
	private final static String defaultIndexname="csup_cardinfo";
	private final static String defaultType="doc";
	
	private static TransportClient client;
	
	public static ElasticSearchHandler handler=null;
	
	/**
	 * 
	 * @param hosts：es集群ip,多个用逗号隔开 如182.180.183.169,182.180.183.176
	 * @param clusterName:集群名字
	 * @return 
	 */
	public static ElasticSearchHandler getInstance(String hosts,String clusterName){
		if (handler==null) {
			handler=new ElasticSearchHandler(hosts,clusterName);
		}
		return handler;
	}
	
	public static ElasticSearchHandler getInstance(String hosts){
		return getInstance(hosts, defaultClusterName);
	}
	
	private ElasticSearchHandler(String hosts,String clusterName) {
		try {
			esHosts = hosts.split(",");
			logger.warn("====ElasticSearchHandler="+hosts);
			Settings settings = Settings.builder().put("cluster.name",clusterName).build();// 设置ES实例的名称
			System.setProperty("es.set.netty.runtime.available.processors", "false");
			client = new PreBuiltTransportClient(settings);
			for (String host : esHosts) {
				logger.warn("====host=" + host);
				client.addTransportAddress(new TransportAddress(InetAddress.getByName(host), es_Port));
				logger.warn("====end host=" + host);
			}
		} catch (Throwable e) {
			logger.error("----- can not found ES conf file -----"+e.getMessage(),e);
			throw new RuntimeException("can not found ES conf file");
		}
	}
	
	
	public void close(){
		if (handler!=null) {
			logger.info("es client closed---------");
			client.close();
		}
	}
	
	/**
	 * 
	 * @param indexname 索引名字
	 * @param type 类型
	 * @param id 主键
	 * @param updateParam 修改变量
	 */
	public boolean updateIndex(String indexname,String type,String id,XContentBuilder updateParam){
		logger.debug("========queryEsIndex updateParam"+updateParam.toString()+" id="+id);
		logger.info("- ====es indexname is "+indexname);
		UpdateRequest updateRequest = new UpdateRequest(indexname, type, id);
		updateRequest.doc(updateParam);
		try {
			client.update(updateRequest).get();
		} catch (Throwable e) {
			logger.error(e.getMessage());
			return false;
		} 	
		return true;
	}
	
	 public boolean bulkUpdate(String indexname,String type,List<String>idList,List<XContentBuilder> updateList){
	    	BulkRequestBuilder bulkRequest=client.prepareBulk();
	    	for(int i=0;i<updateList.size();i++){
	        	bulkRequest.add(client.prepareUpdate(indexname, type, idList.get(i)).setDoc(updateList.get(i)));
	        }
	    	BulkResponse bulkResponse=bulkRequest.get();
	    	if(bulkResponse.hasFailures()){
	    		logger.error("bulkResponse update error!"+bulkResponse.buildFailureMessage());
	    		return false;
	    	}
	    	return true;
	    }
	 
	 
	 /**
		 * 修改es索引,id不存在则新建
		 * @param indexname
		 * @param type
		 * @param id
		 * @param updateParam
		 * @return
		 */
		public boolean upsertIndexMap(String indexname,String type,String id,HashMap<String,Object> updateParam){
			XContentBuilder xBuilder = null;
			try {
				xBuilder=XContentFactory.jsonBuilder().startObject();
				for(String key : updateParam.keySet()){
					xBuilder.field(key, updateParam.get(key));
				}
				xBuilder.endObject();
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			}
			
			
			IndexRequest indexRequest=new IndexRequest(indexname, type,id);
			indexRequest.source(xBuilder);
			
			UpdateRequest updateRequest = new UpdateRequest(indexname, type, id);
			updateRequest.doc(xBuilder).upsert(indexRequest);
			
			try {
				client.update(updateRequest).get();
			} catch (Throwable e) {
				logger.error(e.getMessage());
				return false;
			} 	
			return true;
		}
		
		 public  List<Map<String, Object>> queryEsIndexByTime(String indexname,String type,HashMap<String,String> param,String startTime,String endTime) {
		    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		    	try{
		    		
		    		RangeQueryBuilder rangeQueryBuilder=QueryBuilders.rangeQuery("crtTime").from(startTime).to(endTime);
		            BoolQueryBuilder queryBuilder=QueryBuilders.boolQuery();
		            queryBuilder.must(rangeQueryBuilder);
		            if(param!=null){
		            	for(String name : param.keySet()){
		        			String value=param.get(name);
		        			queryBuilder.must(QueryBuilders.termQuery(name, value));
		        			
		        		}
		            }
		            
		            
		            SearchResponse searchResponse =  client.prepareSearch(indexname).setIndices(indexname).setTypes(type).setQuery(queryBuilder).setFrom(0).setSize(10000).execute().actionGet();
		            SearchHits hits = searchResponse.getHits();
		            SearchHit[] searchHists = hits.getHits();
		            for (SearchHit hit : searchHists) {
		                list.add(hit.getSourceAsMap());
		            }
		    	}catch(Throwable e){
		    		logger.info(e.getMessage(),e);
		    	}
		        return list;
		    }
	 
	/**
	 * 查询API
	 * @param indexname 索引名字
	 * @param type 类型
	 * @param param 查询参数
	 * @return 注意此方法最多返回1000条数据
	 */
    public  List<Map<String, Object>> queryEsIndex(String indexname,String type,HashMap<String,String> param) {
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	try{
    		
            BoolQueryBuilder queryBuilder=QueryBuilders.boolQuery();
            if(param!=null){
            	for(String name : param.keySet()){
        			String value=param.get(name);
        			queryBuilder.must(QueryBuilders.termQuery(name, value));
        			
        		}
            }
            
            
            SearchResponse searchResponse =  client.prepareSearch(indexname).setIndices(indexname).setTypes(type).setQuery(queryBuilder).setFrom(0).setSize(10000).execute().actionGet();
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHists = hits.getHits();
            for (SearchHit hit : searchHists) {
                list.add(hit.getSourceAsMap());
            }
    	}catch(Throwable e){
    		logger.info(e.getMessage(),e);
    	}
        return list;
    }
    
	
    public  List<Map<String, Object>> queryEsIndex(HashMap<String,String> param) {
    	return queryEsIndex(defaultIndexname,defaultType,param);
    }
    
    /**
     * 批量插入API
     * @param indexname
     * @param type
     * @param idList 主键列表，与insertList对应
     * @param insertList 具体每一行数据
     * @return
     */
    public boolean bulkInsert(String indexname,String type,List<String>idList,List<XContentBuilder> insertList){
    	BulkRequestBuilder bulkRequest=client.prepareBulk();
    	if(idList==null || idList.size()==0){
    		for(int i=0;i<insertList.size();i++){
        		bulkRequest.add(client.prepareIndex(indexname, type).setSource(insertList.get(i)));
        	}
    	}else{
    		for(int i=0;i<insertList.size();i++){
        		bulkRequest.add(client.prepareIndex(indexname, type, idList.get(i)).setSource(insertList.get(i)));
        	}
    	}
    	BulkResponse bulkResponse=bulkRequest.get();
    	if(bulkResponse.hasFailures()){
    		logger.error("bulkResponse insert error!"+bulkResponse.buildFailureMessage());
    		return false;
    	}
    	return true;
    }
    
    
    
    public boolean bulkInsertMap(String indexname,String type,List<String>idList,List<HashMap<String,Object>> insertMaps){
    	List<XContentBuilder> insertList=new ArrayList<XContentBuilder>();
    	for(HashMap<String,Object> map : insertMaps ){
    		try {
    			XContentBuilder xBuilder = XContentFactory.jsonBuilder().startObject();
    			for(String key : map.keySet()){
    				xBuilder.field(key, map.get(key));
    			}
    			xBuilder.endObject();
    			insertList.add(xBuilder);
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			}
    	}
    	
    	
    	BulkRequestBuilder bulkRequest=client.prepareBulk();
    	if(idList==null || idList.size()==0){
    		for(int i=0;i<insertList.size();i++){
        		bulkRequest.add(client.prepareIndex(indexname, type).setSource(insertList.get(i)));
        	}
    	}else{
    		for(int i=0;i<insertList.size();i++){
        		bulkRequest.add(client.prepareIndex(indexname, type, idList.get(i)).setSource(insertList.get(i)));
        	}
    	}
    	BulkResponse bulkResponse=bulkRequest.get();
    	if(bulkResponse.hasFailures()){
    		logger.error("bulkResponse insert error!"+bulkResponse.buildFailureMessage());
    		return false;
    	}
    	return true;
    }
    
    /**
     * 单条插入API
     * @param indexname
     * @param type
     * @param insertParam
     */
	public boolean insert(String indexname,String type,String id,XContentBuilder insertParam){
		IndexRequest indexRequest=new IndexRequest(indexname, type,id);
		if(id==null){
			indexRequest=new IndexRequest(indexname, type);
		}
		  indexRequest.source(insertParam);
		  try {
				client.index(indexRequest);
			} catch (Throwable e) {
				logger.error(e.getMessage());
				return false;
			} 
		  return true;
	}
	
	
	public boolean insert(String indexname, String type, String id, Map<String, Object> paramMap) throws IOException {

		XContentBuilder insertParam = createXContentBuilder(paramMap);
		IndexRequest indexRequest = null;
		if (id == null) {
			indexRequest = new IndexRequest(indexname, type);
		} else {
			indexRequest = new IndexRequest(indexname, type, id);
		}
		indexRequest.source(insertParam);
		try {
			client.index(indexRequest);
		} catch (Throwable e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}
	
	public void deleteData(String indexname,String type,String id){
		client.prepareDelete(indexname, type, id).get();
	}
	
	
	public XContentBuilder createXContentBuilder(Map<String, Object> paramMap) throws IOException {

		XContentBuilder xcb = XContentFactory.jsonBuilder().startObject();
		for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
			xcb.field(entry.getKey(), entry.getValue());
		}

		xcb.endObject();
		return xcb;
	}

	
	
    
    
}
