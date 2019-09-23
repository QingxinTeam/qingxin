//判断是否在前面加0
function getNow(s) {
return s < 10 ? '0' + s: s;
}

var getTime=function(){
	var myDate = new Date();             

	var year=myDate.getFullYear();        //获取当前年
	var month=myDate.getMonth()+1;   //获取当前月
	var date=myDate.getDate();            //获取当前日


	var h=myDate.getHours();              //获取当前小时数(0-23)
	var m=myDate.getMinutes();          //获取当前分钟数(0-59)
	var s=myDate.getSeconds();

	var now=getNow(h)+':'+getNow(m)+":"+getNow(s);
	return now;
}

function readchart3(obj,url,divname,captions,echartsObj1){
		var categorylist=[];
		var datasetlist=[];
		$.ajax({
			"type" : "POST",
			"data" : JSON.stringify(obj),
			"url" : url,
			"dataType": "json",
			"contentType" : "application/json;charset=utf-8",
			success : function(data1) {
				echartLine("column2D","不同分类困惑度",data1.category,data1.dataset,data1.legends.data,echartsObj1);
			}
		});
	};
	
 function echartLine(divname,captions,categorylist,datasetlist,legends,echartsObj){
	 echartsObj.clear();
	 
	 var option={
			 title:{
				text:captions
			 },
			 legend:{
					data:legends,
					type:'scroll',
					orient:'horizontal',
					x:'center'
				 },
		      toolbox:{
				show:true,
				orient:'vertical',
				x:'right',
				y:'center',
				feature:{
					mark:{show:true},
					dataView:{show:true,readOnly:false},
					magicType:{show:true,type:['line','bar','stack','tiled']},
					restore:{show:true},
					saveAsImage:{show:true}
				}
			},
			calculable:true,
			 tooltip:{
				trigger:'axis',
				axisPointer:{
					type:'cross',
					label:{
						formatter:function(params){
							if(params.seriesData.length==0){
								window.mouseCurValue=params.value;
							}
						}
					}
				},
				formatter:function(params){
					var res="",sum=0;
					for(var i=0;i<params.length;i++){
						var series=params[i];
						sum+=Number(series.data);
						if(sum>=window.mouseCurValue){
							res=series.axisValue+"<br/>"+series.marker+series.seriesName+":"+series.data+"<br/>";
							break;
						}
					}
					return res;
				}
			 },
			 xAxis:{
				 data:categorylist
			 
			 },
			 yAxis:{
				 type:'value'
			 },
			 series:datasetlist
			 
	 };
	 
	 echartsObj.setOption(option);
	 $('#'+divname).css("position","");
 }
 
 
 
 
 function readchart2(obj,url,divname,captions,echartsObj1,$scope){
		var categorylist=[];
		var datasetlist=[];
		$.ajax({
			"type" : "post",
			"data" : JSON.stringify(obj),
			"url" : url,
			"dataType": "json",
			"contentType" : "application/json;charset=utf-8",
			success : function(data1) {
				echartPie(divname,"分类占比",echartsObj1,data1.category,data1.dataset);
			}
		});
	};
	
	
	 function echartPie(divname,captions,echartsObj,legendlist,datalist){
		 echartsObj.clear();
		 var option={
				 tooltip:{
					 trigger:"item",
					 formatter:"{a}<br/>{b}: {c}({d}%)"
				 },
				 legend:{
						x:"left",
						orient:"vertical",
						data:legendlist
					 },
				 series:[{
					 name:"分类占比",
					 type:"pie",
					 radius:["50%","70%"],
					 aviodLabelOverlap:false,
					 label:{
						 normal:{
							 show:false,
							 position:"center"
						 },
						 emphasis:{
								 show:true,
								 textStyle:{
									 fontSize:"30",
									 fontWeight:"bold"
								 }
						 }
					 },
					 labelLine:{
						 normal:{
							 show:false
						 }
					 } ,
					 
					 data:datalist
					 
				 }]
		 };
		 
		 echartsObj.setOption(option);
		 $('#'+divname).css("position","");
		 
	 }
	