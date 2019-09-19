package com.aiop.lda.emotion;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.aiop.lda.fenci.Fenci;
import com.aiop.lda.util.StringsUtil;

public class EmotionModel {
  //消极情感词典
  private static List<String> negdict=new ArrayList<String>();
  private static List<Integer> negdictWeight=new ArrayList<Integer>();
  //积极情感词典
  private static List<String> posdict=new ArrayList<String>();
  //否定词词典
  private static List<String> nodict=new ArrayList<String>();
  //程度副词词典
  private static List<String> plusdict=new ArrayList<String>();
  
  static {
	  loadEmotionDictory();
  }
  
  private static void loadFile(String filepath,List<String> list,List<Integer> weight,String splitStr) {
	  try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "utf-8"));
			//read number of document
			String line=reader.readLine();
			while(line!=null) {
				if(splitStr!=null) {
					String[] tmps=line.split(splitStr);
					list.add(tmps[0]);
					weight.add(Integer.valueOf(tmps[1]));
				}else {
					list.add(line);
				}
				
				line=reader.readLine();
			}
			reader.close();
			System.out.println(list.size());
		}
		catch (Exception e){
			System.out.println("Read Dataset Error: " + e.getMessage());
			e.printStackTrace();
		} 
  }
  
  private static void loadEmotionDictory() {
	  loadFile("src/main/resources/feel/negdict.txt",negdict,negdictWeight,"\t");
	  loadFile("src/main/resources/feel/posdict.txt",posdict,null,null);
	  loadFile("src/main/resources/feel/nodict.txt",nodict,null,null);
	  loadFile("src/main/resources/feel/plusdict.txt",plusdict,null,null);
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
		  if(negdict.contains(sd[i])){
			  int index=negdict.indexOf(sd[i]);
			  if(index>0) {
				  negweight=negdictWeight.get(index);
			  }
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
			  }else if(i>0 && negdict.contains(sd[i-1])) {
				  p=p-1;
			  }else if(i<sd.length-1 && negdict.contains(sd[i+1])) {
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
