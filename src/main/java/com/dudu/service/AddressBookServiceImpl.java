package com.dudu.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dudu.entity.AddressBook;
import com.dudu.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>  implements AddressBookService{
}
