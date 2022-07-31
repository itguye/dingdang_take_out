package com.dudu.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dudu.entity.DishFlavor;
import com.dudu.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends  ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService{
}
