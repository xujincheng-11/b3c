package com.mr.service;

import com.mr.client.BrandClient;
import com.mr.client.CategoryClient;
import com.mr.client.GoodsClient;
import com.mr.client.SpecClient;
import com.mr.service.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ItemService
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/17
 * @Version V1.0
 **/
@Service
//商品详情
public class ItemService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecClient specClient;

    //根据商品详情填充数据
    public Map<String, Object> toGoodInfo(Long spuId) {
        //获取spu数据
        Spu spu = goodsClient.querySpuById(spuId);
        //获取spuDetail详情数据
        SpuDetail spuDetail =goodsClient.queryDetail(spuId);
        //获取skus数据
        List<Sku> skuList = goodsClient.querySku(spuId);
        //获取分类category的数据
        List<Category> categoryList = categoryClient.queryCategoryList(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
        //获取品牌brand数据
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        //通过分类cid获取规格组specGroup数据
        List<SpecGroup> specGroupList = specClient.querySpecGroup(spu.getCid3());

        specGroupList.forEach(specGroup -> {
            //规格组填充规格参数 需要在specGroup中加入specParam集合，实体加忽略注解
            List<SpecParam> specParamList = specClient.querySpecParam(specGroup.getId(),null,null,null);

            specGroup.setSpecParamList(specParamList);
        });

        //根据分类cid获取特有规格参数 false:特有规格,true:普通规格,
        List<SpecParam> specParamList = specClient.querySpecParam(null,spu.getCid3(),null,false);

        //循环specParamList集合,使用map把 id :name 如:(12,"内存") 的返回前台
        Map<Long,String> specParamMap=new HashMap<>();

        specParamList.forEach(specParam -> {
            //通过规格参数的id,获取规格参数的name,在详情页面展示,如:(机身颜色: 黑色,月光白)
            specParamMap.put(specParam.getId(),specParam.getName());
        });


        //组装所有数据,返回页面
        Map<String,Object> map=new HashMap<>();
        //获取spu数据
        map.put("spu",spu);
        //获取spuDetail详情数据
        map.put("spuDetail",spuDetail);
        //获取skus数据
        map.put("skuList",skuList);
        //获取分类category的数据
        map.put("categoryList",categoryList);
        //获取品牌brand数据
        map.put("brand",brand);
        //通过分类cid获取规格组specGroup数据
        map.put("specGroupList",specGroupList);
        //根据分类cid获取特有规格参数
        map.put("specParamMap",specParamMap);
        return map;
    }
}
