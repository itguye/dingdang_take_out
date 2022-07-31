package com.dudu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dudu.common.Result;
import com.dudu.constant.RedisConstant;
import com.dudu.dto.DishDto;
import com.dudu.entity.Category;
import com.dudu.entity.Dish;
import com.dudu.entity.DishFlavor;
import com.dudu.service.CategoryService;
import com.dudu.service.DishFlavorService;
import com.dudu.service.DishService;
import com.fasterxml.jackson.databind.util.BeanUtil;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Resource
    private DishService dishService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private DishFlavorService dishFlavorService;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 分页查询
     * @param page 页
     * @param pageSize 一页多少条数据
     * @param name 搜索内容
     * @return
     */
    @GetMapping("/page")
    public Result<Page> showDishInfo(int page,int pageSize,String name) {
        //构造分页构造器对象
        Page<Dish> dishPage = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(dishPage, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> list  = records.stream().map((item) ->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return Result.success(dishDtoPage);
    }

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public Result<String> addDish(@RequestBody DishDto dishDto, HttpSession session) {
        log.info("dish:{}",dishDto);
        dishService.saveWithFlavor(dishDto);
        // 保存图片名称到Redis中(表示上传成功的图片)
        //将上传图片名称存入Redis，基于Redis的Set集合存储
        redisTemplate.opsForSet().add(RedisConstant.SETMEAL_PIC_DB_RESOURCES, dishDto.getImage());

        // 移除Redis中对应修改菜品的缓存数据
        // 拼接Redis的key
        String key =  "dish_"+dishDto.getCategoryId();
        // 根据Key从Redis中移除数据
        redisTemplate.delete(key);

        return Result.success("添加菜品成功");
    }



    /**
     * 根据ID查询菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> getDishById(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return Result.success(dishDto);
    }

    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public Result<String> updateDish(@RequestBody  DishDto dishDto) {
        // 移除Redis中所有数据
        redisTemplate.delete(redisTemplate.keys("dish_*"));
        dishService.updateWithFlavor(dishDto);
        return Result.success("菜品修改成功");
    }

    /**
     * 改变菜品状态(停售,起售)
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> updateState(@PathVariable int status,String ids) {
        if (StringUtils.isEmpty(ids.trim())) {
            return  Result.error("批量操作，请先勾选操作菜品！");
        }
        String[] split = ids.trim().split(",");
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            list.add(Long.parseLong(split[i]));
        }
        // 移除Redis中所有数据
         redisTemplate.delete(redisTemplate.keys("dish_*"));
        // 修改状态
        dishService.updateStatus(list,status);
        return Result.success(status == 0 ? "停售成功":"起售成功");
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> deleteDish(String ids) {
        // 验证ids是否为空
        if (StringUtils.isEmpty(ids.trim())) {
            return  Result.error("批量操作，请先勾选操作菜品！");
        }
        // 将多个ids单独存放到list集合中
        String[] split = ids.trim().split(",");
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            list.add(Long.parseLong(split[i]));
        }
        try {
            // 移除Redis中所有数据
            redisTemplate.delete(redisTemplate.keys("dish_*"));
            dishService.deleteWithFlavor(list);
        } catch (Exception exception) {
            return Result.error("套餐正在售卖中，不能删除");
        }
        return Result.success("菜品删除成功");
    }

    /**
     * 根据菜品分类获取所有菜品
     * @param dish
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "dish_"+"#dish.categoryId")// key= setmealCache::分类Id
    public Result<List<DishDto>> getDishList(Dish dish) {
        // 拼接Redis的Key值
        String key =  "dish_"+dish.getCategoryId();
        List<DishDto> list = null;
        // 通过key从Redis中获取数据
//         list =(List<DishDto>) redisTemplate.opsForValue().get(key);
//        if (list != null) {
//            // Redis中有缓存数据,从Redis中获取数据
//            return Result.success(list);
//        }

        // Redis中没有数据就从数据库中获取
        Dish dishTemp = new Dish();
        dishTemp.setCategoryId(dish.getCategoryId());
        // 查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dishTemp != null, Dish::getCategoryId, dishTemp.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);

        list = dishList.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId_temp = item.getCategoryId();// 获取当前分类Id
            // 根据分类ID获取分类对象
            Category categoryTemp = categoryService.getById(categoryId_temp);
            if (categoryTemp != null) {
                dishDto.setCategoryName(categoryTemp.getName());// 将分类名存入到dishDto中
            }

            // 获取dish_flavor集合
            // 获取当前菜品Id
            Long dishId = item.getId();
            // 根据dishId获取dish_flavor集合
            LambdaQueryWrapper<DishFlavor> queryWrapperDisFlavor = new LambdaQueryWrapper<>();
            queryWrapperDisFlavor.in(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapperDisFlavor);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        // 将数据库中获取到的数据存入到Redis中去，
//        redisTemplate.opsForValue().set(key,list);
        return Result.success(list);
    }
}
