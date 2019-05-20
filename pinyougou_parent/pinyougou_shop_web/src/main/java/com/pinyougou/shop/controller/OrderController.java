package com.pinyougou.shop.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderItemService;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

import entity.Result;

/**
 * @program: butuofa
 * @description: 订单控制器
 * @author: Mr.Wang
 * @create: 2019-05-17 16:47
 **/
@RestController
@RequestMapping("/form")
public class OrderController {
    @Reference
    private OrderService orderService;

    @Reference
    private OrderItemService orderItemService;

    //查询
    @RequestMapping("/search")
    public List<TbOrder> findallOrder() {

        String selleId = SecurityContextHolder.getContext().getAuthentication().getName();
        //添加查询条件
        List<TbOrder> orders = orderService.findOrdersBySellId(selleId);
        return orders;

    }

    //根据订单id查询
//    @RequestMapping("/findByOrderId")
//    public List<TbOrderItem> preserve( String orderIdStr){
//        return  orderItemService.findByOrderId(orderIdStr);
//    }

    //查询数据id
    @RequestMapping("/findByQueryId")
    public TbOrder findByQueryId(Long orderId) {
        return orderService.findByQueryId(orderId);
    }


    //修改
    @RequestMapping("/modification")
    public Result modification(TbOrder TbOrder) {
        try {
            orderService.modification(TbOrder);
            return new Result(true, "操作结果成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "操作结果失败");
    }

    //根据时间查找时间段每天的销售额
    @RequestMapping("/searchDaySale")
    public Map searchDaySale(@DateTimeFormat(pattern = "yyyy-MM-dd")Date start,
                             @DateTimeFormat(pattern = "yyyy-MM-dd")Date end){
        return orderService.searchDaySale(start, end);
    }
}