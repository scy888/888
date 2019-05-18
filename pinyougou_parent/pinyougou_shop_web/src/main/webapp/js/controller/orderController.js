//控制层
app.controller('orderController',function ($scope,$controller,orderService) {
    $controller('baseController',{$scope:$scope});//继承

    //读取列表数据到表单中
    // $scope.findByParentId=function () {
    //     orderService.findByParentId().success(function (response) {
    //         $scope.list=response;
    //     });
    //
    // }

    //读取列表数据绑定到表单中
    $scope.findByParentId=function(){
        orderService.findByParentId().success(
            function(response){
                $scope.list=response;
            }
        );
    }
    //
    $scope.findByOrderId=function(orderIdStr){
        orderService.findByOrderId(orderIdStr).success(
            function(response){
                $scope.orderItemList=response;
            }
        );
    }
    //修改
    $scope.modification=function(OrderId){
        orderService.findByParentId(OrderId).success(
            function (response) {
                $scope.modificationorderid=response;
            }
        )
    }

    //查询
    $scope.findByQueryId=function(orderId){
        orderService.findByQueryId(orderId).success(
            function (response) {
                $scope.findByQueryId=response;
            }
        )
    }


    /**
     * 显示状态 已未付款','已付款','未发货','已发货','交易成功','交易关闭','待评价'的状态
     */
    $scope.status=['未付款','已付款','未发货','已发货','交易成功','交易关闭','待评价'];//商品状态


})