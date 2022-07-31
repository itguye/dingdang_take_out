package com.dudu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dudu.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
