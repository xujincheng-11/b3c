package com.mr.service.bo;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName SearchPageBo
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/10
 * @Version V1.0
 **/
@Data
public class SearchPageBo {
    private String key;//搜索条件

    private Integer page=0;//默认第0页

    private Integer size=10;//每页显示10条

    private Map<String,String> filter;//过滤筛选条件
}
