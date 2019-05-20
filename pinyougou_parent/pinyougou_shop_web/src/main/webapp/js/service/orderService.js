//服务层
app.service('orderService',function ($http) {
    /**
     * 商家商品订单查询
     * @returns {*}
     */
    //读取数据到当前数据
    this.findByParentId=function () {
        return $http.get("../form/search.do");
    }

    this.findByQueryId=function (orderIdStr) {
        return $http.get("../from/findByQueryId.do？orderIdStr="+orderIdStr);
    }


    this.searchDaySale=function (startTime,endTime) {
        return $http.get("../form/searchDaySale.do?start="+startTime+"&end="+endTime);
    }

    //读取列表数据绑定到表单中
    this.findAll=function(){
        return $http.get('../form/findAll.do');
    }

    //更改订单状态
    this.updateStatus=function(ids,status){
        return $http.get('../form/updateStatus.do?ids='+ids+"&status="+status);
    }
    //搜索分页
    this.search=function(page,rows,searchEntity){
        return $http.post('../form/searchAndPaging.do?page='+page+"&rows="+rows, searchEntity);
    }

})