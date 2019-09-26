 var app=angular.module('myApp', ["ngRoute"]).config(function($sceProvider){
	    $sceProvider.enabled(false);
 });

 app.config(['$routeProvider',
		 function($routeProvider){
	 $routeProvider
	 
	   .when('/home',{
	    	templateUrl:'ftl/home.html',
	    	controller:'homeCtrl'
	    })  
	    .when('/index',{
	    	templateUrl:'ftl/theme.html',
	    	controller:'themeCtrl'
	    })  
	    
	     .when('/fenci',{
	    	templateUrl:'ftl/stopword.html',
	    	controller:'fenciCtrl'
	    })  
	    
	    .when('/msg',{
	    	templateUrl:'ftl/msg.html',
	    	controller:'msgCtrl'
	    })  
	    
	     .when('/feel',{
	    	templateUrl:'ftl/feel.html',
	    	controller:'feelCtrl'
	    })  
	    
	    .when('/data',{
	    	templateUrl:'ftl/data.html',
	    	controller:'dataCtrl'
	    })  
	     .when('/monitor',{
	    	templateUrl:'ftl/monitor.html',
	    	controller:'monitorCtrl'
	    })  
	    .otherwise({
	    	redirectTo:'/home'
	    		});
 }]);
 

//添加http拦截器
 app.config(["$httpProvider", function ($httpProvider) {   
     $httpProvider.interceptors.push('httpInterceptor');  
 }]);

 
//loading  
 app.factory('httpInterceptor', ["$rootScope", function ($rootScope) {  
     var httpInterceptor = {
         request: function (config) { 
             $rootScope.loading = true;  
             return config;  
         },  
         response: function (response) { 
             $rootScope.loading = false;  
             return response;  
         }  
     };  
     return httpInterceptor;  
 }]);
 
 
 app.directive('loading', function(){  
	    return {  
	        restrict: 'E',  
	        transclude: true,  
	        template: '<div ng-show="loading" class="loading" id="allDiv"  style="position:fixed; top:0px; left:0px; width:100%; height:100%; display:none; background-color:#000; opacity: 0.5; z-index:99999;">'  
	        +'<img alt="" src="img/loading-0.gif" style="vertical-align: middle;width:100px; height:100px; position: absolute; top:50%; left:50%; margin-top: -50px; margin-left:-50px;"/></div>',  
	        link: function (scope, element, attr) {  
	            scope.$watch('loading', function (val) {
	                if (val){  
	                    document.getElementById("allDiv").style.display = "block";  
	                }else{  
	                    document.getElementById("allDiv").style.display = 'none';  
	                }  
	            });  
	        }  
	    }  
	});


 
 
 