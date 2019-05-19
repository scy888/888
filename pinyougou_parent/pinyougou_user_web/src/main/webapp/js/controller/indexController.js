app.controller("indexController",function ($scope,loginService) {

    //初始化数据
    $scope.initData=function () {
        loginService.loginName().success(function (response) {
            $scope.loginName = response.loginName;
        })
    }
    //查询用户订单
    $scope.findOrderByUserId=function () {
        loginService.findOrderByUserId().success(
            function (response) {
                $scope.orderList=response;
            }
        )
    }

})