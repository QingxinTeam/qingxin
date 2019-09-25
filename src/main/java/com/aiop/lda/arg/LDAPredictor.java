package com.aiop.lda.arg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.aiop.lda.fenci.Fenci;
import com.aiop.lda.service.LdaService;

public class LDAPredictor {
	private Inferencer inferencer;
	private static LDAPredictor predictor = new LDAPredictor();

	//////输入模型文件地址初始�?
	public LDAPredictor() {
		LDAOption option = new LDAOption();
		List<Map<String, Object>> result=LdaService.queryData(LdaService.OPTION_INDEX_NAME);
		if(result.size()>0) {
			option.K=Integer.valueOf(result.get(0).get("K")+"");
		}
		
		option.inf = true;
		inferencer = new Inferencer();
		inferencer.init(option);
	}
	
	/////////推断新数�?
	public Model inference(String data){
		String [] docs = new String[1];
		docs[0] = data;
		return inferencer.inference(docs);
	}
	
	public static double [] theme(String msg) {
		String input = Fenci.fenci(msg);
		System.out.println(input);
		Model model = predictor.inference(input);
		
		double [] dist = model.theta[0];
		return dist;
	}
	
	public static int maxtheme(double[] dist) {
		double max=0;
		int result=0;
		for(int i=0;i<dist.length;i++) {
			if(dist[i]>max) {
				max=dist[i];
				result=i;
			}
		}
		
		result+=1;
		return result;
	}
		

	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		
		
		String[] inputs={
				"对于之前提额问题银行的回复不满意，投诉人员工作态度问题。投诉3个处理经办的态度问题。"
		};
		for(String text : inputs){
			System.out.println(theme(text));
		}

	}
	
	
	
	
	
	
	
	
	
}
