package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private TbOrder order;//订单

    private List<OrderItem> orderItemList;//订单清单


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
