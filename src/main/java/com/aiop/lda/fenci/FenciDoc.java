package com.aiop.lda.fenci;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.aiop.lda.service.LdaService;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.WordDictionary;

public class FenciDoc 
{
	private static List<String>stopwords=new ArrayList<String>();
	
	private static JiebaSegmenter segmenter = new JiebaSegmenter();
	
	private static List<String> datas = new ArrayList<String>();
	
	private void init() {
		Path path = Paths.get(new File( getClass().getClassLoader().getResource("jieba.dict").getPath() ).getAbsolutePath() ) ;
        WordDictionary.getInstance().loadUserDict(path);
        
        try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("src/main/resources/stopwords.txt"), "utf-8"));
			//read number of document
			String line=reader.readLine();
			while(line!=null) {
				stopwords.add(line);
				line=reader.readLine();
			}
			reader.close();
		}
		catch (Exception e){
			System.out.println("Read Dataset Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	static{
		new FenciDoc().init();
	}
	
	public static String fenci(String line){
		if(StringUtils.isNoneBlank(line)){
			List<String> texts = segmenter.sentenceProcess(line);
			String text = "";
			for (String t : texts) {
				if (Fenci.filterWord(t)) {
					text = text + " " + t;
				}
			}
			text = text.substring(1);
			return text;
		}
		return null;
	}
	
	private static String readDoc(String file){
		 try {
//			 BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			 BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GB2312"));
				StringBuffer result=new StringBuffer();
				String line=reader.readLine();
				while(line!=null) {
					if(StringUtils.isNoneBlank(line)){
						List<String> texts = segmenter.sentenceProcess(line);
						String text = "";
						for (String t : texts) {
							if (Fenci.filterWord(t)) {
								text = text + " " + t;
							}
						}
						if(text.length()>1){
							text = text.substring(1);
						}
						result.append(" ").append(text);
					}
					
					line=reader.readLine();
				}
				reader.close();
				return result.toString();
			}
			catch (Exception e){
				System.out.println("Read Dataset Error: " + e.getMessage());
				e.printStackTrace();
			}
		 return "";
	}
	
	private static  void readFile(String filepath){
		 try {
				File file = new File(filepath);
				if (file.isFile()) {
					System.out.println("文件");
					System.out.println("path=" + file.getPath());
					System.out.println("name=" + file.getName());
					String d = readDoc(filepath);
					if(d.length()>1){
						datas.add(d.toString());
						if(datas.size()%200==0){
							LdaService.insertBatchLine(LdaService.FENCI_INDEX_NAME,datas);
							datas.clear();
						}
					}
				} else if (file.isDirectory()) {
					System.out.println("文件夹");
					String[] filelist = file.list();
					for (int i = 0; i < filelist.length; i++) {
						readFile(filepath + "\\" + filelist[i]);
					}
				}  
			  
			}
			catch (Exception e){
				System.out.println("Read Dataset Error: " + e.getMessage());
				e.printStackTrace();
			}
		
	}
	
	
    public static void main( String[] args )
    {     
    	readFile("D:/d12/model/SogouC.reduced/Reduced"); 
    	if(datas.size()>0){
			LdaService.insertBatchLine(LdaService.FENCI_INDEX_NAME, datas);
		}
    	
    }
    
}
