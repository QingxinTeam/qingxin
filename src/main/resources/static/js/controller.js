 app.controller('dataCtrl', function($scope, $http) {
	 var today = new Date();
     var s = today.getTime() - 1000 * 60 * 60*24*15; // 15天
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
	 
	 
	 
  });
 
 
 
 app.controller('msgCtrl', function($scope, $http) {
	 $scope.sendbtn=function(){
		  $("#contentDiv").append(getTime()+"| <label class='msgcss' >"+$scope.msgtxt+"</label> <br/>");
		  $("#msgtxt").val("");
	  };
	 
	 $scope.themebtn=function(){
		  var result="";
		  $(".msgcss").each(function(){
			  result=result+$(this).text()+","
		  });
		  alert(result);
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
						$("#themeDiv").html(data)
					});
	  };
	  
	  
	  $scope.feelbtn=function(){
		  var result="";
		  $(".msgcss").each(function(){
			  result=result+$(this).text()+","
		  });
		  alert(result);
		  msgstr=$("#contentDiv").html();
		  var obj={"msgtxt":result,"msgstr":msgstr};
		 $http({
					method : 'POST',
					url : 'httpService/feel',
					data : JSON.stringify(obj),
					headers : {
						'Content-Type' : 'application/json;charset=utf-8',
						"dataType": "json",
					}
				}).success(function(data) {
					$("#feelDiv").html(data)
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
 









