package com.mr.service.bo;

import com.mr.service.pojo.Sku;
import com.mr.service.pojo.Spu;
import com.mr.service.pojo.SpuDetail;
import lombok.Data;

import java.util.List;

/**
 * @ClassName SpuBo
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/28
 * @Version V1.0
 **/
@Data
public class SpuBo extends Spu{
    private String categoryName;// 商品分类名称

    private String brandName;// 品牌名称

    private SpuDetail spuDetail;// 商品详情

    private List<Sku> skus;// sku列表


}
