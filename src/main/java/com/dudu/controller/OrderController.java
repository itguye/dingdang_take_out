package com.dudu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dudu.common.Result;
import com.dudu.dto.DishDto;
import com.dudu.dto.OrdersDto;
import com.dudu.entity.*;
import com.dudu.service.OrderDetailService;
import com.dudu.service.OrderService;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单管理
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;
    @Resource
    private OrderDetailService orderDetailService;
    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return Result.success("下单成功");
    }

    @GetMapping("/page")
    public Result<Page> showEmployeeInfo(int page, int pageSize, Long number, String beginTime, String endTime) { // page=1&pageSize=10 &name
        log.info("====================");
        log.info("page = {},pageSize = {},name = {},number={}" ,page,pageSize,number);
        log.info("beginTime = {},endTime={}" ,beginTime,endTime);
        Page pageInfo_Orders = null;
        Page<OrdersDto> sumPages = new Page<>();
        try {
            //构造分页构造器
            pageInfo_Orders = new Page(page, pageSize);
            //构造条件构造器
            LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
            // 添加订单号条件
            queryWrapper.eq(number != null, Orders::getNumber, number);
            if (beginTime != null && endTime != null) {
                // 添加开始时间与结束时间
                queryWrapper.between(Orders::getOrderTime,beginTime,endTime);
                queryWrapper.between(Orders::getCheckoutTime,beginTime,endTime);
            }
            //添加排序条件
            queryWrapper.orderByDesc(Orders::getOrderTime);
            //执行查询
            orderService.page(pageInfo_Orders, queryWrapper);
            //对象拷贝
            BeanUtils.copyProperties(pageInfo_Orders,sumPages,"records");
            List<Orders> records = pageInfo_Orders.getRecords();
            // 获取OrdersDetail的数据
            List<OrdersDto> list  = records.stream().map((item) ->{
                OrdersDto ordersDto = new OrdersDto();
                BeanUtils.copyProperties(item,ordersDto);
                Long orderId = item.getId();//订单ID
                LambdaQueryWrapper<OrderDetail> queryWrapper_orderDetail = new LambdaQueryWrapper<>();
                queryWrapper_orderDetail.eq(OrderDetail::getOrderId, orderId);
                ordersDto.setOrderDetails(orderDetailService.list(queryWrapper_orderDetail));
                return ordersDto;
            }).collect(Collectors.toList());
            sumPages.setRecords(list);// 自己来重新加载Records
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return Result.error("查询失败");
        }
        return Result.success(sumPages);
    }

    @PutMapping
    public Result<String> order(@RequestBody Orders orders) {
        log.info("====================");
        log.info("status:{},id:{}",orders.getStatus(),orders.getId());

        //订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
        orderService.updateById(orders);
        if (orders.getStatus() == 3) {
            return Result.success("订单已派送");
        } else if (orders.getStatus() == 4) {
            return Result.success("订单已完成");
        } else if (orders.getStatus() == 5) {
            return Result.success("订单已取消");
        }

        return Result.success("订单状态已修改");
    }
}
