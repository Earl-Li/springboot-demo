package com.atlisheng.admin.mapper;

import com.atlisheng.admin.bean.Account;
import org.apache.ibatis.annotations.Mapper;

//@Mapper
public interface AccountMapper {
    public Account getActById(Long id);
}
