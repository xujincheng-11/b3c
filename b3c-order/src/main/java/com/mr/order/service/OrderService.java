package com.mr.order.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.mr.bo.UserInfo;
import com.mr.client.AddressClient;
import com.mr.client.GoodsClient;
import com.mr.common.utils.IdWorker;
import com.mr.common.utils.JsonUtils;
import com.mr.common.utils.PageResult;
import com.mr.order.bo.AddressBo;
import com.mr.order.bo.CartBo;
import com.mr.order.bo.OrderBo;
import com.mr.order.mapper.OrderDetailMapper;
import com.mr.order.mapper.OrderMapper;
import com.mr.order.mapper.OrderStatusMapper;
import com.mr.order.pojo.Order;
import com.mr.order.pojo.OrderDetail;
import com.mr.order.pojo.OrderStatus;
import com.mr.service.pojo.Sku;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private GoodsClient goodsClient;
    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper detailMapper;

    @Resource
    private OrderStatusMapper statusMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Transactional//多表要添加事务
    public Long createOrder(OrderBo orderBo, UserInfo userInfo) {
        //前端只传递，id信息，其余数据从数据库查询，防止人员揣改数据提交，

        // 初始化订单数据
        Order order=new Order();
        // 雪花算法，生成 唯一订单id
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);//订单id
        order.setCreateTime(new Date());//订单创建时间
        // 支付方式
        order.setPaymentType(orderBo.getPayMentType());//前台传回的支付方式
        //订单中用户信息
        order.setUserId(userInfo.getId());//用户id
        order.setBuyerNick(userInfo.getUsername());//用户名称
        order.setBuyerRate(false);//买家是否已经评价
        // 收货人信息（调用地址服务查询）根据收货人的id,获取收货人的信息
        AddressBo address= AddressClient.getAddressByID(orderBo.getAddressId());
        order.setReceiver(address.getName());//收货人全名
        order.setReceiverMobile(address.getPhone());//移动电话
        order.setReceiverState(address.getState());//省份
        order.setReceiverCity(address.getCity());//城市
        order.setReceiverDistrict(address.getDistrict());//区/县
        order.setReceiverAddress(address.getAddress());//收货地址，如：xx路xx号
        order.setReceiverZip(address.getZipCode());//邮政编码,如：310001

        //总金额

        Long totalPay=0l;
        //查询sku数据，
        for(CartBo cartBo : orderBo.getCartList()){
            //根据skuId查询sku数据
            cartBo.setSku(goodsClient.querySkuById(cartBo.getSkuId()));
            totalPay+=cartBo.getSku().getPrice() *cartBo.getNum();
        }
        //总金额
        order.setTotalPay(totalPay);
        //实际付款 满减，邮费，积分，等会影响实际价格 目前不做，所以默认是总金额
        order.setActualPay(totalPay);
        // 保存数据
        this.orderMapper.insertSelective(order);


        //组装订单详情，一个订单有多个sku详情
        List<OrderDetail> orderDetailList=new ArrayList<>();
        //一个购物车有多个sku商品和多个商品详情
        orderBo.getCartList().forEach(cartBo -> {
            //填充订单中商品的详情页面
            OrderDetail detail=new OrderDetail();
            detail.setImage(cartBo.getSku().getImages().split(",")[0]);
            detail.setNum(cartBo.getNum());
            detail.setOrderId(orderId);

            detail.setOwnSpec(cartBo.getSku().getOwnSpec());

            detail.setPrice(cartBo.getSku().getPrice());
            detail.setSkuId(cartBo.getSkuId());
            detail.setTitle(cartBo.getSku().getTitle());
            orderDetailList.add(detail);
        });
        //批量保存详情
        detailMapper.insertList(orderDetailList);

        // 保存订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setStatus(1);// 初始状态为未付款
        // 保存订单状态
        this.statusMapper.insertSelective(orderStatus);

        //调用goodfeign执行库存减去操作。涉及到，并发下，锁， 分布式事务的问题

        return orderId;
    }

    public Order queryById(Long id) {
        // 查询订单
        Order order = this.orderMapper.selectByPrimaryKey(id);

        // 查询订单详情
        OrderDetail detail = new OrderDetail();
        detail.setOrderId(id);
        List<OrderDetail> details = this.detailMapper.select(detail);
        order.setOrderDetails(details);

        // 查询订单状态
        OrderStatus status = this.statusMapper.selectByPrimaryKey(order.getOrderId());
        order.setStatus(status.getStatus());
        return order;
    }

    public PageResult<Order> queryUserOrderList(Integer page, Integer rows, Integer status,UserInfo userInfo) {
        try {
            // 设置分页等其起始值，每页条数
            PageHelper.startPage(page, rows);

            // 创建查询条件用户id和状态，由于需要三表联查，sql注解拼接麻烦，所以采用了xml形式
            Page<Order> pageInfo = (Page<Order>) this.orderMapper.queryOrderList(34l, status);

            return new PageResult<>(pageInfo.getTotal(), pageInfo);
        } catch (Exception e) {
            logger.error("查询订单出错", e);
            return null;
        }
    }

    @Transactional
    public Boolean updateStatus(Long id, Integer status) {
        OrderStatus record = new OrderStatus();
        record.setOrderId(id);
        record.setStatus(status);
        // 根据状态判断要修改的时间
        switch (status) {
            case 2:
                record.setPaymentTime(new Date());// 付款
                break;
            case 3:
                record.setConsignTime(new Date());// 发货
                break;
            case 4:
                record.setEndTime(new Date());// 确认收获，订单结束
                break;
            case 5:
                record.setCloseTime(new Date());// 交易失败，订单关闭
                break;
            case 6:
                record.setCommentTime(new Date());// 评价时间
                break;
            default:
                return null;
        }
        int count = this.statusMapper.updateByPrimaryKeySelective(record);
        return count == 1;
    }

}
