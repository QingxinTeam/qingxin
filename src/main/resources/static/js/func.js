var users=[{"age":"年龄：28","sex":"性别：男","out":"本月支出：10000","card":"卡类别：白金卡"},
	{"age":"年龄：30","sex":"性别：女","out":"本月支出：900","card":"卡类别：普卡"},
	{"age":"年龄：39","sex":"性别：男","out":"本月支出：1032","card":"卡类别：黑卡"},
	{"age":"年龄：19","sex":"性别：女","out":"本月支出：12222","card":"卡类别：青年卡"},
	{"age":"年龄：46","sex":"性别：男","out":"本月支出：20000","card":"卡类别：白金卡"},
	{"age":"年龄：55","sex":"性别：男","out":"本月支出：3000","card":"卡类别：白金卡"}];


//新增行和编辑行时右侧操作改为保存和取消
let editFormatter = ` 
<button onclick = 'saveRow(this)' id='button' class='btn btn-default' style='background-color:#bf502e;color:white;border: 0px;'> <span class='glyphicon glyphicon-ok'></span> 保存</button>
<button onclick='cancleRow(this)' id='button' class='btn btn-default' style='background-color:#1caf27;color:white;border: 0px;'> <span class='glyphicon glyphicon-minus'></span> 取消</button>
` 
	
	function refreshTable() { //静默刷新
	   $('#table').bootstrapTable('refresh', {
	       silent: true,
	       url: '',
	       query: ''
	   })
	    }
	
	 //开启编辑
	 function editRow(index) {
	     $("#table tr:nth-child(" + (index + 1)).attr('data-edit', true) //编辑标志
	     $("#table tr:nth-child(" + (index + 1) + ") td.editable").each(function () {
	         var value = $(this).text();
	         $(this).html("<input value='" + value + "'>");
	     });
	     $("#table tr:nth-child(" + (index + 1) + ") td.editOperate").html(editFormatter)
	 };	
	 
	//取消编辑
	 function cancleRow(ele) {
	     let obj = $(ele).parent().parent(); //获取tr的dom
	     let del = obj.siblings().length == 0 ? false : true;
	     let edit = obj.attr('data-edit') ? true : false;
	     // console.log(edit)
	     if (edit) { //编辑状态回归
	         refreshTable() //刷新
	     } else { //新增行删除
	         if (!del) return; //没有数据
	         $("#table").bootstrapTable('remove', {
	             field: 'name',
	             values: ['']
	         });
	         addKey = true;
	     }
	 };
	 
	 //保存数据
	 function saveRow(ele) {
	 	let obj = $(ele).parent().parent(); //获取tr的dom
	 	let saveKey = true; //是否允许保存
	 	let edit = obj.attr('data-edit') ? true : false; //是编辑还是添加
	 	let arrVal = []; //保存填入的数据
	 	obj.find('td.editable').each(function (index) {
	 	    let val = $(this).find("input").val().trim();
	 	    if (index < 1 && !val) saveKey = false; 
	 	    arrVal.push(val)
	 	})
	 	if (!saveKey) {
	 	    console.log('分类名称不能为空');
	 	    return;
	 	}
	 	if (edit) { //修改编辑
	 	    let id = obj.find('td.topic').text(); //这个就自己去获取，我这里只是随便写的（自
	 	   var paramobj={"index":id,"field":"topicname","value":arrVal[0]};
	 	  $.ajax({
					method : 'POST',
					url : 'httpService/updateTopicName',
					data : JSON.stringify(paramobj),
					'contentType' : 'application/json;charset=utf-8',
					"dataType": "json",
					"success" : function(data) {
						alert('success');

					}
				});
	 	
	 	} 
	 	//发送数据成功后回调刷新
	 	refreshTable() //刷新
	 };

	

function btable($http){
	 let $table = $('#table');
	    let $button = $('#button');
	    let $getTableData = $('#getTableData');


	    $table.bootstrapTable({
	        url: 'httpService/querytopic',
	        toolbar: '#toolbar',
	        clickEdit: true,
	        search: true,//是否显示右上角的搜索框  
	        showToggle: true,
	        pagination: true,       //显示分页条
	        showColumns: true,
	        showPaginationSwitch: true,     //显示切换分页按钮
	        showRefresh: true,      //显示刷新按钮
//	        clickToSelect: true,  //点击row选中radio或CheckBox
	        columns: [{
	            checkbox: true
	        }, {
	            field: 'crtTime',
	            title: '创建时间'
	        }, {
	            field: 'topic',
	            title: '主题序号',
	            class: 'topic'
	        },{
	            field: 'topicname',
	            title: '主题分类',
	            class: 'editable'
	        },{
	            field: 'words',
	            title: '关键字'
	        },
	        {
	            title: '操作',
	            field: 'operate',
	            class: 'editOperate',
	            formatter: formatterOperate
	        }],
	        /**
	         * @param {点击列的 field 名称} field
	         * @param {点击列的 value 值} value
	         * @param {点击列的整行数据} row
	         * @param {td 元素} $element
	        
	        onDblClickCell: function(field, value, row, $element) {
	            $element.attr('contenteditable', true);
	            $element.blur(function() {
	                let index = $element.parent().data('index');
	                let tdValue = $element.html();

	                saveData(index, field, tdValue);
	            })
	        } */
	    });
	    
	  //格式化操作
		 function formatterOperate(value, row, index) {
			return "<button onclick='editRow("+index+")' id='button' class='btn btn-default' style='background-color:#1caf27;color:white;border: 0px;'> <span class='glyphicon glyphicon-edit'></span> 编辑</button>";
			 };

	    $getTableData.click(function() {
	        alert(JSON.stringify($table.bootstrapTable('getData')));
	    });

	    function saveData(index, field, value) {
	      
	    }

}


function btabledata($http,obj){
	
	 let $table = $('#table');
	    let $button = $('#button');
	    let $getTableData = $('#getTableData');


	    $table.bootstrapTable({
	    	method: 'get',  
	        url: 'httpService/querylog',
	        contentType: 'application/json;charset=utf-8',
			dataType: "json",
	        queryParams: obj, //参数  
	        queryParamsType: "limit",
	        search: true,//是否显示右上角的搜索框  
	        toolbar: '#toolbar',
	        clickEdit: true,
	        showToggle: true,
	        pagination: true,       //显示分页条
	        showColumns: true,
	        showPaginationSwitch: true,     //显示切换分页按钮
	        showRefresh: true,      //显示刷新按钮
	        //clickToSelect: true,  //点击row选中radio或CheckBox
	        columns: [{
	            checkbox: true
	        }, {
	            field: 'crtTime',
	            title: '创建时间',
	            edit:false
	        }, {
	            field: 'neg',
	            title: '顾客情绪',
	            edit:false
	        },{
	            field: 'feelNum',
	            title: '情感值'
	        },{
	            field: 'theme',
	            title: '咨询主题',
	            edit:false
	        },  {
	            field: 'themeRate',
	            title: '主题分步'
	        }],
	        /**
	         * @param {点击列的 field 名称} field
	         * @param {点击列的 value 值} value
	         * @param {点击列的整行数据} row
	         * @param {td 元素} $element
	         */
	        onDblClickCell: function(field, value, row, $element) {
	            $element.attr('contenteditable', true);
	            $element.blur(function() {
	                let index = $element.parent().data('index');
	                let tdValue = $element.html();

	                saveData(index, field, tdValue);
	            })
	        }
	    });

	    $getTableData.click(function() {
	        alert(JSON.stringify($table.bootstrapTable('getData')));
	    });

	    function saveData(index, field, value) {
	        var obj={"index":index,"field":field,"value":value};
			 $http({
					method : 'POST',
					url : 'httpService/updateTopicName',
					data : obj,
					headers : {
						'Content-Type' : 'application/json;charset=utf-8',
						"dataType": "json",
					}
				}).success(function(data) {
					 alert("success");
				});
	    }

}


function btable02($http){
	 let $table = $('#table02');
	    let $addbutton = $('#addbutton02');
	    let $delbutton = $('#delbutton02');
	    let $getTableData = $('#getTableData02');

	    $addbutton.click(function() {
	        $table.bootstrapTable('insertRow', {
	            index: 0,
	            row: {
	            	crtTime: getTimeAll(),
	            	word: ''
	            }
	        });
	    });

	    $table.bootstrapTable({
	        url: 'httpService/queryStopWord',
	        queryParams:{},
	        toolbar: '#toolbar02',
	        clickEdit: true,
	        search: true,//是否显示右上角的搜索框  
	        showToggle: true,
	        pagination: true,       //显示分页条
	        showColumns: true,
	        showPaginationSwitch: true,     //显示切换分页按钮
	        showRefresh: true,      //显示刷新按钮
	        //clickToSelect: true,  //点击row选中radio或CheckBox
	        columns: [{
	            checkbox: true
	        }, {
	            field: 'crtTime',
	            title: '创建时间',
	            edit:false
	        },{
	            field: 'word',
	            title: '停用词'
	        }],
	        /**
	         * @param {点击列的 field 名称} field
	         * @param {点击列的 value 值} value
	         * @param {点击列的整行数据} row
	         * @param {td 元素} $element
	         */
	        onDblClickCell: function(field, value, row, $element) {
	            $element.attr('contenteditable', true);
	            $element.blur(function() {
	                let index = $element.parent().data('index');
	                let tdValue = $element.html();

	                saveData(index, field, tdValue);
	            })
	        }
	    });

	    $getTableData.click(function() {
	        alert(JSON.stringify($table.bootstrapTable('getData')));
	    });

	    function saveData(index, field, value) {
	    	  var obj={"index":index,"field":field,"stopwords":value,"type":"stop","method":"add"};
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
	    }

}


function btable03($http){
	 let $table = $('#table03');
	    let $addbutton = $('#addbutton03');
	    let $delbutton = $('#delbutton03');
	    let $getTableData = $('#getTableData03');

	    $addbutton.click(function() {
	        $table.bootstrapTable('insertRow', {
	            index: 0,
	            row: {
	            	crtTime: getTimeAll(),
	            	word: ''
	            }
	        });
	    });

	    $table.bootstrapTable({
	        url: 'httpService/queryJiebaWord',
	        queryParams:{},
	        toolbar: '#toolbar03',
	        clickEdit: true,
	        search: true,//是否显示右上角的搜索框  
	        showToggle: true,
	        pagination: true,       //显示分页条
	        showColumns: true,
	        showPaginationSwitch: true,     //显示切换分页按钮
	        showRefresh: true,      //显示刷新按钮
	        //clickToSelect: true,  //点击row选中radio或CheckBox
	        columns: [{
	            checkbox: true
	        }, {
	            field: 'crtTime',
	            title: '创建时间',
	            edit:false
	        },{
	            field: 'word',
	            title: '词语'
	        }],
	        /**
	         * @param {点击列的 field 名称} field
	         * @param {点击列的 value 值} value
	         * @param {点击列的整行数据} row
	         * @param {td 元素} $element
	         */
	        onDblClickCell: function(field, value, row, $element) {
	            $element.attr('contenteditable', true);
	            $element.blur(function() {
	                let index = $element.parent().data('index');
	                let tdValue = $element.html();

	                saveData(index, field, tdValue);
	            })
	        }
	    });

	    $getTableData.click(function() {
	        alert(JSON.stringify($table.bootstrapTable('getData')));
	    });

	    function saveData(index, field, value) {
	        var obj={"index":index,"field":field,"stopwords":value,"type":"jieba","method":"add"};
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
	    }

}



function btable04($http){
	 let $table = $('#table');
	    let $addbutton = $('#addbutton');
	    let $delbutton = $('#delbutton');
	    let $getTableData = $('#getTableData');

	    $addbutton.click(function() {
	        $table.bootstrapTable('insertRow', {
	            index: 0,
	            row: {
	            	crtTime: getTimeAll(),
	            	word: '',
	            	value:''
	            }
	        });
	    });

	    $table.bootstrapTable({
	        url: 'httpService/queryNegWord',
	        queryParams:{},
	        toolbar: '#toolbar',
	        search: true,//是否显示右上角的搜索框  
	        clickEdit: true,
	        showToggle: true,
	        pagination: true,       //显示分页条
	        showColumns: true,
	        showPaginationSwitch: true,     //显示切换分页按钮
	        showRefresh: true,      //显示刷新按钮
	        //clickToSelect: true,  //点击row选中radio或CheckBox
	        columns: [{
	            checkbox: true
	        }, {
	            field: 'crtTime',
	            title: '创建时间',
	            edit:false
	        },{
	            field: 'word',
	            title: '负面情绪'
	        },{
	            field: 'value',
	            title: '权值'
	        }],
	        /**
	         * @param {点击列的 field 名称} field
	         * @param {点击列的 value 值} value
	         * @param {点击列的整行数据} row
	         * @param {td 元素} $element
	         */
	        onDblClickCell: function(field, value, row, $element) {
	            $element.attr('contenteditable', true);
	            $element.blur(function() {
	                let index = $element.parent().data('index');
	                let tdValue = $element.html();
	                saveData(index, field, tdValue);
	            })
	        }
	    });

	    $getTableData.click(function() {
	        alert(JSON.stringify($table.bootstrapTable('getData')));
	    });
	    
	    function saveData(i, name, str) {
	    	var ob={name:str};
	    	if(name=='word'){
	    		ob={'word':str};
	    	}else if(name=='value'){
	    		ob={'value':str};
	    	}
	    	
	    	$table.bootstrapTable('updateRow', {
	    		    index: i,
	    		    row: ob
	    	 });
	    }


}

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

var getTimeAll=function(){
	var myDate = new Date();             

	var year=myDate.getFullYear();        //获取当前年
	var month=myDate.getMonth()+1;   //获取当前月
	var date=myDate.getDate();            //获取当前日


	var h=myDate.getHours();              //获取当前小时数(0-23)
	var m=myDate.getMinutes();          //获取当前分钟数(0-59)
	var s=myDate.getSeconds();

	var now=year+'-'+getNow(month)+"-"+getNow(date)+" "+getNow(h)+':'+getNow(m)+":"+getNow(s);
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
	