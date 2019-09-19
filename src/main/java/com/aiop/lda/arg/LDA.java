package com.aiop.lda.arg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aiop.lda.service.LdaService;



public class LDA implements Runnable{
	private static final Logger logger = LogManager.getLogger(LDA.class.getName());

	public static int k=6;
	private Estimator estimator;
	public static LDAOption option = new LDAOption();
	@Override
	public void run() {
		
		option.dir = "D:/d12/model";
		option.dfile = "doc.dat";
		option.est = true;  /////
		///option.estc = true;
		option.inf = false;
		option.modelName = "model-final";
		option.niters = 200;
		option.K=k;
		estimator = new Estimator();
		estimator.init(option);
		estimator.estimate();
		
		List<HashMap<String,Object>> result=new ArrayList<HashMap<String,Object>>();
		List<String>ids=new ArrayList<String>();
		ids.add("1");
		HashMap<String,Object> o=new HashMap<String, Object>();
		o.put("niters",option.niters);
		o.put("K",option.K);
		o.put("alpha",option.alpha);
		o.put("beta",option.beta);
		result.add(o);
		LdaService.insertBatchLineForMap("zxj_lda_option",ids, result);
		double p=getRe();
		LdaService.insertPLog(p, k);
	}
	
	public double getRe(){
		double count=0;
		int i=0;
		Model trnModel=estimator.trnModel;
		LDADataset data=trnModel.data;
		double[][] phi=trnModel.phi;
		double[][] theta=trnModel.theta;
		
		for (int m = 0; m < trnModel.M; m++){	
			double mul=0;
			for (int n = 0; n < trnModel.data.docs[m].length; n++){
				double sum=0;
				int index=data.docs[m].words[n];
				for(int k=0;k<trnModel.K;k++){
					sum=sum+phi[k][index]*theta[i][k];
				}
				mul=mul+Math.log(sum);
			}
			count=count+mul;
			i++;
		}
		count=0-count;
		double P=Math.exp(count/trnModel.V);
		int p=(int) (P/1000000000000000l);
		logger.info("Perplexity:"+p);
		return p;
	}
	
	public static void main(String[] args) {
		for(int i=2;i<50;i++){
			k=i;
			LdaService.deleteIndex();
			new LDA().run();
		}
		
		
//		LdaService.deleteIndex();
//		new LDA().run();
		
		
	}

}
