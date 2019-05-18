package com.pinyougou.shop.controller;



import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    //查询
    @RequestMapping("/search")
    public List<TbOrder> findallOrder (){

        String selleId = SecurityContextHolder.getContext().getAuthentication().getName();
        //添加查询条件
        List<TbOrder> orders = orderService.findOrdersBySellId(selleId);
        return orders;

    }



}
