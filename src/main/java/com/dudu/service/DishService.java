package com.dudu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dudu.dto.DishDto;
import com.dudu.entity.Dish;
import org.springframework.stereotype.Service;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    void saveWithFlavor(DishDto dishDto);
    //根据id查询菜品信息和对应的口味信息
    DishDto getByIdWithFlavor(Long id);
    //更新菜品信息，同时更新对应的口味信息
    void updateWithFlavor(DishDto dishDto);
    // 删除商品信息
    void deleteWithFlavor(List<Long> id);
    void updateStatus(List<Long> ids,int status);
}
