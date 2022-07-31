package com.dudu.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dudu.common.Result;
import com.dudu.entity.Category;
import com.dudu.service.CategoryService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 菜品品类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    /**
     * 添加菜品品类
     * @param category
     * @return
     */
    @PostMapping
    public Result<String> addCategory(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success("新增分类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件，根据sort进行排序
        queryWrapper.orderByAsc(Category::getSort);

        //分页查询
        categoryService.page(pageInfo,queryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * 修改菜品品类
     * @param category
     * @return
     */
    @PutMapping
    public Result<String> updateCategory(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success("修改分类信息成功!");
    }

    /**
     * 删除菜品品类
     * @param id
     * @return
     */
    @DeleteMapping
    public Result<String> delCategoryById(Long id) {
       // categoryService.removeById(id);
        categoryService.remove(id);
        return Result.success("分类信息删除成功");
    }

    /**
     * 获取菜品品类
     * @param category
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> categoryList(Category category) {
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType, category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return Result.success(list);
    }

}
