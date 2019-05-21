package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojogroup.Order;
import com.pinyougou.user.service.UserService;
import entity.PageResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.user.controller
 * @date 2019-4-29
 */
@RestController
@RequestMapping("login")
public class LoginController {
    @Reference
    private UserService userService;
    @RequestMapping("info")
    public Map info(){
        Map map = new HashMap();
        //用户登录名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("loginName", username);
        return map;
    }


    /**查询用户订单
     * @return
     */
    @RequestMapping("findOrderByUserId")
    public PageResult findOrderByUserId(String status, int page, int rows ){
        //用户登录名
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbOrder tbOrder = new TbOrder();
        tbOrder.setStatus(status);
        tbOrder.setUserId(userId);
        return userService.findOrderByUserId(tbOrder,page,rows);
    }

}
