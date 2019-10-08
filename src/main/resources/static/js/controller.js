 app.controller('dataCtrl', function($scope, $http) {
	 var today = new Date();
     var s = today.getTime() - 1000 * 60 * 60*24*1; // 15天
     var t = today.getTime();
 	$scope.beginTime = new Date(s).defaultDateTimeFormat();
	$scope.endTime = new Date(t).defaultDateTimeFormat();
	
	 var obj={"beginTime":$scope.beginTime,"endTime":$scope.endTime};
	 btabledata($http,obj);
  });
 
 app.controller('homeCtrl', function($scope, $http) {
	  
  });
 
 
 app.controller('monitorCtrl', function($scope, $http) {
	 $scope.basequery = function() {
	    	var obj={};
	    	
	    	var url = "httpService/queryPer";
	    	var divname="chart-container1";
	    	var caption="web接口调用次数";
	    	var myChart1=echarts.init(document.getElementById(divname));
	    	readchart3(obj,url,divname,caption,myChart1);
	    	
	    	var url = "httpService/queryNum";
	    	var divname="chart-container2";
	    	var caption="web接口调用次数";
	    	var myChart2=echarts.init(document.getElementById(divname));
	    	readchart2(obj,url,divname,caption,myChart2,$scope);

	        var url = "httpService/queryFeel";
		    var divname = "chart-container3";
		    var caption = "web接口调用次数";
		    var myChart2 = echarts.init(document.getElementById(divname));
		    readchart2(obj, url, divname, caption, myChart2, $scope);
	    	
	    };
	    
	    $scope.basequery();
 });

 app.controller('feelCtrl', function($scope, $http) {
	 $scope.edit=function(type,method,word){
		 var obj={"stopwords":word,"type":type,"method":method};
		 $http({
				method : 'POST',
				url : 'httpService/editWords',
				data : obj,
				headers : {
					'Content-Type' : 'application/json;charset=utf-8',
					"dataType": "json",
				}
			}).success(function(data) {
			});
	 };
	 
	 
	 
	 btable04($http);
	    
	    
	    
	    $("#delbutton").click(function(){
	    	$.map($('#table').bootstrapTable('getSelections'),function (row) {
	    		 $scope.edit("neg","del",row.word+"|"+row.value);
		    });
	    });
	    
	    $("#sendbtn").click(function(){
	    	$.map($('#table').bootstrapTable('getSelections'),function (row) {
	    		 $scope.edit("neg","add",row.word+"|"+row.value);
		    });
	    });
	 
  });
 
 
 
 app.controller('msgCtrl', function($scope, $http) {
	 $scope.user=users[Math.floor(Math.random()*6)];
	 $scope.themetype=0;
	 $scope.style1="btn-default";
	 $scope.style2="btn-default";
	 
	 
	 $scope.myKeyup01 = function(e){
		 var keycode = window.event?e.keyCode:e.which; 
	        if(keycode==13){
	        	 $scope.sendbtn();
	        }
	 }
	 
	 $scope.myKeyup02 = function(e){
		 var keycode = window.event?e.keyCode:e.which; 
	        if(keycode==13){
	        	 $scope.sendbtn02();
	        }
	 }
	 
	 $scope.sendbtn=function(){
		 $("#user").removeClass("ng-hide");
		  $("#contentDiv").append("<div ><span class='fontlarge'>客户: </span><span class='msgspandetail msgcss usercss'>"+$scope.msgtxt+"</span><br/></span><span style='color: gray;'>"+getTime()+"</span></div>");
		  $("#msgtxt").val("");
		  $scope.themebtn();
		  $scope.feelbtn("0",$scope.msgtxt);
	  };
	  
	  $scope.sendbtn02=function(){
		  $("#contentDiv").append("<div style='text-align:right'><span class='msgspandetail msgcss' >"+$scope.msgtxt02+"</span><span class='fontlarge' >:坐席</span><br/><span style='color: gray;'>"+getTime()+"</span></div>");
		  $("#msgtxt02").val("");
		  $scope.themebtn();
		  $scope.feelbtn("1",$scope.msgtxt02);
	  };
	  
	  $scope.finish=function(){
		  var result="";
		  $(".usercss").each(function(){
			  result=result+$(this).text()+","
		  });
		  
		  
		  var obj={"msgtxt":result,"msghtml":""};
		  $http({
				method : 'POST',
				url : 'httpService/finish',
				data : JSON.stringify(obj),
				headers : {
					'Content-Type' : 'application/json;charset=utf-8',
					"dataType": "json",
				}
			}).success(function(data) {
				  $("#user").addClass("ng-hide");
				  $scope.user=users[Math.floor(Math.random()*6)];
				  $("#contentDiv").html("");
				  $("#msgtxt").val("");
				  $("#themeDiv").html("");
			});
		  
		  
	  };
	 
	 $scope.themebtn=function(){
		 
		
		 var result="";
		  $(".msgcss").each(function(){
			  result=result+$(this).text()+","
		  });
		  
		  var obj={"msgtxt":result};
		  $http({
						method : 'POST',
						url : 'httpService/theme',
						data : JSON.stringify(obj),
						headers : {
							'Content-Type' : 'application/json;charset=utf-8',
							"dataType": "json",
						}
					}).success(function(data) {
						$("div[id^='themetype_']").addClass("ng-hide");
						$("#themetype_"+data.maxd).removeClass("ng-hide");
//						 $scope.themetype=data.maxd;
						$("#themeDiv").html(data.theme);
						if(data.theme!=""){
							
							if(data.maxd==1){
								$("#liucheng_h").html("业务流程-暴力催收");
							}else if(data.maxd==2){
								$("#liucheng_h").html("业务流程-协商还款");
							}else if(data.maxd==3){
								$("#liucheng_h").html("业务流程-减免利息");
							}else if(data.maxd==4){
								$("#liucheng_h").html("业务流程-逾期征信");
							}else{
								$("#liucheng_h").html("业务流程-服务态度");
							};
							$("#liucheng1").removeClass("ng-hide");
						}
						
					});
	  };
	  
	  
	  $scope.feelbtn=function(usertype,msgtxt){
		 
		
		  msgstr="";
		  var obj={"msgtxt":msgtxt,"msgstr":msgstr};
		 $http({
					method : 'POST',
					url : 'httpService/feel',
					data : JSON.stringify(obj),
					headers : {
						'Content-Type' : 'application/json;charset=utf-8',
						"dataType": "json",
					}
				}).success(function(data) {
					fdata=parseFloat(data);
					var feel="中性";
					if(fdata>0){
						feel="满意";
					}else if(fdata<0){
						feel="不满意";
						
					}
					
					if(usertype=="0"){
						 $scope.style1="btn-default";
						$("#userfeelDiv").html("客户情绪："+feel);
						if(fdata<0){
							$scope.style1="btn-danger";
						}
					}else if(usertype=="1"){
						  $scope.style2="btn-default";
						$("#feelDiv").html("坐席情绪："+feel);
						if(fdata<0){
							$scope.style2="btn-danger";
						}
						
					}
				});
			 
	  };
    
  });
 
 app.controller('themeCtrl', function($scope, $http) {
	 $scope.send=function(){
		 var obj={"minK":$scope.minK};
		 $http({
				method : 'POST',
				url : 'httpService/modeltopic',
				data : JSON.stringify(obj),
				headers : {
					'Content-Type' : 'application/json;charset=utf-8',
					"dataType": "json",
				}
			}).success(function(data) {
				 $scope.basequery();
			});
	 };
	 
	 $scope.edit=function(type,method,word){
		 var obj={"stopwords":word,"type":type,"method":method};
		 $http({
				method : 'POST',
				url : 'httpService/editWords',
				data : obj,
				headers : {
					'Content-Type' : 'application/json;charset=utf-8',
					"dataType": "json",
				}
			}).success(function(data) {
				 alert("success");
			});
	 };
	    
	    btable($http);
  });
 
 
 app.controller('fenciCtrl', function($scope, $http) {
	 
	 $scope.edit=function(type,method,word){
		 var obj={"stopwords":word,"type":type,"method":method};
		 $http({
				method : 'POST',
				url : 'httpService/editWords',
				data : obj,
				headers : {
					'Content-Type' : 'application/json;charset=utf-8',
					"dataType": "json",
				}
			}).success(function(data) {
				 alert("success");
			});
	 };
	    
	    btable02($http);
	    btable03($http);
	    
	    
	    
	    $("#delbutton03").click(function(){
	    	$.map($('#table03').bootstrapTable('getSelections'),function (row) {
	    		 $scope.edit("jieba","del",row.word);
		    });
	    });
	    
	    $("#delbutton02").click(function(){
	    	$.map($('#table02').bootstrapTable('getSelections'),function (row) {
	    		 $scope.edit("stop","del",row.word);
		    });
	    });
	   
    
  });
 









