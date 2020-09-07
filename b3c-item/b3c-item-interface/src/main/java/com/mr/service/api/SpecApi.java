package com.mr.service.api;

import com.mr.common.utils.PageResult;
import com.mr.service.bo.SpuBo;
import com.mr.service.pojo.*;
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
@RequestMapping(value="spec")
public interface SpecApi {
    //规格 查询
    @GetMapping(value = "groups/{cid}")
    public List<SpecGroup> querySpecGroup(@PathVariable("cid") Long cid);

    //规格 增加
    @PostMapping(value="group")
    public void saveSpecGroup(@RequestBody SpecGroup specGroup);
    //规格 修改
    @PutMapping(value="group")
    public void updateSpecGroup(@RequestBody SpecGroup specGroup);

    //规格 删除
    @DeleteMapping
    public void delete(@RequestParam("id") Long id);


    //规格 参数查询  cid
    //参数可通过category分类查询 :用于商品增加:点击分类出现品牌,给品牌赋规格组参数
    //searching:es搜索条件
    //特有规格generic用于前段查询商品详情
    @GetMapping("/params")
    public List<SpecParam> querySpecParam(
            @RequestParam(value="gid", required = false) Long gid,
            @RequestParam(value="cid", required = false) Long cid,
            @RequestParam(value="searching", required = false) Boolean searching,
            @RequestParam(value="generic", required = false) Boolean generic
    );

    //规格 参数增加
    @PostMapping(value="param")
    public void addSpecParam(@RequestBody SpecParam specParam);
    //规格 参数修改
    @PutMapping(value="param")
    public void editSpecParam(@RequestBody SpecParam specParam);

    //规格 参数删除
    @DeleteMapping(value="param/{id}")
    public void remove(@PathVariable("id") Long id);
}
