package com.mr.web;

import com.mr.common.utils.PageResult;
import com.mr.espojo.Goods;
import com.mr.service.GoodsIndexService;
import com.mr.service.bo.SearchPageBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName SearchGoodsController
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/10
 * @Version V1.0
 **/
@RestController
@RequestMapping(value="goods")
public class GoodsController {
    @Autowired
    private GoodsIndexService goodsIndexService;
    /**
     * es前台查询分页请求
     * */
    @PostMapping(value="page")
    public ResponseEntity<PageResult<Goods>> queryGoodsPage(@RequestBody SearchPageBo searchPageBo){
        PageResult<Goods> pageResult= goodsIndexService.queryGoodsPage(searchPageBo);
        if(searchPageBo ==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
       return ResponseEntity.ok(pageResult);
    }
}
