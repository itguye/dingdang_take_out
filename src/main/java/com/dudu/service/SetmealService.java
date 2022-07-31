package com.dudu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dudu.dto.SetmealDto;
import com.dudu.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithSetMealDish(SetmealDto setmealDto);
    SetmealDto getSetmealDtoById(Long ids);
    void updateWithSetmealDish(SetmealDto setmealDto);
    void  deleteWithSetmealDishByIds(List<Long> ids);
    void updateStatus(List<Long> ids,int status);
}
