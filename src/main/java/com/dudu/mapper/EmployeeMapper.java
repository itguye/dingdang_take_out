package com.dudu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dudu.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
