package com.mr.web;

import com.mr.common.enums.ExceptionEnums;
import com.mr.common.exception.MrException;
import com.mr.common.utils.PageResult;
import com.mr.service.BrandService;
import com.mr.service.pojo.Brand;
import com.mr.service.pojo.Category;
import com.mr.service.pojo.SpecParam;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName BrandController
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/17
 * @Version V1.0
 **/
@RestController
@RequestMapping(value="brand")
public class BrandController {
    @Autowired
    private BrandService service;

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
    @ApiOperation(value = "分页查询当前品牌信息", notes = "分页查询当前品牌信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", defaultValue = "1", type = "Integer"),
            @ApiImplicitParam(name = "row", value = "每页大小", defaultValue = "5", type = "Integer"),
            @ApiImplicitParam(name = "searchKey", value = "搜索", type = "String"),
            @ApiImplicitParam(name = "sortBy", value = "首字母排序", type = "String"),
            @ApiImplicitParam(name = "desc", value = "降序", type = "Boolean"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "品牌的分页结果"),
            @ApiResponse(code = 404, message = "没有查询到结果"),
            @ApiResponse(code = 500, message = "查询失败"),
    })
    public ResponseEntity<PageResult> queryBrandPage(
            @RequestParam(value="page",defaultValue = "1") Integer page,
            @RequestParam(value="row",defaultValue = "5") Integer row,
            @RequestParam(value="searchKey",required = false) String searchKey,
            @RequestParam(value="sortBy",required = false) String sortBy,
            @RequestParam(value="desc",required = false) Boolean desc
    ){
        //请求service返回实体和前台传回的参数,以及分页数据
        PageResult<Brand> pageResult = service.queryByList(page,row,searchKey,sortBy,desc);
        if(pageResult == null || pageResult.getItems().size() == 0){
            //如果没有数据 执行异常代码
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(pageResult);
    }


    //增加
    @PostMapping
    @ApiOperation(value = "增加品牌接口", notes = "增加品牌")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "brand", required = false, value = "填写品牌信息"),
            @ApiImplicitParam(name = "cids", required = true, value = "填写分类中品牌信息")
    })
    public ResponseEntity<Void> addBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        System.out.println(brand);
        System.out.println(cids);
        service.addBrand(brand,cids);
        return ResponseEntity.ok(null);

    }

    //根据id修改
    @PutMapping
    @ApiOperation(value = "更新品牌接口", notes = "更新品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "修改品牌信息"),
            @ApiImplicitParam(name = "cids", required = true, value = "修改分类中品牌信息")
    })
    public ResponseEntity<Void> editBrand(Brand brand, @RequestParam("cids") List<Long> cids){
        service.addBrand(brand,cids);
        return ResponseEntity.ok(null);
    }

    //删除
    @ApiOperation(value = "删除品牌接口", notes = "删除品牌")
    @ApiImplicitParam(name = "id", required = true, value = "删除品牌信息")
    @DeleteMapping(value = "delete")
    public ResponseEntity deleteBrand(@RequestParam(value = "id") Long id){
        return this.service.findById(id);
    }

    //根据 分类 查询 品牌(用于spu商品修改回显)
    @GetMapping(value="cid/{cid}")
    @ApiOperation(value = "通过分类查询品牌接口", notes = "查询品牌")
    @ApiImplicitParam(name = "cid", required = true, value = "填写分类cid信息")
    public ResponseEntity<List<Brand>> queryCategoryBrand(@PathVariable("cid") Long cid){
            List<Brand> list = service.queryCategoryBrand(cid);
        return ResponseEntity.ok(list);
    }

    //用于es库,根据品牌id查询品牌
    @GetMapping(value="queryBrandById")
    @ApiOperation(value = "通过查询品牌接口", notes = "查询品牌")
    @ApiImplicitParam(name = "id", required = true, value = "根据品牌id查询品牌")
    public ResponseEntity<Brand> queryBrandById(@RequestParam("id") Long id){
        Brand brand = service.queryBrandById(id);
        return ResponseEntity.ok(brand);
    }

    /**
     * 用于es库,根据品牌ids,查询多个品牌数据(根据ids查询)
     */
    @GetMapping(value="queryBrandIdsList")
    @ApiOperation(value = "通过查询多个品牌接口", notes = "查询品牌")
    @ApiImplicitParam(name = "ids", required = true, value = "根据ids查询品牌")
    public ResponseEntity<List<Brand>> queryBrandIdsList(@RequestParam("ids") List<Long> ids){
        if (ids == null || ids.equals("")) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(service.queryBrandIdsList(ids));
    }
}
