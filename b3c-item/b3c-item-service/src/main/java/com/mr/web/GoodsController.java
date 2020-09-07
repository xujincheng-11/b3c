package com.mr.web;

import com.mr.common.utils.PageResult;
import com.mr.service.GoodsService;
import com.mr.service.bo.SpuBo;
import com.mr.service.pojo.Sku;
import com.mr.service.pojo.Spu;
import com.mr.service.pojo.SpuDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName GoodsController
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/27
 * @Version V1.0
 **/
@RestController
@RequestMapping(value="good")
public class GoodsController {
    @Autowired
    private GoodsService service;
    /**
     * 商品查询
     * 参数:page:页数
     *      rows:每页条数
     *      saleable:是否上下架
     *      search: 搜索
     * */
    @GetMapping(value="page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable") Long saleable,
            @RequestParam(value = "search") String search
    ){
        PageResult<SpuBo> pageResult = service.querySpuPage(page,rows,saleable,search);
        if(pageResult ==  null && pageResult.getItems().size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 商品增加
     * */
    @PostMapping
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo){
        try {
            service.saveGoods(spuBo);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     *查询商品详情detail表信息回显 (修改回显detail表 ++ sku表:第一种方法)
     * */
    @GetMapping(value="/spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> queryDetail(@PathVariable("spuId") Long spuId){
        SpuDetail  spuDetail = service.queryDetail(spuId);
        return ResponseEntity.ok(spuDetail);
    }

    /**
     *查询sku表信息回显 sku集合
     * */
  @GetMapping(value="/skuList/{spuId}")
    public ResponseEntity<List<Sku>> querySku(@PathVariable("spuId") Long spuId){
        return ResponseEntity.ok(service.querySku(spuId));
    }

    /**
     * 修改回显详细数据(第二种写法)
     */
   /* @GetMapping("detail/{spuId}")
    public ResponseEntity<SpuBo> queryDetail(@PathVariable("spuId") Long spuId){
        SpuBo spuBo = service.queryDetail(spuId);
        return ResponseEntity.ok(spuBo);
    }*/
    /***
     * 修改数据
     */
    @PutMapping
    public ResponseEntity updateGoods(@RequestBody SpuBo spuBo){
        try {
            service.updateGoods(spuBo);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /***
     * 删除数据
     */
    @DeleteMapping
    public ResponseEntity deleteGoods(@RequestParam("id") Long id){
            service.deleteGoods(id);
        return ResponseEntity.status(HttpStatus.CREATED).body("成功");
    }
    /***
     *saleable数据商品上架或下架 ;第一种写法
     */
   @PutMapping("saleable")
    public ResponseEntity saleableGoods(@RequestBody Spu spu){
        service.saleableGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).body("成功");
    }

    /**
     * 上下架 ;第二种写法
     * */
   /*@PutMapping("saleable/{id}")
    public ResponseEntity saleable(@PathVariable("id") Long id){
        service.saleable(id);
        return ResponseEntity.ok(null);
    }*/

    /*
     * 通过商品spuId查询数据(用于前台查询商品详情)
     * */
    @GetMapping(value="/querySpuById/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        Spu spu = service.querySpuById(id);
        if(spu ==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(spu);
    }

    /**
     *通过skuid查询sku的数据,用于购物车增加sku商品
     * */
    @GetMapping(value="querySkuById/{id}")
    public  ResponseEntity<Sku> querySkuById(@PathVariable("id") Long skuId){

        return  ResponseEntity.ok(this.service.querySkuById(skuId));
    }
}
