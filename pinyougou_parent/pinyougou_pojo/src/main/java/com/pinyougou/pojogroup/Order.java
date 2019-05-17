package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbOrder;

import java.io.Serializable;
import java.util.HashMap;

public class Order implements Serializable {
    public HashMap getDateMap() {
        return dateMap;
    }

    public void setDateMap(HashMap dateMap) {
        this.dateMap = dateMap;
    }

    private HashMap dateMap;

    private TbOrder tbOrder;

    public TbOrder getTbOrder() {
        return tbOrder;
    }

    public void setTbOrder(TbOrder tbOrder) {
        this.tbOrder = tbOrder;
    }
}
