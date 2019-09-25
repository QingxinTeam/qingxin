package com.aiop.lda.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.aiop.lda.arg.LDA;
import com.aiop.lda.arg.LDAPredictor;
import com.aiop.lda.arg.LDAUtils;
import com.aiop.lda.emotion.EmotionModel;
import com.aiop.lda.fenci.Fenci;
import com.aiop.lda.service.LdaService;
import com.aiop.lda.service.ReportRequest;
import com.aiop.lda.util.DateUtil;
import com.aiop.lda.util.StringsUtil;
import com.alibaba.fastjson.JSONObject;

@RestController
@RequestMapping("/httpService")
public class AgentRestController {

	private static Logger logger=LogManager.getLogger(LdaService.class.getName());
	
	
	
	@RequestMapping(value = "/updateTopicName", produces = "text/html;charset=utf-8")
	@ResponseBody
	public void updateTopicName(@RequestBody HashMap<String, String> obj ) {
		String index=obj.get("index");
		String field=obj.get("field");
		String value=obj.get("value");
		logger.info("updateTopicName start,index:{},field:{},value:{}",index,field,value);
//		  index: index,       //行索引
//          field: field,       //列名
//          value: value        //cell值
		
		int id=Integer.valueOf(index);
		if("topicname".equalsIgnoreCase(field)) {
			HashMap<String,Object> param =new HashMap<String, Object>();
			param.put("topicname", value);
			LdaService.updateindex(LdaService.TWORDS_INDEX_NAME, id+1+"", param);
		}
	}
	
	
	@RequestMapping(value = "/editWords", produces = "text/html;charset=utf-8")
	@ResponseBody
	public String editWords(@RequestBody HashMap<String, String> obj ) {
		//type:stop|jieba|neg
		//method:del|add
		String type=obj.get("type");
		String methods=obj.get("method");
		String words=obj.get("stopwords");
		logger.info("editWords start,type:{},methods:{},words:{}",type,methods,words);
		if(StringsUtil.isBlank(words)){
			return "input is empty";
		}
		String[] wordlist=words.split(" ");
		List<HashMap<String,Object>> dbre=new ArrayList<HashMap<String,Object>>();
		List<String>ids=new ArrayList<String>();
		for(String word:wordlist){
			HashMap<String,Object> o=new HashMap<String, Object>();
			if("neg".equalsIgnoreCase(type)) {
				String[] w1=word.split("\\|");
				if(w1.length==2) {
				   o.put("word", w1[0]);
				   o.put("value", w1[1]);
				   o.put("type", type);
				}
			}else {
				o.put("word", word);
				o.put("type", type);
			}
			
			ids.add(type+o.get("word"));
			dbre.add(o);
		}
		if("add".equalsIgnoreCase(methods)){
			LdaService.insertBatchLineForMap(LdaService.WORD_INDEX_NAME, ids,dbre);
		}else if("del".equalsIgnoreCase(methods)){
			for(String id:ids){
				LdaService.deleteData(LdaService.WORD_INDEX_NAME,id);
			}
		}
		if("neg".equalsIgnoreCase(type)) {
			EmotionModel.loadEmotionDictory();
		}else {
			Fenci.init();
		}
		return "success";
	}
	
	@RequestMapping(value = "/queryStopWord", produces = "text/html;charset=utf-8")
	@ResponseBody
	public List<Map<String, Object>> queryStopWord( ) {
		logger.info("queryStopWord start");
		HashMap<String,String> param =new HashMap<String, String>();
		param.put("type", "stop");
		List<Map<String, Object>> result=LdaService.queryData(LdaService.WORD_INDEX_NAME,param);
		
		for(Map<String, Object> m:result) {
			Date datestr=DateUtil.StrToDateForPatternAddHour(m.get("crtTime")+"");
			m.put("crtTime", DateUtil.formatDate(datestr, DateUtil.YYYY_MM_DD_HHMMSS_SSS));
		}
		return result;
	}
	
	@RequestMapping(value = "/queryJiebaWord", produces = "text/html;charset=utf-8")
	@ResponseBody
	public List<Map<String, Object>> queryJiebaWord() {
		logger.info("queryStopWord start");
		HashMap<String,String> param =new HashMap<String, String>();
		param.put("type", "jieba");
		List<Map<String, Object>> result=LdaService.queryData(LdaService.WORD_INDEX_NAME,param);
		
		for(Map<String, Object> m:result) {
			Date datestr=DateUtil.StrToDateForPatternAddHour(m.get("crtTime")+"");
			m.put("crtTime", DateUtil.formatDate(datestr, DateUtil.YYYY_MM_DD_HHMMSS_SSS));
		}
		return result;
	}
	
	@RequestMapping(value = "/queryNegWord", produces = "text/html;charset=utf-8")
	@ResponseBody
	public List<Map<String, Object>> queryNegWord( ) {
		logger.info("queryNegWord start");
		HashMap<String,String> param =new HashMap<String, String>();
		param.put("type", "neg");
		List<Map<String, Object>> result=LdaService.queryData(LdaService.WORD_INDEX_NAME,param);
		
		for(Map<String, Object> m:result) {
			Date datestr=DateUtil.StrToDateForPatternAddHour(m.get("crtTime")+"");
			m.put("crtTime", DateUtil.formatDate(datestr, DateUtil.YYYY_MM_DD_HHMMSS_SSS));
		}
		return result;
	}


	@RequestMapping(value = "/modeltopic", produces = "text/html;charset=utf-8")
	@ResponseBody
	public String modeltopic(@RequestBody HashMap<String, String> obj ) {
		Integer min=Integer.valueOf(obj.get("minK"));
		logger.info("modeltopic start.minK="+min);
		LdaService.deleteIndex();
		Fenci.loadfileToEs();
    	LDA.k=min;
    	new LDA().run();
		return min+"";
	}
	
	
	@RequestMapping(value = "/finish", produces = "text/html;charset=utf-8")
	@ResponseBody
	public JSONObject finish(@RequestBody HashMap<String, String> obj) {
		JSONObject resultObj=new JSONObject();
		String msgtxt=obj.get("msgtxt");
		String msghtml=obj.get("msghtml");
		logger.info("finish start.msgtxt={},msghtml={}",msgtxt,msghtml);
		JSONObject tobj=theme(obj);
		System.out.println(tobj.toJSONString());
		double result=EmotionModel.predict(msgtxt);
		List<HashMap<String,Object>> lines=new ArrayList<HashMap<String,Object>>();
		
		HashMap<String,Object> line=new HashMap<String,Object>();
		line.put("msgtxt", msgtxt);
		line.put("msghtml", msghtml);
		line.put("theme", tobj.get("theme"));
		line.put("themeNum", tobj.get("maxd"));
		line.put("themeRate", tobj.get("rate"));
		line.put("feelNum", result);
		if(result>0) {
			line.put("neg", "正面情绪");
		}else if(result==0){
			line.put("neg", "正常");
		}else if(result<0) {
			line.put("neg", "负面情绪");
		}
		lines.add(line);
		LdaService.insertBatchLineForMap("zxj_lda_log", lines);
		
		return resultObj;
	}
	
	
	@RequestMapping(value = "/theme", produces = "text/html;charset=utf-8")
	@ResponseBody
	public JSONObject theme(@RequestBody HashMap<String, String> obj) {
		JSONObject resultObj=new JSONObject();
		String msgtxt=obj.get("msgtxt");
		logger.info("theme start.msgtxt="+msgtxt);
		double[] dist=LDAPredictor.theme(msgtxt);
		String result="";
		double maxd=0,mind=10000;
		int i=0,maxi=0;
		for (double d : dist) {
			i++;
			if(d>maxd) {
				maxd=d;
				maxi=i;
				result=result+" "+Math.round(LDAUtils.baoliu(d,7)*10000)/100 + "% ";
			}
			if(d<mind) {
				mind=d;
			}
		}
		if(maxd==mind) {
			resultObj.put("theme", "");
			resultObj.put("rate", result);
		}else {
			HashMap<String,String> param=new HashMap<String, String>();
			param.put("topic", maxi+"");
			List<Map<String, Object>> list=LdaService.queryData(LdaService.TWORDS_INDEX_NAME,param);
			if(list.size()>0) {
				resultObj.put("theme", list.get(0).get("topicname"));
				resultObj.put("maxd", maxi);
			}
			
			resultObj.put("rate", result);
		
		}
		
		logger.info("theme end.result="+result);
		return resultObj;
	}
	
	@RequestMapping(value = "/feel", produces = "text/html;charset=utf-8")
	@ResponseBody
	public double feel(@RequestBody HashMap<String, String> obj) {
		String msgtxt=obj.get("msgtxt");
		logger.info("feel start.msgtxt="+msgtxt);
		double result=EmotionModel.predict(msgtxt);
		logger.info("feel end.result="+result);
		return result;
	}
	
	@RequestMapping(value = "/querylog", produces = "text/html;charset=utf-8")
	@ResponseBody
	public List<Map<String, Object>> querylog(HttpServletRequest request) {
		logger.info("querylog start");
		String beginTimeStr=request.getParameter("beginTime");
		String endTimeStr=request.getParameter("endTime");
		Date beginTime=DateUtil.StrToDateForPattern(beginTimeStr, DateUtil.YYYY_MM_DD_HHMMSS_SSS);
		Date endTime=DateUtil.StrToDateForPattern(endTimeStr, DateUtil.YYYY_MM_DD_HHMMSS_SSS);
		beginTimeStr=DateUtil.DateToUTCstr(beginTime);
		endTimeStr=DateUtil.DateToUTCstr(endTime);
		HashMap<String,String> param =new HashMap<String, String>();
		param.put("neg.keyword", "负面情绪");
		
		List<Map<String, Object>> result=LdaService.queryDataByTime(LdaService.LOG_INDEX_NAME,param,beginTimeStr,endTimeStr);
		for(Map<String, Object> m:result) {
			Date datestr=DateUtil.StrToDateForPatternAddHour(m.get("crtTime")+"");
			m.put("crtTime", DateUtil.formatDate(datestr, DateUtil.YYYY_MM_DD_HHMMSS_SSS));
		}
	
		return result;
	}
	
	@RequestMapping(value = "/queryPer", produces = "text/html;charset=utf-8")
	@ResponseBody
	public Object queryPer(HttpServletRequest request) {
		logger.info("queryPer start");
		return ReportRequest.queryPer();
	}
	
	@RequestMapping(value = "/queryNum", produces = "text/html;charset=utf-8")
	@ResponseBody
	public Object queryNum(HttpServletRequest request) {
		logger.info("/queryNum start");
		JSONObject datas=(JSONObject) ReportRequest.queryNum();
		return datas;
	}
	
	@RequestMapping(value = "/queryFeel", produces = "text/html;charset=utf-8")
	@ResponseBody
	public Object queryFeel(HttpServletRequest request) {
		logger.info("/queryFeel start");
		JSONObject datas=(JSONObject) ReportRequest.queryFeel();
		return datas;
	}
	
	
	@RequestMapping(value = "/querytopic", produces = "text/html;charset=utf-8")
	@ResponseBody
	public List<Map<String, Object>> querytopic(HttpServletRequest request) {
		logger.info("querytopic start");
		List<Map<String, Object>> result=LdaService.queryData(LdaService.TWORDS_INDEX_NAME);
		
		
		
		Collections.sort(result, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (int)o1.get("topic")-(int)o2.get("topic");
			}
		});
		for(Map<String, Object> m:result){
			String words=(String) m.get("words");
			m.put("name", words.split(" ")[0]);
		}
		
		for(Map<String, Object> m:result) {
			Date datestr=DateUtil.StrToDateForPatternAddHour(m.get("crtTime")+"");
			m.put("crtTime", DateUtil.formatDate(datestr, DateUtil.YYYY_MM_DD_HHMMSS_SSS));
		}
		return result;
	}
	
	
	


	
	

}
