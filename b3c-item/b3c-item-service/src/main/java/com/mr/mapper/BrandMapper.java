package com.mr.mapper;

import com.mr.service.pojo.Brand;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;

import java.util.List;

@Mapper
public interface BrandMapper extends tk.mybatis.mapper.common.Mapper<Brand>, SelectByIdListMapper<Brand,Long> {
    //新增中间表的方法,没有定义独立的mapper
    // @Insert注解:与tk没关系
    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    public int insertBrandCategory(@Param("bid")Long bid,@Param("cid") Long cid);

    //删除原来的分类
    @Delete("DELETE from tb_category_brand where brand_id = #{bid}")
    public int deleteBrandById(@Param("bid") Long bid);

    //根据 分类 查询 品牌
    @Select("SELECT * from tb_brand where id in(SELECT brand_id from tb_category_brand where category_id=#{cid})")
    List<Brand> selectByKey(Long cid);
}
