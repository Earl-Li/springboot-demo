package com.atlisheng.admin.service;


import com.atlisheng.admin.bean.Account;
import com.atlisheng.admin.mapper.AccountMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AccountService {
    @Resource
    AccountMapper accountMapper;
    public Account getActById(Long id){
        return accountMapper.getActById(id);
    }
}
