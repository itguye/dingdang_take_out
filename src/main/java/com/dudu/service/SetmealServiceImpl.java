package com.dudu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dudu.common.CustomException;
import com.dudu.dto.DishDto;
import com.dudu.dto.SetmealDto;
import com.dudu.entity.Setmeal;
import com.dudu.entity.SetmealDish;
import com.dudu.mapper.SetmealMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService{
    @Resource
    private SetmealDishService setmealDishService;
    @Override
    @Transactional
    public void saveWithSetMealDish(SetmealDto setmealDto) {
        // 添加套餐
        this.save(setmealDto);
        // 添加套餐的菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public SetmealDto getSetmealDtoById(Long ids) {
        // 获取套餐信息
        Setmeal setmeal = this.getById(ids);
        // 获取套餐的商品信息
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        // 通过setmeal的Id获取商品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    @Override
    @Transactional
    public void updateWithSetmealDish(SetmealDto setmealDto) {
        // 修改套餐信息
        this.updateById(setmealDto);

        // 修改套餐对应的菜品信息
        SetmealDish setmealDish = new SetmealDish();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmealDto != null, SetmealDish::getDishId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        // 添加新的菜品信息
        // 添加套餐的菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void deleteWithSetmealDishByIds(List<Long> ids) {
        // 删除套餐
        LambdaQueryWrapper<Setmeal> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Setmeal::getStatus, 1);
        queryWrapper1.in(Setmeal::getId, ids);
        int count = this.count(queryWrapper1);
        if (count > 0) {
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        this.removeByIds(ids);
        // 删除套餐对应的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper2);
    }

    @Override
    @Transactional
    public void updateStatus(List<Long> ids,int status) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        this.update(setmeal,queryWrapper);
    }
}
