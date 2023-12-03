package com.atlisheng.admin.service;

import com.atlisheng.admin.bean.City;
import com.atlisheng.admin.mapper.CityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CityService {
    @Autowired
    CityMapper cityMapper;

    public City getCityById(Long id){
        return cityMapper.getCityById(id);
    }


    public City saveCity(City city){
        cityMapper.insertCity(city);
        return city;
    }
}
