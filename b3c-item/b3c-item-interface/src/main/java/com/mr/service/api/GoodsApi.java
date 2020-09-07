package com.mr.service.api;

import com.mr.common.utils.PageResult;
import com.mr.service.bo.SpuBo;
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
@RequestMapping(value = "good")
public interface GoodsApi {
    /**
     * 商品查询
     * 参数:page:页数
     * rows:每页条数
     * saleable:是否上下架
     * search: 搜索
     */
    @GetMapping(value = "page")
    public PageResult<SpuBo> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable") Long saleable,
            @RequestParam(value = "search") String search
    );

    /**
     * 商品增加
     */
    @PostMapping
    public void saveGoods(@RequestBody SpuBo spuBo);

    /**
     * 查询商品详情detail表信息回显 (修改回显detail表 ++ sku表:第一种方法)
     */
    @GetMapping(value = "/spu/detail/{spuId}")
    public SpuDetail queryDetail(@PathVariable("spuId") Long spuId);

    /**
     * 查询sku表信息回显 sku集合
     */
    @GetMapping(value = "/skuList/{spuId}")
    public List<Sku> querySku(@PathVariable("spuId") Long spuId);


    @PutMapping
    public void updateGoods(@RequestBody SpuBo spuBo);

    /***
     * 删除数据
     */
    @DeleteMapping
    public void deleteGoods(@RequestParam("id") Long id);

    /***
     *saleable数据商品上架或下架 ;第一种写法
     */
    @PutMapping("saleable")
    public void saleableGoods(@RequestBody Spu spu);

    /*
     * 通过商品spuId查询数据(用于前台查询商品详情)
     * */
    @GetMapping(value = "/querySpuById/{id}")
    public Spu querySpuById(@PathVariable("id") Long id);

    /**
     * 通过skuid查询sku的数据,用于购物车增加sku商品
     */
    @GetMapping(value = "querySkuById/{id}")
    public Sku querySkuById(@PathVariable("id") Long skuId);
}