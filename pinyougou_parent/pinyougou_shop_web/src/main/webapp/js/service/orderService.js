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
})