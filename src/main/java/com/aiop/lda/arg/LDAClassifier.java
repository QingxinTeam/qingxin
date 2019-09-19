package com.aiop.lda.arg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

public class LDAClassifier {

	public static void main(String args[]) throws IOException {
		LDAOption option = new LDAOption();

		option.dir = "D:\\d12\\model";
		////option.dfile = "doc.dat";
		option.est = false; // ////
		option.estc = false;
		option.inf = true;
		option.modelName = "model-final";

		Inferencer inferencer = new Inferencer();
		inferencer.init(option);

		//////
		String car = "";
		BufferedReader br = new BufferedReader(new FileReader("D:\\d12\\model\\doctest.dat"));
		
		while((car = br.readLine()) != null){
			if(car.contains(":")){
				
				String [] newData = new String[1];
				newData[0] = car;
				
				Model newModel = inferencer.inference(newData);
				
				double [] dist = newModel.theta[0];
				double max=0l;
				String group="";
				int count=0;
				for (double d : dist) {
					count++;
					double result=Math.round(LDAUtils.baoliu(d,7)*10000)/100;
					if(result>max){
						max=result;
						group ="group"+count+"="+max;
					}
				}
				System.out.println( group+ "% ");
			}
		}
		
		br.close();
		
	}
	
	/**
	 * ���Map��Doubleֵ����Map�����򣬽���
	 */
	public static Comparator<Map.Entry<String, Double>> SortMapByValueComparator = new Comparator<Map.Entry<String,Double>>() {
		@Override
		public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
			if(o2.getValue() - o1.getValue() > 0){
				return 1;
			}else if(o2.getValue() - o1.getValue() < 0){
				return -1;
			}else {
				return 0;
			}
		}
	};
}
