package com.dudu.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dudu.dto.SetmealDto;
import com.dudu.entity.Setmeal;
import com.dudu.entity.SetmealDish;
import com.dudu.mapper.SetmealDishMapper;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {

}
