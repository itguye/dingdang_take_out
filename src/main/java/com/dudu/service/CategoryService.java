package com.dudu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dudu.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
