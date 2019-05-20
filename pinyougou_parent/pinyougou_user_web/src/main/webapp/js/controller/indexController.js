app.controller("indexController",function ($scope,$location,loginService,payService) {

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


//提交订单
    $scope.submitOrder=function(order){
        loginService.submitOrder(order).success(
            function(response){
                alert(response.success);
                if(response.success){
                    // var orderResult = JSON.stringify(response);
                   var orderResult1 = JSON.parse(JSON.stringify(response));
                    var orderResult = JSON.stringify(orderResult1);
                    window.location.href="pay.html#?orderResult="+orderResult;
                }
            }
        );
    }



    //生成二维码
    $scope.createNative=function () {
        //生成二维码前先获取微信接口返回的数据
       var orderResult = JSON.parse($location.search()["orderResult"]);
        // var orderResult = JSON.parse(JSON.stringify($location.search()["orderResult"]));
        $scope.money = (orderResult.total_fee / 100).toFixed(2);
        $scope.out_trade_no = $scope.orderResult.out_trade_no;  //订单号


        //生成二维码
        /*var qr = new QRious({
            element: document.getElementById('qrious'),
            size: 260,
            value: response.code_url,
            level: 'L'
        });*/
        var qrcode = new QRCode(document.getElementById("qrious"), {
            width : 250,
            height : 250,
            correctLevel : QRCode.CorrectLevel.Q,
        });
        qrcode.makeCode($scope.orderResult.code_url);

        //二维码生成成功后，马上开始查询订单支付状态
        queryPayStatus($scope.out_trade_no);
    }


    //查询订单状态
    queryPayStatus=function (out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(function (response) {
            if(response.success){
                window.location.href = "paysuccess.html#?money=" + $scope.money;
            }else{
                if("支付已超时" == response.message){
                    window.location.href = "paytimeout.html";
                }else{
                    window.location.href = "payfail.html";
                }

            }
        })
    }


    //支付成功页，加载支付金额
    $scope.loadMoney=function () {
        $scope.money = $location.search()["money"];
    }

})