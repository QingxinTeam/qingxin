package com.aiop.lda.emotion;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aiop.lda.fenci.Fenci;
import com.aiop.lda.service.LdaService;
import com.aiop.lda.util.StringsUtil;

public class EmotionModel {
  //消极情感词典
  private static HashMap<String, Integer> negdict=new HashMap<String, Integer>();
  //积极情感词典
  private static List<String> posdict=new ArrayList<String>();
  //否定词词典
  private static List<String> nodict=new ArrayList<String>();
  //程度副词词典
  private static List<String> plusdict=new ArrayList<String>();
  
  static {
	  loadEmotionDictory();
  }
  
  private static void loadFile(String filepath,List<String> list,String splitStr) {
	  try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "utf-8"));
			//read number of document
			String line=reader.readLine();
			while(line!=null) {
				if(splitStr!=null) {
					//neg
					String[] tmps=line.split(splitStr);
					if(tmps.length!=2) {
						continue;
					}
					negdict.put(tmps[0], Integer.valueOf(tmps[1]));
				}else {
					list.add(line);
				}
				
				line=reader.readLine();
			}
			reader.close();
		}
		catch (Exception e){
			System.out.println("Read Dataset Error: " + e.getMessage());
			e.printStackTrace();
		} 
  }
  
  public static void loadEmotionDictory() {
	  loadFile("src/main/resources/feel/negdict.txt",null,"\t");
	  loadFile("src/main/resources/feel/posdict.txt",posdict,null);
	  loadFile("src/main/resources/feel/nodict.txt",nodict,null);
	  loadFile("src/main/resources/feel/plusdict.txt",plusdict,null);
	  
	  HashMap<String,String> param =new HashMap<String, String>();
		param.put("type", "neg");
		List<Map<String, Object>> result=LdaService.queryData(LdaService.WORD_INDEX_NAME,param);
		
		for(Map<String, Object> m:result) {
			negdict.put((String)m.get("word"), Integer.valueOf((String)m.get("value")));
		}
  }
  
  public static double predict(String input) {
	  double p=0d;
	  if(StringsUtil.isBlank(input)) {
		  return 1;
	  }
	  String[] sd=Fenci.fenci(input).split(" ");
	  int negweight=1;
	  for(int i=0;i<sd.length;i++) {
		  if(StringsUtil.isBlank(sd[i])) {
			  continue;
		  }
		  if(negdict.get(sd[i])!=null){
			  negweight=negdict.get(sd[i]);
			  if(i>0 && nodict.contains(sd[i-1])) {
				p=p+1;  
			  }else if(i>0 && plusdict.contains(sd[i-1])) {
				p=p-2*negweight;
			  }else {
				  p=p-1*negweight;
			  }
		  }else if(posdict.contains(sd[i])) {
			  if(i>0 && nodict.contains(sd[i-1])) {
				  p=p-1;
			  }else if(i>0 && plusdict.contains(sd[i-1])) {
				  p=p+2;
			  }else if(i>0 && negdict.get(sd[i-1])!=null) {
				  p=p-1;
			  }else if(i<sd.length-1 && negdict.get(sd[i+1])!=null) {
				  p=p-1;
			  }else {
				  p=p+1;
			  }
			  
		  }else if(nodict.contains(sd[i])){
			  p=p-0.5;
		  }
	  }
	  return p;
  }
  
  public static void main(String[] args) {
	String test="我想咨询一下我的信用卡账单，为什么逾期了";
	System.out.println(predict(test));
}
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
 
}
