Date.prototype.format = function(fmt) {
  var o = {
      'M+' : this.getMonth() + 1                      // 月份(1-2占位符)
    , 'd+' : this.getDate()                           // 日(1-2占位符)
    , 'h+' : this.getHours()                          // 小时(1-2占位符)
    , 'm+' : this.getMinutes()                        // 分(1-2占位符)
    , 's+' : this.getSeconds()                        // 秒(1-2占位符)
    , 'q+' : Math.floor((this.getMonth() + 3) / 3)    // 季度(1-2占位符)
    , 'S'  : this.getMilliseconds()                   // 毫秒(1个占位符，1-3位数字)
  };
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
  }
  for (var k in o) {
    if(new RegExp('('+ k +')').test(fmt)) {
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (('00' + o[k]).substr(('' + o[k]).length)));
    }
  }
  return fmt;
}
Date.prototype.defaultDateTimeFormat = function() {
  return this.format('yyyy-MM-dd hh:mm:ss');
}
Date.prototype.defaultDateFormat = function() {
  return this.format('yyyy-MM-dd');
}
Date.prototype.HourDateFormat = function() {
	  return this.format('yyyy-MM-dd hh');
	}
Date.prototype.defaultTimeFormat = function() {
  return this.format('hh:mm:ss');
}
Date.prototype.digitDateTimeFormat = function() {
  return this.format('yyyyMMddhhmmss');
}
String.prototype.replaceAll = function(reallyDo, replaceWith, ignoreCase) {
  if (!RegExp.prototype.isPrototypeOf(reallyDo)) {
    return this.replace(new RegExp(reallyDo, (ignoreCase ? "gi": "g")), replaceWith);
  } else {
    return this.replace(reallyDo, replaceWith);
  }
}
String.prototype.startWith = function(str) {
  var reg = new RegExp('^' + str);
  return reg.test(this);
}
String.prototype.endWith = function(str) {
  var reg = new RegExp(str + '$');
  return reg.test(this);
}

var shadowDialogCostValue = 0;
var shadowDialogCostInterval;
var shadownDialogFunctionId;
function disableButton($scope, id, dataDiv) {
  $('#' + id).prop('disabled', true);
  $('#' + id).blur();
  $('#' + dataDiv).hide();
  $scope.hasServerError = false;
  $scope.serverMessage = '';
  $('#shadowDialog').modal({backdrop: 'static', keyboard: false});
  $('#shadowDialogTitle').text(document.title);
  $('#shadowDialog').modal('show');
  shadowDialogCostValue = 1;
  $('#shadowDialogCost').text(shadowDialogCostValue);
  shadownDialogFunctionId = id;
  shadowDialogCostInterval = self.setInterval('shadowDialogCostAdd();', 1000);
}

function resetDisableButton($scope, id, dataDiv) {
  $('#' + id).prop('disabled', false);
  $('#' + id).blur();
  $('#' + dataDiv).show();
  if (id == shadownDialogFunctionId) {
    $('#shadowDialog').modal('hide');
    $('#shadowDialogCost').text('');
    shadowDialogCostValue = 0;
    window.clearInterval(shadowDialogCostInterval);
  }
}

function shadowDialogCostAdd() {
  shadowDialogCostValue = shadowDialogCostValue + 1
  $('#shadowDialogCost').text(shadowDialogCostValue);
}

function vcenter(adjust) {
  $('.vcenter').each(function() {
    var h = ($(this).parent().height() - $(this).height()) / 2;
    h = h + adjust;
    $(this).css('margin-top', h);
  });
}

function vcenterForDialog(adjust) {
  $('.vcenterForDialog').each(function() {
    var h = ($(this).parent().height() - $(this).height()) / 2;
    h = h + adjust;
    $(this).css('margin-top', h);
  });
}

(function($){
  $.isBlank = function(obj){
    return(!obj || $.trim(obj) === '');
  };
})(jQuery);

var _v = $('meta[name="v"]').prop('content');
var _firstQuery = '?v=' + _v;

function openMyUrl(url, query) {
  var q = encodeURI(_firstQuery + query);
  var url = url + q;
  window.open(url);
}

function init_datetime(j, fmt) {
  var minView = '';
  if (fmt == 'yyyy-mm-dd') {
    minView = 2;
  } else {
    minView = 1;
  }
  j.datetimepicker('remove');
  j.datetimepicker(
    {
      language: 'zh-CN',
      weekStart: 1,
      startView: 2,
      minView: minView,
      format: fmt,
      autoclose: 1,
      todayBtn: 1,
      todayHighlight: 1,
      forceParse: 1
    }
  );
}

function showPaginationBar(paginationData) {
  var prefix = '<p align="center"><nav class="form-inline"><ul class="pager">';
  var first  = '<li><input type="button" class="btn btn-default btn-sm w60 ml5" value="首　页" onclick="paginationBar_jump_first_page();" /></li>';
  var prev   = '<li><input type="button" class="btn btn-default btn-sm w60 ml5" value="上一页" onclick="paginationBar_jump_prev_page();" /></li>';
  var next   = '<li><input type="button" class="btn btn-default btn-sm w60 ml5" value="下一页" onclick="paginationBar_jump_next_page();" /></li>';
  var last   = '<li><input type="button" class="btn btn-default btn-sm w60 ml5" value="尾　页" onclick="paginationBar_jump_last_page();" /></li>';
  var pageNo = '<span class="ml5">共<span id="paginationBar_totalPage">' + paginationData.totalPage + '</span>页</span>';
  var totalCount = '<span class="ml5">总' + paginationData.totalCount + '条</span>';
  var pageSize = '<span class="ml5">每页</span><input id="paginationBar_pageSize" value="' + paginationData.pageSize + '" class="form-control ml5" size="5" /><span class="ml5">条</span>'
  var jPageNum = '<span class="ml5">到第</span><input id="paginationBar_pageNo" value="' + paginationData.currentPage + '" class="form-control ml5" size="5" /><span class="ml5">页</span><input type="button" class="btn btn-default btn-sm w60 ml5" id="jPageNum_page" value="确　定" onclick="paginationBar_jump_some_page();" />'
  var suffix = '</ul></nav></p>';
  var paginationBar = prefix + first + prev + next + last + pageNo + totalCount + pageSize + jPageNum + suffix;
  return paginationBar;
}

var _paginationBar_callback;
function paginationBar_jump_first_page() {
  var newPageNo = 1;
  $('#paginationBar_pageNo').val(newPageNo);
  paginationBar_jump_some_page();
}
function paginationBar_jump_prev_page() {
  var newPageNo = parseInt($('#paginationBar_pageNo').val()) - 1;
  $('#paginationBar_pageNo').val(newPageNo);
  paginationBar_jump_some_page();
}
function paginationBar_jump_next_page() {
  var newPageNo = parseInt($('#paginationBar_pageNo').val()) + 1;
  $('#paginationBar_pageNo').val(newPageNo);
  paginationBar_jump_some_page();
}
function paginationBar_jump_last_page() {
  var newPageNo = parseInt($('#paginationBar_totalPage').text());
  $('#paginationBar_pageNo').val(newPageNo);
  paginationBar_jump_some_page();
}
function paginationBar_jump_some_page() {
  _paginationBar_callback();
}
function paginationBar_getQueryString() {
  var pageNo;
  var pageSize;
  if ($('#paginationBar_pageNo').val() == undefined) {
    pageNo = 1;
  } else {
    pageNo = $('#paginationBar_pageNo').val();
  }
  if ($('#paginationBar_pageSize').val() == undefined) {
    pageSize = 10;
  } else {
    pageSize = $('#paginationBar_pageSize').val();
  }
  var queryString = 'pageNo=' + pageNo + '&pageSize=' + pageSize;
  return queryString;
}