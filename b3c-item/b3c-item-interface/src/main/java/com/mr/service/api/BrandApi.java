package com.mr.service.api;

import com.mr.common.utils.PageResult;
import com.mr.service.bo.SpuBo;
import com.mr.service.pojo.Brand;
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
@RequestMapping(value="brand")
public interface BrandApi {
    //查询
    /**
     * 前台传回的参数
     *                   必传   page:this.options.page,//分页
     *                   必传   row:this.options.rowsPerPage,//一页显示多少条
     *                     searchKey:this.search,//搜索条件
     *                     sortBy:this.options.sortBy,//根据谁降序或升序
     *                     desc:this.options.descending//降序或升序
     *
     *                     defaultValue 默认填写的值
     *                     required :必须填写的属性,默认不写, 不默认时 required = false
     *                     */
    @GetMapping(value="page")
    public PageResult queryBrandPage(
            @RequestParam(value="page",defaultValue = "1") Integer page,
            @RequestParam(value="row",defaultValue = "5") Integer row,
            @RequestParam(value="searchKey",required = false) String searchKey,
            @RequestParam(value="sortBy",required = false) String sortBy,
            @RequestParam(value="desc",required = false) Boolean desc
    );


    //增加
    @PostMapping
    public void addBrand(Brand brand, @RequestParam("cids") List<Long> cids);

    //根据id修改
    @PutMapping
    public void editBrand(Brand brand, @RequestParam("cids") List<Long> cids);

    //删除
    @DeleteMapping(value = "delete")
    public void deleteBrand(@RequestParam(value = "id") Long id);

    //根据 分类 查询 品牌
    @GetMapping(value="cid/{cid}")
    public List<Brand> queryCategoryBrand(@PathVariable("cid") Long cid);

    //用于es库,根据品牌id查询品牌
    @GetMapping(value="queryBrandById")
    public Brand queryBrandById(@RequestParam("id") Long id);

    /**
     * 用于es库,根据品牌ids,查询多个品牌数据(根据ids查询)
     */
    @GetMapping(value="queryBrandIdsList")
    public List<Brand> queryBrandIdsList(@RequestParam("ids") List<Long> ids);
}
