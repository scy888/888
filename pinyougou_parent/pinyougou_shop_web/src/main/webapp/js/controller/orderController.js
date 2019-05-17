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
    /**
     * 显示状态 已收货 未发货 未收到 的状态
     */
    $scope.status=['已收货','未发货','关闭'];//商品状态
})