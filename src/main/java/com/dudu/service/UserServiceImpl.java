package com.dudu.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dudu.entity.User;
import com.dudu.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
}
