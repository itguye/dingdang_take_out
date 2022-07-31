package com.dudu.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dudu.entity.OrderDetail;
import com.dudu.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService{
}
