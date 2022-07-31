package com.dudu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dudu.common.Result;
import com.dudu.constant.RedisConstant;
import com.dudu.dto.SetmealDto;
import com.dudu.entity.Category;
import com.dudu.entity.Setmeal;
import com.dudu.service.CategoryService;
import com.dudu.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Resource
    private SetmealService setmealService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 分页查询
     * @param page 页
     * @param pageSize 一页数据条数
     * @param name 搜素内容
     * @return
     */
    @GetMapping("/page")
    public Result<Page> showSetmealInfo(int page,int pageSize,String name) {
        //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        // 查询第一张表
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);

        // 查询第二张表,并初始到dtoPage
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return Result.success(dtoPage);
    }

    /**
     * 添加套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)// 当执行到addSetMeal()方法时,删除key=setmealCache的Redis数据
    public Result<String> addSetMeal(@RequestBody SetmealDto setmealDto) {
        log.info("setmealDto:{}",setmealDto);
        //将上传图片名称存入Redis，基于Redis的Set集合存储
        redisTemplate.opsForSet().add(RedisConstant.SETMEAL_PIC_DB_RESOURCES, setmealDto.getImage());
        setmealService.saveWithSetMealDish(setmealDto);
        return Result.success("添加套餐成功");
    }

    /**
     * 改变状态(停售,起售)
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache",allEntries = true)
    public Result<String> updateStatus(@PathVariable int status,String ids) {
        log.info("status:{},ids:{}",status,ids);
        if (StringUtils.isEmpty(ids.trim())) {
            return  Result.error("批量操作，请先勾选操作菜品！");
        }
        String[] split = ids.split(",");
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            list.add(Long.parseLong(split[i]));
        }
        setmealService.updateStatus(list,status);
        return Result.success(status == 0 ? "停售成功":"起售成功");
    }

    /**
     * 通过Id获取套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Cacheable(key = "setmealCache",value = "#id")// key= setmealCache::id
    public Result<SetmealDto> getSetmealDtoById(@PathVariable Long id) {
        log.info("===============================id:{}",id);
        SetmealDto setmealDto = setmealService.getSetmealDtoById(id);
        log.info("===============================setmealDto:{}",setmealDto);
        return Result.success(setmealDto);
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public Result<String> updateWithSetmealDish(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithSetmealDish(setmealDto);
        return Result.success("套餐修改成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public Result<String> deleteWithSetmealDishByIds(String ids) {
        if (StringUtils.isEmpty(ids.trim())) {
            return  Result.error("批量操作，请先勾选操作菜品！");
        }
        String[] split = ids.trim().split(",");
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            list.add(Long.parseLong(split[i]));
        }
        try {
            setmealService.deleteWithSetmealDishByIds(list);
        } catch (Exception exception) {
            return Result.error(exception.getMessage());
        }
        return Result.success("商品删除成功");
    }

    /**
     * 获取所有套餐信息
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache" ,key="#setmeal.categoryId")// key= setmealCache::分类Id
    public Result<List<Setmeal>> getSetmealDtoList(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() !=null,Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        return Result.success(setmealService.list(queryWrapper));
    }
}
