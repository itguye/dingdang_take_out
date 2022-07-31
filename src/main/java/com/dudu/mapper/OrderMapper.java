package com.dudu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dudu.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
