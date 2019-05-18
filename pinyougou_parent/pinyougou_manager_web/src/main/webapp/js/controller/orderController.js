//控制层
app.controller('orderController', function ($scope, $controller, orderService, orderItemService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        orderService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    $scope.findOrderItem = function (orderId) {
        orderItemService.findByOrderId(orderId).success(function (response) {
            $scope.orderItemList = response;
        })
    }

    //分页
    $scope.findPage = function (page, rows) {
        orderService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        orderService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = orderService.update($scope.entity); //修改
        } else {
            serviceObject = orderService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        orderService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }

    $scope.status = ['', '未付款', '已付款', '未发货', '已发货', '交易成功', '交易关闭', '待评价'];
    $scope.payType = ['', '在线支付', '货到付款'];
    $scope.sourceType = ['', 'app端', 'pc端', 'M端', '微信端', '手机qq端']

    $scope.searchEntity = {};//定义搜索对象
    //搜索
    $scope.search = function (page, rows) {
        orderService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数

            }
        );
    }
    $scope.outPutAsXlsx = function (putAll) {
        if(putAll==1){
            var order = JSON.stringify($scope.searchEntity).replace(/(\")/g,'\'');

        }else {
            var order = null;
        }
        alert(order)
        var options={
        url:'../userOrder/findOrderAndOrderItem.do',
        data:{Order:order},
        method:'post'
        };


        DownLoadFile(options)
    }
    DownLoadFile = function (options) {
        var config = $.extend(true, { method: 'post' }, options);
        var $iframe = $('<iframe id="down-file-iframe" />');
        var $form = $('<form target="down-file-iframe" method="' + config.method + '" />');
        $form.attr('action', config.url);
        for (var key in config.data) {
            $form.append('<input type="hidden" name="' + key + '" value="' + config.data[key] + '" />');
        }
        $iframe.append($form);
        $(document.body).append($iframe);
        $form[0].submit();
        $iframe.remove();
    }


    //条件展开
    $scope.openTime='展开';
    $scope.openField='展开';
});
