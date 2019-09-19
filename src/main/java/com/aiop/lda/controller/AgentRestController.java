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
	
	@RequestMapping(value = "/editWords", produces = "text/html;charset=utf-8")
	@ResponseBody
	public String editWords(@RequestBody HashMap<String, String> obj ) {
		//type:stop|jieba
		//method:del|add
		String type=obj.get("type");
		String methods=obj.get("method");
		String words=obj.get("stopwords");
		if(StringsUtil.isBlank(words)){
			return "input is empty";
		}
		String[] wordlist=words.split(" ");
		List<HashMap<String,Object>> dbre=new ArrayList<HashMap<String,Object>>();
		List<String>ids=new ArrayList<String>();
		for(String word:wordlist){
			HashMap<String,Object> o=new HashMap<String, Object>();
			o.put("word", word);
			ids.add(type+word);
			o.put("type", type);
			dbre.add(o);
		}
		if("add".equalsIgnoreCase(methods)){
			LdaService.insertBatchLineForMap(LdaService.WORD_INDEX_NAME, ids,dbre);
		}else if("del".equalsIgnoreCase(methods)){
			for(String id:ids){
				LdaService.deleteData(LdaService.WORD_INDEX_NAME,id);
			}
		}
		
		Fenci.init();
		
		return "success";
	}
	
	@RequestMapping(value = "/queryStopWord", produces = "text/html;charset=utf-8")
	@ResponseBody
	public List<Map<String, Object>> queryStopWord(@RequestBody HashMap<String, String> obj ) {
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
	public List<Map<String, Object>> queryJiebaWord(@RequestBody HashMap<String, String> obj ) {
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
	
	
	@RequestMapping(value = "/theme", produces = "text/html;charset=utf-8")
	@ResponseBody
	public String theme(@RequestBody HashMap<String, String> obj) {
		String msgtxt=obj.get("msgtxt");
		logger.info("theme start.msgtxt="+msgtxt);
		double[] dist=LDAPredictor.theme(msgtxt);
		String result="";
		for (double d : dist) {
			result=result+" "+Math.round(LDAUtils.baoliu(d,7)*10000)/100 + "% ";
		}
		logger.info("theme end.result="+result);
		return result;
	}
	
	@RequestMapping(value = "/feel", produces = "text/html;charset=utf-8")
	@ResponseBody
	public String feel(@RequestBody HashMap<String, String> obj) {
		String msgtxt=obj.get("msgtxt");
		String msgstr=obj.get("msgstr");
		logger.info("theme start.msgtxt="+msgtxt);
		double result=EmotionModel.predict(msgtxt);
		if(result<0) {
			saveEs(msgtxt,msgstr ,result);
		}
		logger.info("theme end.result="+result);
		return result+"";
	}
	
	@RequestMapping(value = "/querylog", produces = "text/html;charset=utf-8")
	@ResponseBody
	public List<Map<String, Object>> querylog(@RequestBody HashMap<String, String> obj) {
		logger.info("querylog start");
		String beginTimeStr=obj.get("beginTime");
		String endTimeStr=obj.get("endTime");
		Date beginTime=DateUtil.StrToDateForPattern(beginTimeStr, DateUtil.YYYY_MM_DD_HHMMSS_SSS);
		Date endTime=DateUtil.StrToDateForPattern(endTimeStr, DateUtil.YYYY_MM_DD_HHMMSS_SSS);
		beginTimeStr=DateUtil.DateToUTCstr(beginTime);
		endTimeStr=DateUtil.DateToUTCstr(endTime);
		
		List<Map<String, Object>> result=LdaService.queryDataByTime(LdaService.LOG_INDEX_NAME,beginTimeStr,endTimeStr);
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
		return result;
	}
	
	
	


	private void saveEs(String msgtxt,String msgstr, double result) {
		double[] dist=LDAPredictor.theme(msgtxt);
		String topicstr="";
		for (double d : dist) {
			topicstr=topicstr+" "+Math.round(LDAUtils.baoliu(d,7)*10000)/100 + "% ";
		}
		int theme=LDAPredictor.maxtheme(dist);
		
		List<HashMap<String,Object>> dbre=new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> o=new HashMap<String, Object>();
		o.put("msgtxt",msgtxt);
		o.put("theme",theme);
		o.put("topicstr",topicstr);
		o.put("feel",result);
		o.put("msgstr",msgstr);
		dbre.add(o);
		LdaService.insertBatchLineForMap("zxj_lda_log", dbre);
	}
	

}
