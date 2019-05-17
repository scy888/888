package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbOrder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Order implements Serializable {
    private TbOrder order;//订单

    private List<OrderItem> orderItemList;//订单清单
    private HashMap dateMap;

    public HashMap getDateMap() {
        return dateMap;
    }

    public void setDateMap(HashMap dateMap) {
        this.dateMap = dateMap;
    }


    public TbOrder getOrder() {
        return order;
    }

    public void setOrder(TbOrder order) {
        this.order = order;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
