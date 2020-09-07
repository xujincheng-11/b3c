package com.mr.mapper;

import com.mr.service.pojo.Category;
import com.mr.service.pojo.Spu;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;

@Mapper
public interface SpuMapper extends tk.mybatis.mapper.common.Mapper<Spu>{
}
