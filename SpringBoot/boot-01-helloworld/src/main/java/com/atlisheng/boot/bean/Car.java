package com.atlisheng.boot.bean;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ToString
@Data
//@Component//只有纳入IoC容器管理的组件才能享受类似配置绑定等其他spring强大功能
//@ConfigurationProperties(prefix = "mycar")
@ConfigurationProperties("mycar")
//1.在application.properties文件中完成属性配置，在目标类上使用@ConfigurationProperties(prefix = "mycar")注解通知springboot
// 自动根据前缀mycar拼接属性名查找application.properties对应key如mycar.brand和mycar.price,并将值通过set注入注入属性值
public class Car {
    private String brand;
    private Integer price;

//    public String getBrand() {
//        return brand;
//    }
//
//    public void setBrand(String brand) {
//        this.brand = brand;
//    }
//
//    public Integer getPrice() {
//        return price;
//    }
//
//    public void setPrice(Integer price) {
//        this.price = price;
//    }
//
//    @Override
//    public String toString() {
//        return "Car{" +
//                "brand='" + brand + '\'' +
//                ", price=" + price +
//                '}';
//    }
}
