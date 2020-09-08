package com.mr.web;

import com.mr.service.SpecGroupService;
import com.mr.service.SpecParamService;
import com.mr.service.pojo.SpecGroup;
import com.mr.service.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName SpecGroup
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/24
 * @Version V1.0
 **/
@RestController
@RequestMapping(value="spec")
public class SpecController {
    @Autowired
    private SpecGroupService specGroupService;
    @Autowired
    private SpecParamService specParamService;

    //规格 查询
    @GetMapping(value = "groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroup(@PathVariable("cid") Long cid){
            //返回分类下的规格
        List<SpecGroup> list = specGroupService.querySpecGroup(cid);
        if(list == null || list.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);

    }

    //规格 增加
    @PostMapping(value="group")
    public ResponseEntity saveSpecGroup(@RequestBody SpecGroup specGroup){

        return specGroupService.saveOrUpdateSpecGroup(specGroup);
    }
    //规格 修改(根据实体修改)
    @PutMapping(value="group")
    public ResponseEntity updateSpecGroup(@RequestBody SpecGroup specGroup){

        return specGroupService.saveOrUpdateSpecGroup(specGroup);
    }

    //规格 删除
    @DeleteMapping
    public ResponseEntity delete(@RequestParam("id") Long id){
        return this.specGroupService.findById(id);
    }


    //规格 参数查询 gid,通过规格获取规格的参数
    //参数cid可通过category分类查询 :用于商品增加:点击分类出现品牌,给品牌赋规格组参数
    //searching:es搜索条件
    //特有规格generic用于前段查询商品详情
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParam(
            @RequestParam(value="gid", required = false) Long gid,
            @RequestParam(value="cid", required = false) Long cid,
            @RequestParam(value="searching", required = false) Boolean searching,
            @RequestParam(value="generic", required = false) Boolean generic
    ){
        List<SpecParam> list = this.specParamService.querySpecParams(gid,cid,searching,generic);
        if(list == null || list.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    //规格 参数增加
    @PostMapping(value="param")
    public ResponseEntity addSpecParam(@RequestBody SpecParam specParam){

        return specParamService.saveOrUpdateSpecParam(specParam);
    }
    //规格 参数修改
    @PutMapping(value="param")
    public ResponseEntity editSpecParam(@RequestBody SpecParam specParam){

        return specParamService.saveOrUpdateSpecParam(specParam);
    }

    //规格 参数删除
    @DeleteMapping(value="param/{id}")
    public ResponseEntity remove(@PathVariable("id") Long id){
        return this.specParamService.findById(id);
    }

    @PostMapping("addCollect/{skuId}")
    public ResponseEntity<Void> addCollect(@PathVariable("skuId") Long skuId){

        specGroupService.addCollect(skuId);
        return ResponseEntity.ok(null);
    }

}
