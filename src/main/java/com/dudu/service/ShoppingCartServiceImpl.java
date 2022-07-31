package com.dudu.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dudu.entity.ShoppingCart;
import com.dudu.mapper.ShoppingCartMapper;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
