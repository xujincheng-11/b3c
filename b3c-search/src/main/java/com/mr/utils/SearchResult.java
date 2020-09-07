package com.mr.utils;

import com.mr.common.utils.PageResult;
import com.mr.espojo.Goods;
import com.mr.service.pojo.Brand;
import com.mr.service.pojo.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @ClassName SearchResult
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/12
 * @Version V1.0
 **/
@Data
@NoArgsConstructor
public class SearchResult extends PageResult<Goods> {

    private List<Category> categoryList;

    private List<Brand> brandList;

    List<Map<String,Object>> specMapList;

    public SearchResult(Long total, Long totalPage, List<Goods> items, List<Category> categoryList, List<Brand> brandList,List<Map<String,Object>> specMapList) {
        super(total, totalPage, items);
        this.categoryList = categoryList;
        this.brandList = brandList;
        this.specMapList = specMapList;
    }
}
