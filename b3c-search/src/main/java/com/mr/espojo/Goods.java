package com.mr.espojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName Good
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/6
 * @Version V1.0
 **/
 /*
        indexName:用于存储此实体的索引的名称
        type:映射类型(文档);（从版本4.0开始不推荐使用）
        shards:索引的分片数
        replicas:索引的副本数

        用@Field标记的字段会才会储存在es库中标记为文档的字段
        其他字段在增加数据的时候默认增加
        */
@Data
@Document(indexName = "goods", type = "docs", shards = 1, replicas = 0)
public class Goods {
    @Id
    private Long id; // spuId
  //Field作用在成员变量,标记为文档的字段，并指定字段映射属性
    //type：字段类型，是枚举：FieldType
    @Field(type = FieldType.text, analyzer = "ik_max_word")
    private String all; // 所有需要被搜索的信息，包含标题，分类，甚至品牌;3个字段都可以被搜素
    @Field(type = FieldType.keyword, index = false)
    private String subTitle;// 卖点(子标题)
    private Long brandId;// 品牌id
    private Long cid1;// 1级分类id
    private Long cid2;// 2级分类id
    private Long cid3;// 3级分类id
    private Date createTime;// 创建时间
    private List<Long> price;// 价格
    @Field(type = FieldType.keyword, index = false)
    private String skus;// sku信息的json结构 [{},{}]
    private Map<String, Object> specs;// 可搜索的规格参数，key是参数名，值是参数值
}