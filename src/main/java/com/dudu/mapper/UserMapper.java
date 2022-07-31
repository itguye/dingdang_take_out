package com.dudu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dudu.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
