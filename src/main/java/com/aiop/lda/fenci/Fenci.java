package com.aiop.lda.fenci;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.aiop.lda.service.LdaService;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.WordDictionary;

public class Fenci 
{
	private static List<String>stopwords=new ArrayList<String>();
	
	private static JiebaSegmenter segmenter = new JiebaSegmenter();
	
	private static Pattern ci=Pattern.compile("^[A-Za-z\u4e00-\u9fa5]+$");
	
	public static void init() {
        try {
        	FileOutputStream fos=new FileOutputStream( "jieba.dict");
    		HashMap<String,String> param =new HashMap<String, String>();
    		param.put("type", "jieba");
    		List<Map<String, Object>> result=LdaService.queryData(LdaService.WORD_INDEX_NAME,param);
    		for(Map<String, Object> m:result) {
    			String word=(String)m.get("word")+" 3 n"+"\r\n";
    			fos.write(word.getBytes());
    		}
    		fos.close();
    		
    		
    		
    		Path path = Paths.get("jieba.dict") ;
            WordDictionary.getInstance().loadUserDict(path);
            
            
//			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("src/main/resources/stopwords.txt"), "utf-8"));
//			//read number of document
//			String line=reader.readLine();
//			while(line!=null) {
//				stopwords.add(line);
//				line=reader.readLine();
//			}
//			reader.close();
			param =new HashMap<String, String>();
			param.put("type", "stop");
			result=LdaService.queryData(LdaService.WORD_INDEX_NAME,param);
			for(Map<String, Object> m:result) {
				stopwords.add((String)m.get("word"));
			}
			System.out.println(stopwords.size());
		}
		catch (Exception e){
			System.out.println("Read Dataset Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	static{
		Fenci.init();
	}
	
	
	public static boolean filterWord(String t){
		//停用词过滤
		if (stopwords.contains(t)) {
			return false;
		}
		//空格制表符过滤
		if(!ci.matcher(t).find()){
			return false;
		}
		return true;
	}
	
	public static String fenci(String line){
		if(StringUtils.isNoneBlank(line)){
			List<String> texts = segmenter.sentenceProcess(line);
			System.out.println(texts);
			String text = "";
			for (String t : texts) {
				if (filterWord(t)) {
					text = text + " " + t;
				}
			}
			if(text.length()>1){
				text = text.substring(1);
			}
			
			return text;
		}
		return null;
	}
	

	
	public static  void loadfileToEs(){
		 try {
			   
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:/d12/model/data.txt"), "utf-8"));
				String line=reader.readLine();
				List<String> datas=new ArrayList<String>();
				List<String> ids=new ArrayList<String>();
				int index=0;
				while(line!=null) {
					index++;
					if(StringUtils.isNoneBlank(line)){
						List<String> texts = segmenter.sentenceProcess(line);
						StringBuffer text=new StringBuffer();
						for (String t : texts) {
							if(filterWord(t)){
								text.append(" " + t);
							}
						}
						if(text.length()>1){
							ids.add(index+"");
							datas.add(text.substring(1));
							if(datas.size()%200==0){
								LdaService.insertBatchLineForIds(LdaService.FENCI_INDEX_NAME,ids,datas);
								datas.clear();
								ids.clear();
							}
						}
					}
					
					line=reader.readLine();
				}
				reader.close();
				
				if(datas.size()>0){
					LdaService.insertBatchLineForIds(LdaService.FENCI_INDEX_NAME,ids, datas);
				}
			}
			catch (Exception e){
				System.out.println("Read Dataset Error: " + e.getMessage(
						));
				e.printStackTrace();
			}
	}
	
	
	
    public static void main( String[] args )
    {     
//    	loadfileToEs();
    	System.out.println(fenci("我很不满意"));
    }
    
}
