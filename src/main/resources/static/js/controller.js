 app.controller('dataCtrl', function($scope, $http) {
	 var today = new Date();
     var s = today.getTime() - 1000 * 60 * 60*24*1; // 15天
     var t = today.getTime();
 	$scope.beginTime = new Date(s).defaultDateTimeFormat();
	$scope.endTime = new Date(t).defaultDateTimeFormat();
	
	$scope.query = function() {
		 var obj={"beginTime":$scope.beginTime,"endTime":$scope.endTime};
		 $http({
				method : 'POST',
				url : 'httpService/querylog',
				data : JSON.stringify(obj),
				headers : {
					'Content-Type' : 'application/json;charset=utf-8',
					"dataType": "json",
				}
			}).success(function(data) {
				 $scope.list=data;
			});
	 }
	 $scope.query();	  
  });
 
 app.controller('homeCtrl', function($scope, $http) {
	  
  });
 
 
 app.controller('feelCtrl', function($scope, $http) {
	 $scope.query = function() {
		 var obj={"beginTime":$scope.beginTime,"endTime":$scope.endTime};
		 $http({
				method : 'POST',
				url : 'httpService/queryNegWord',
				data : JSON.stringify(obj),
				headers : {
					'Content-Type' : 'application/json;charset=utf-8',
					"dataType": "json",
				}
			}).success(function(data) {
				 $scope.list=data;
			});
	 }
	 $scope.query();
	 
	 $scope.edit=function(type,method){
		 var obj={"stopwords":$scope.stopwords,"type":type,"method":method};
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
				 $scope.query();
			});
	 };
	 
	 $scope.addneg=function(){
		 $scope.edit("neg","add");
	 };
	 
	 $scope.delneg=function(){
		 $scope.edit("neg","del");
	 };
	 
	 
	 $scope.basequery = function() {
	    var obj={};
		 var url = "httpService/queryFeel";
	    	var divname="chart-container1";
	    	var caption="web接口调用次数";
	    	var myChart2=echarts.init(document.getElementById(divname));
	    	readchart2(obj,url,divname,caption,myChart2,$scope);
	    }
	    
	    $scope.basequery();
	 
	 
	 
  });
 
 
 
 app.controller('msgCtrl', function($scope, $http) {
	 $scope.style1="btn-default";
	 $scope.style2="btn-default";
	 $scope.sendbtn=function(){
		  $("#contentDiv").append("<div ><span class='fontlarge'>客户: </span><span class='msgspandetail'>"+$scope.msgtxt+"</span><br/></span><span style='color: gray;'>"+getTime()+"</span></div>");
		  $("#msgtxt").val("");
		  $scope.themebtn();
		  $scope.feelbtn("0",$scope.msgtxt);
	  };
	  
	  $scope.sendbtn02=function(){
		  $("#contentDiv").append("<div style='text-align:right'><span class='msgspandetail' >"+$scope.msgtxt02+"</span><span class='fontlarge' >:坐席</span><br/><span style='color: gray;'>"+getTime()+"</span></div>");
		  $("#msgtxt02").val("");
//		  $scope.themebtn();
		  $scope.feelbtn("1",$scope.msgtxt02);
	  };
	  
	  $scope.finish=function(){
		  $("#contentDiv").html("");
		  $("#msgtxt").val("");
		  $("#themeDiv").html("");
		  $("#feelDiv").html("");
	  };
	 
	 $scope.themebtn=function(){
		 $scope.style1="btn-default";
		 $scope.style2="btn-default";
		  var obj={"msgtxt":$scope.msgtxt};
		  $http({
						method : 'POST',
						url : 'httpService/theme',
						data : JSON.stringify(obj),
						headers : {
							'Content-Type' : 'application/json;charset=utf-8',
							"dataType": "json",
						}
					}).success(function(data) {
						$("#themeDiv").html(data)
					});
	  };
	  
	  
	  $scope.feelbtn=function(usertype,msgtxt){
		 
		
		  msgstr=$("#contentDiv").html();
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
	 
	 
	 
	 $scope.addstop=function(){
		 $scope.edit("stop","add");
	 };
	 $scope.delstop=function(){
		 $scope.edit("stop","del");
	 };
	 $scope.addjieba=function(){
		 $scope.edit("jieba","add");
	 };
	 $scope.deljieba=function(){
		 $scope.edit("jieba","del");
	 };
	 
	 
	 
	 $scope.edit=function(type,method){
		 var obj={"stopwords":$scope.stopwords,"type":type,"method":method};
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
	    	
	    	
	    	$http({
				method : 'POST',
				url : 'httpService/querytopic',
				headers : {
					'Content-Type' : 'application/json;charset=utf-8',
					"dataType": "json",
				}
			}).success(function(data) {
				$scope.list = data;
			});
	    	
	    	
	    	$http({
				method : 'POST',
				url : 'httpService/queryStopWord',
				data:{},
				headers : {
					'Content-Type' : 'application/json;charset=utf-8',
					"dataType": "json",
				}
			}).success(function(data) {
				$scope.list2 = data;
			});
	    	
	    	$http({
				method : 'POST',
				url : 'httpService/queryJiebaWord',
				data:{},
				headers : {
					'Content-Type' : 'application/json;charset=utf-8',
					"dataType": "json",
				}
			}).success(function(data) {
				$scope.list3 = data;
			});
	    	
	    };
	    
	    $scope.basequery();
    
  });
 









