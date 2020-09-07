package com.mr.service.api;

import com.mr.common.enums.ExceptionEnums;
import com.mr.common.exception.MrException;
import com.mr.common.utils.PageResult;
import com.mr.service.bo.SpuBo;
import com.mr.service.pojo.Category;
import com.mr.service.pojo.Sku;
import com.mr.service.pojo.Spu;
import com.mr.service.pojo.SpuDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName GoodClient
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/6
 * @Version V1.0
 **/
@RequestMapping(value = "category")
public interface CategoryApi {
    //查询
    @GetMapping(value="list")
    public List<Category> queryCategoryList(@RequestParam(value="pid",defaultValue = "0") Long pid);

    //删除
    @DeleteMapping(value="remove")
    public void removeCategory(@RequestParam(value="id") Long id);

    //修改回显2

    @GetMapping(value = "findById")
    public Category findById(@RequestParam("id") Long id);

    //修改
    @PutMapping(value = "save")
    public Category editCategory(@RequestBody Category category);

    //增加
    @PostMapping(value = "save")
    public Category saveCategory(@RequestBody Category category);

    //查询品牌下拥有的分类
    /**
     * 通过品牌id查询分类数据
     * */
    @GetMapping(value="queryCategoryBrand/{bid}")
    public List<Category> queryCategoryBrand(@PathVariable("bid") Long bid);

    //用于es库,根据分类ids,查询多个分类
    @GetMapping(value="queryCategoryListByIds")
    public List<Category> queryCategoryList(@RequestParam("ids") List<Long> ids);
}
