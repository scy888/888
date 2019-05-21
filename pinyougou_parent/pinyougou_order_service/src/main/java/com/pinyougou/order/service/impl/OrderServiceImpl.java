package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.pojogroup.Order;
import com.pinyougou.utils.IdWorker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import entity.PageResult;


/**
 * 业务逻辑实现
 *
 * @author Steven
 */
@Service(timeout = 5000)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbGoodsMapper goodsMapper;

    /**
     * 查询全部已付款订单
     */
    @Override
    public List<TbOrder> findAll() {
        TbOrder where = new TbOrder();
        where.setStatus("1");
        return orderMapper.select(where);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {

        PageResult<TbOrder> result = new PageResult<TbOrder>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //查询数据
        List<TbOrder> list = orderMapper.select(null);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(list);
        result.setTotal(info.getTotal());
        return result;
    }

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker worker;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbPayLogMapper payLogMapper;


    /**
     * 增加
     *
     * @param order 这个对象在前端，只有支付方式与收件人、用户等信息
     */
    @Override
    public void add(TbOrder order) {
        //先查询所有的购物车列表出来
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

        long totalFee = 0;  //订单支付总金额
        //记录所有订单号
        List<Long> orderList = new ArrayList<>();
        //订单拆单-一个商家一张订单
        for (Cart cart : cartList) {
            //1、创建新的订单对象,绑定所需要属性
            TbOrder beSave = new TbOrder();
            //生成唯一订单号
            long orderId = worker.nextId();
            //记录订单号
            orderList.add(orderId);
            beSave.setOrderId(orderId);  //订单id
            double totalMoney = 0.0;  //实付金额(单个商家总金额)
            beSave.setPaymentType(order.getPaymentType());  //支付方式
            beSave.setStatus("1");  //未付款状态

            beSave.setCreateTime(new Date());
            beSave.setUpdateTime(beSave.getCreateTime());
            beSave.setUserId(order.getUserId());  //下单人
            beSave.setReceiverAreaName(order.getReceiverAreaName());//地址
            beSave.setReceiverMobile(order.getReceiverMobile());//手机号
            beSave.setReceiver(order.getReceiver());//收货人
            beSave.setSourceType(order.getSourceType());  //订单来源
            beSave.setSellerId(cart.getSellerId());  //商家Id

            //2、保存订单的商品列表
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setId(worker.nextId());
                //绑定订单号
                orderItem.setOrderId(orderId);
                //计算总金额
                totalMoney += orderItem.getTotalFee().doubleValue();
                orderItemMapper.insertSelective(orderItem);
            }
            //保存订单
            beSave.setPayment(new BigDecimal(totalMoney));
            //计算订单总金额
            totalFee += (long) (totalMoney * 100);
            orderMapper.insertSelective(beSave);
        }
        //清空购物车
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());

        //生成日志并保存
        if ("1".equals(order.getPaymentType())) {
            TbPayLog payLog = new TbPayLog();
            //生成支付日志单号
            payLog.setOutTradeNo(worker.nextId() + "");
            payLog.setCreateTime(new Date());
            payLog.setTotalFee(totalFee);  //订单总金额(分)
            payLog.setUserId(order.getUserId());
            payLog.setTradeState("0");  //0未支付 1已支付
            payLog.setPayType("1");  //微信支付
            //转换订单列表以","分隔
            String orderIds = orderList.toString().replace("[", "").replace("]", "").replace(" ", "");
            payLog.setOrderList(orderIds);
            //把日志先保存数据库
            payLogMapper.insertSelective(payLog);
            //保存订单到redis
            redisTemplate.boundHashOps("payLogs").put(order.getUserId(), payLog);
        }
    }

    @Override
    public TbPayLog searchPayLogFromRedis(String userId) {
        TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLogs").get(userId);
        return payLog;
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        //1. 修改支付日志状态
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setTradeState("1");  //已支付
        payLog.setPayTime(new Date());  //支付时间
        payLog.setTransactionId(transaction_id);  //微信支付单号
        payLogMapper.updateByPrimaryKeySelective(payLog);
        //2. 修改关联的订单的状态
        String[] orderList = payLog.getOrderList().split(",");
        for (String orderId : orderList) {
            TbOrder beUpdate = new TbOrder();
            beUpdate.setOrderId(new Long(orderId));
            beUpdate.setStatus("2");  //已支付
            orderMapper.updateByPrimaryKeySelective(beUpdate);
        }
        //3. 清除缓存中的支付日志对象
        redisTemplate.boundHashOps("payLogs").delete(payLog.getUserId());
    }

    /**
     * 查询订单和订单里的商品详情
     *
     * @param border
     * @return
     */
    @Override
    public List<TbOrder> findOrderAndOrderItem(Order border) {
        Example example = createExample(border);
        List<TbOrder> tbOrderList = orderMapper.selectByExample(example);
//		List<TbOrder> tbOrderList=null;
//        if (tbOrderIds != null) {
//            Example example=new Example(TbOrder.class);
//            Example.Criteria criteria = example.createCriteria();
//            criteria.andIn("orderId",Arrays.asList(tbOrderIds));
//            tbOrderList = orderMapper.selectByExample(example);
//        }else {
//            tbOrderList = orderMapper.select(null);
//        }

        for (TbOrder tbOrder : tbOrderList) {
            TbOrderItem where = new TbOrderItem();
            where.setOrderId(tbOrder.getOrderId());
            List<TbOrderItem> tbOrderItemList = orderItemMapper.select(where);
            tbOrder.setOrderItemList(tbOrderItemList);
        }
        return tbOrderList;
    }


    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        //数组转list
        List longs = Arrays.asList(ids);
        //构建查询条件
        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", longs);

        //跟据查询条件删除数据
        orderMapper.deleteByExample(example);
    }

    /**
     * 日期转换方法
     *
     * @param timeStr
     * @return
     */
    private Date parseToDate(String timeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(timeStr);
        } catch (ParseException e) {
            System.out.println("日期格式错误!");
        }
        return date;
    }

    /**
     * 添加日期查询条件
     *
     * @param timeCriteria
     */
    private void addTimeCriteria(Map timeCriteria, String timeProperty, Example.Criteria criteria) {
        if (timeCriteria != null && timeCriteria.size() != 0) {
            if (timeCriteria.get("start") != null && !timeCriteria.get("start").equals("")) {
                criteria.andGreaterThanOrEqualTo(timeProperty, parseToDate((String) timeCriteria.get("start")));
            }
            if (timeCriteria.get("end") != null && !timeCriteria.get("end").equals("")) {
                criteria.andLessThanOrEqualTo(timeProperty, parseToDate((String) timeCriteria.get("end")));
            }
        }
    }

    @Override
    public PageResult findPage(Order border, int pageNum, int pageSize) {
        PageResult<TbOrder> result = new PageResult<TbOrder>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //查询数据
        Example example = createExample(border);
        List<TbOrder> list = orderMapper.selectByExample(example);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(list);
        result.setTotal(info.getTotal());

        return result;
    }

    /**
     * 构建查询条件
     *
     * @param border
     * @return
     */
    private Example createExample(Order border) {
        //构建查询条件
        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        TbOrder order = border.getTbOrder();
        Map dateMap = border.getDateMap();


        if (dateMap != null) {
            for (Object o : dateMap.entrySet()) {
                Map.Entry<String, Map> entry = (Map.Entry<String, Map>) o;
                addTimeCriteria(entry.getValue(), entry.getKey(), criteria);
            }
        }

        if (order != null) {
            //如果字段不为空
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andLike("paymentType", "%" + order.getPaymentType() + "%");
            }
            //如果字段不为空
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andLike("postFee", "%" + order.getPostFee() + "%");
            }
            //如果字段不为空
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andLike("status", "%" + order.getStatus() + "%");
            }
            //如果字段不为空
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andLike("shippingName", "%" + order.getShippingName() + "%");
            }
            //如果字段不为空
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andLike("shippingCode", "%" + order.getShippingCode() + "%");
            }
            //如果字段不为空
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andLike("userId", "%" + order.getUserId() + "%");
            }
            //如果字段不为空
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andLike("buyerMessage", "%" + order.getBuyerMessage() + "%");
            }
            //如果字段不为空
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andLike("buyerNick", "%" + order.getBuyerNick() + "%");
            }
            //如果字段不为空
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andLike("buyerRate", "%" + order.getBuyerRate() + "%");
            }
            //如果字段不为空
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andLike("receiverAreaName", "%" + order.getReceiverAreaName() + "%");
            }
            //如果字段不为空
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andLike("receiverMobile", "%" + order.getReceiverMobile() + "%");
            }
            //如果字段不为空
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andLike("receiverZipCode", "%" + order.getReceiverZipCode() + "%");
            }
            //如果字段不为空
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andLike("receiver", "%" + order.getReceiver() + "%");
            }
            //如果字段不为空
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andLike("invoiceType", "%" + order.getInvoiceType() + "%");
            }
            //如果字段不为空
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andLike("sourceType", "%" + order.getSourceType() + "%");
            }
            //如果字段不为空
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andLike("sellerId", "%" + order.getSellerId() + "%");
            }

        }
        return example;
    }

    /**
     * 商家商品后台查询id
     */
    @Override
    public List<TbOrder> findOrdersBySellId(String sellerId) {
        TbOrder where = new TbOrder();
        where.setSellerId(sellerId);
        List<TbOrder> orders = orderMapper.select(where);
        return orders;
    }

    /**
     * 查询
     *
     * @param orderId
     * @return
     */
    @Override
    public TbOrder findByQueryId(Long orderId) {

        TbOrder where = new TbOrder();
        where.setOrderId(orderId);
        return orderMapper.selectOne(where);


    }


    /**
     * 修改
     *
     * @param order
     */
    @Override
    public void modification(TbOrder order) {
    }

    /**
     * 搜索每天销售额
     *
     * @param start
     * @param end
     * @return
     */
    @Override
    public Map searchDaySale(Date start, Date end) {
        Map map = null;
        try {
            map = new LinkedHashMap();
            Calendar calendar = Calendar.getInstance();
            this.findOneDaySale(map, start);
            while (!start.equals(end)) {
                calendar.setTime(start);
                calendar.add(Calendar.DATE, 1); // 日期加1天
                start = calendar.getTime();
                this.findOneDaySale(map, start);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 根据时间日期查找当天销售量 储存到map集合
     *
     * @param map
     * @param date
     * @return
     */
    public Map findOneDaySale(Map map, Date date) {
        BigDecimal saleNumber = BigDecimal.valueOf(0);//当日销售额
        //构建查询条件
        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        //操作时间类
        Calendar calendar = Calendar.getInstance();
        //一天24小时  24点---23:59:59点
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, 24 * 60 * 60 - 1);
        Date dayEndTime = calendar.getTime();
        criteria.andGreaterThanOrEqualTo("paymentTime", date);//大于等于
        criteria.andLessThanOrEqualTo("paymentTime", dayEndTime);//小于等于
        List<TbOrder> orderList = orderMapper.selectByExample(example);

        //遍历每一个订单
        TbOrderItem orderItem = null;
        for (TbOrder order : orderList) {
            orderItem = new TbOrderItem();
            orderItem.setOrderId(order.getOrderId());
            List<TbOrderItem> orderItemList = orderItemMapper.select(orderItem);
            //遍历订单所买的商品
            for (TbOrderItem tbOrderItem : orderItemList) {
                BigDecimal totalSale = tbOrderItem.getTotalFee();
                saleNumber = saleNumber.add(totalSale);
            }

        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = dateFormat.format(date);
        System.out.println(formatDate);
        map.put(formatDate, saleNumber);
        return map;
    }

    /**
     * 订单发货
     *
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(String[] ids, String status) {
        //修改的结果
        TbOrder record = new TbOrder();
        record.setStatus(status);
        record.setConsignTime(new Date());
        //构建修改范围
        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        List longs = Arrays.asList(ids);
        criteria.andIn("orderId", longs);

        //开始更新
        orderMapper.updateByExampleSelective(record, example);
    }

    /**
     * 查询已付款订单分页
     *
     * @param tbOrder
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult findOrderPage(TbOrder tbOrder, int pageNum, int pageSize) {
        PageResult<TbOrder> result = new PageResult<TbOrder>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);
        //构建查询条件
        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andGreaterThanOrEqualTo("status", "2");
        List<TbOrder> list = orderMapper.selectByExample(example);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(list);
        result.setTotal(info.getTotal());

        return result;
    }

    /**
     * 搜索时间段每个商品的销售额
     *
     * @param start
     * @param end
     * @return
     */
    @Override
    public List<TbItem> searchDayGoodsSale(Date start, Date end) {

        List<TbItem> tbItemList = new ArrayList<>();

        //1.拿到所有的订单
        //构建查询条件
        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        //操作时间类
        Calendar calendar = Calendar.getInstance();
        //一天24小时  24点---23:59:59点
        calendar.setTime(end);
        calendar.add(Calendar.SECOND, 24 * 60 * 60 - 1);
        Date endTime = calendar.getTime();
        criteria.andGreaterThanOrEqualTo("paymentTime", start);//大于等于
        criteria.andLessThanOrEqualTo("paymentTime", endTime);//小于等于
        List<TbOrder> orderList = orderMapper.selectByExample(example);

        //2.拿到每个订单下的商品
        TbOrderItem orderItem = null;
        List<TbOrderItem> orderItemList = new ArrayList<>();//用来放所有的订单下的商品
        //遍历每一个订单
        for (TbOrder order : orderList) {
            orderItem = new TbOrderItem();
            orderItem.setOrderId(order.getOrderId());
            List<TbOrderItem> orderItems = orderItemMapper.select(orderItem);
            orderItemList.addAll(orderItems);
        }
        //3.拿到所有的TbItem
        List<TbItem> itemList = itemMapper.select(null);
        //4.清除不在订单上的tbItem，并把在订单上的item放到item的List<TbOrderItem> orderItemList集合里
        List<TbOrderItem> orderItems = null;
        for (TbItem tbItem : itemList) {
            orderItems = new ArrayList<>();
            BigDecimal totalMoney = BigDecimal.valueOf(0);
            for (TbOrderItem tbOrderItem : orderItemList) {
                if (tbItem.getId().equals(tbOrderItem.getItemId())) {
                    orderItems.add(tbOrderItem);
                }
            }
            if (orderItems.size() > 0) {
                tbItem.setOrderItemList(orderItemList);
                for (TbOrderItem tbOrderItem : orderItemList) {
                    totalMoney = totalMoney.add(tbOrderItem.getTotalFee());
                }
                tbItem.setItemTotalFee(totalMoney);
                tbItemList.add(tbItem);
            }
        }
        return tbItemList;
    }

}
