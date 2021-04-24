package com.example.demo.mapper;

import com.example.demo.bean.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper {
    /**
     * 根据id查询商品信息
     *
     * @param id 商品id
     * @return
     */
    @Select("select * from product where id = #{id}")
    Product selectById(@Param("id") long id);

    /**
     * 商品减库存
     *
     * @param id 商品id
     * @return
     * 依赖mysql数据库的行锁机制
     */
    @Update("update product set stock = stock - 1 where stock > 0 and id = #{id}")
//    @Update("update product set stock = stock - 1 where  id = #{id}")
    int seckillById(long id);
}

