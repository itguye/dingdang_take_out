package com.dudu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dudu.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
