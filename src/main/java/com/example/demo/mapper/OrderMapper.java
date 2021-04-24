package com.example.demo.mapper;

import com.example.demo.bean.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    /**
     * 生成订单
     *
     * @param order 订单信息
     * @return
     */
    @Insert("insert into `order` (pid,uid) values (#{pid},#{uid})")
    int insertOrder(Order order);

}
