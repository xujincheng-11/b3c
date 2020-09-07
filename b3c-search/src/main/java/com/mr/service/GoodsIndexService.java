package com.mr.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mr.client.BrandClient;
import com.mr.client.CategoryClient;
import com.mr.client.GoodsClient;
import com.mr.client.SpecClient;
import com.mr.common.utils.JsonUtils;
import com.mr.common.utils.PageResult;
import com.mr.dao.GoodsRepository;
import com.mr.espojo.Goods;
import com.mr.service.bo.SearchPageBo;
import com.mr.service.pojo.*;
import com.mr.utils.HighLightUtil;
import com.mr.utils.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsIndexService
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/6
 * @Version V1.0
 **/
@Service
//商品索引的service
public class GoodsIndexService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryCliente;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecClient specClient;
    @Autowired //增删改查类
    private GoodsRepository goodsRepository;
    @Autowired//用于字段高亮设置
    private ElasticsearchTemplate elasticsearchTemplate;

    //根据spu填充商品Goods(根据spu构建Goods);保存es库,用于条件筛选
    //spu --> sku -->detail -->stock (根据spu表都能找到其他3张表)
    public Goods buildGoodsBySpu(Spu spu){
        Goods goods = new Goods();
        //依次填充实体goods中的字段(就是填充spu与goods重合的属性)
        goods.setId(spu.getId());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setSubTitle(spu.getSubTitle());
        goods.setCreateTime(spu.getCreateTime());
        //填充all
        //获取品牌,根据品牌查询
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        //查询三级分类
        List<Category> categoryList = categoryCliente.queryCategoryList(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
        //通过流转换获取分类名称的list<String>集合
        List<String> categoryName =categoryList.stream().map(category -> {
            return category.getName();
        }).collect(Collectors.toList());

        //toString()将集合转换为string类型的
        //all全查询:根据title,分类,品牌 查询;   填充完毕
       goods.setAll(spu.getTitle()+categoryName.toString()+brand.getName());

       //填充价格
       List<Long> priceList = new ArrayList<>();
        //填充sku(库存) 通过spuId获得sku数据
        List<Sku> skuList = goodsClient.querySku(spu.getId());
        //在商品搜索界面,展示给用户信息:隐藏的id, 图片一张 价格 ,title(标题)
        //从sku中抽取需要的数据,不需要全部
        List<Map> skuMapList = new ArrayList<>();
        //从List<Sku>抽取数据放到List<Map>中
        skuList.forEach(sku -> {
            Map map = new HashMap();
            map.put("id",sku.getId());
            map.put("price",sku.getPrice());
            map.put("title",sku.getTitle());
            //用三目运算判断图片(图片可能有多个,或一个,可能也没有图片),如果不是空,多个图片用","分割;取第一张图片
            map.put("image",StringUtils.isEmpty(sku.getImages())?"":sku.getImages().split(",")[0]);
            //把抽取需要的数据增加到List<Map>中
            skuMapList.add(map);

            //抽取price价格增加到List<Long>集合中
            priceList.add(sku.getPrice());
        });
        //填充完毕 因为填充的是string(skuMapList中数据是json;利用工具类转换)
        goods.setSkus(JsonUtils.serialize(skuMapList));

        //填充价格完毕(填充价格集合)
        goods.setPrice(priceList);


        //将商品规格拼接
        /*spec_param: 分类下的字段,在商品录入的时候,必须参考规格参数录取规格参数值得信息
           规格参数名称是统一的,规格参数的值,属于各个商品的,值不一致
            规格参数的值: 存在detail表中 generic_spec:通用属性; special:特有属性(值是多个)
         */

        //取出规格数据, 根据分类id(cid) 和用于搜索的规格组的字段(searching)true:1可用于搜索 ,false:0 不可用于搜索
        //specParamList只有 ID   name; 没有value值
        List<SpecParam> specParamList = specClient.querySpecParam(null,spu.getCid3(),true,null);

        //取出value规格值  spuDetail中两个字段 :通用属性,特有属性;(用于拼接页面的搜索条件的值)
        SpuDetail spuDetail = goodsClient.queryDetail(spu.getId());
        spuDetail.getGenericSpec();//通用字段，例如,品牌 型号 {"1":"三星（SAMSUNG）","2":"SM-C5000"}
        spuDetail.getSpecialSpec();//特有属性 例如 颜色{"4":["白色","金色","玫瑰金"]}

        //通用属性和特有属性都是json类型的字符串; 不好做匹配,需要工具类把json类型的字符串转为对象k:v
        //通用属性把json类型的字符串转为对象
        //例如(json) {"1":"三星（SAMSUNG）","2":"SM-C5000"}--->(对象)1:"三星（SAMSUNG）",2:"SM-C5000"
        Map<Long,String>  genericSpecMap = JsonUtils.parseMap(spuDetail.getGenericSpec(),Long.class,String.class);
        //因为特有属性的value是个list集合;特殊处理;如:{"4":["白色","金色","玫瑰金"]}
        //例如(json) 颜色{"4":["白色","金色","玫瑰金"]}--->(对象) 4:["白色","金色","玫瑰金"]
        Map<Long,List<String>> specialSpecMap = JsonUtils.nativeRead(spuDetail.getSpecialSpec(),
                new TypeReference<Map<Long, List<String>>>() {
        });

        //因为 goods.setSpecs()赋值的类型是Map<String,Object>,所以需要将拼装的值(goods中的规格)放入map集合中
        Map<String,Object> goodsSpecMap = new HashMap<>();
        //spuDetail中的通用属性,特有属性 有规格参数的id 和value值;;
        //specParamList规格组参数,只有 ID   name; 没有value值
        //specParamList与spuDetail进行拼凑成 name : value
        specParamList.forEach(specParam -> {
            specParam.getName();//规格参数的名称
            //1.如果是通用属性:就赋通用的value值;2.如果是特有属性:就赋特有的value值;            Object value = null;
            Object value=null;
            if(specParam.getGeneric()){//判断是否是通用属性;Generic是布尔类型
                //通过key定位value
                value = genericSpecMap.get(specParam.getId());

                //一般数值类型都是在通用属性中
                //筛选规格参数是否是number类型(数值类型) 如:英寸 (4.5-5.9)
                if(specParam.getNumeric()){
                    //如果是数值类型。在保存值的，加工，将值变成段（0-4.0,4.0-5.0,5.0-5.5,5.5-6.0,6.0-）
                    //"5.2"---》5.0-5.5英寸
                    //数值类型Numeric与分段间隔值Segment是相互关联的
                    //调用buildSegment进行分段间隔值的转换 如:5.2"---》5.0-5.5英寸
                    //toString()把通用属性中的数值类型value变成string如:(5.2英寸),与规格组参数(参数名name)对应 如:(屏幕):(5.0-5.5英寸)
                    value = this.buildSegment(specParam,value.toString());
                }
            }else{//否则是特有属性
                //通过key定位value
                value = specialSpecMap.get(specParam.getId());
            }
            //将拼装的值(goods中的规格)放入map集合中;进行拼凑成 name : value
            goodsSpecMap.put(specParam.getName(),value);
        });

        //因为赋值的类型是Map<String,Object>,所以需要将值放入map集合中
        goods.setSpecs(goodsSpecMap);
        return goods;
    }

    /**
     * 构建段  //"5.2"---》5.0-5.5英寸
     *分段间隔值的转换
     * @param value
     * @param p
     * @return
     */
    private String buildSegment(SpecParam p,String value) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * es 查询商品分页
     * */
    public PageResult<Goods> queryGoodsPage(SearchPageBo searchPageBo) {
        //多个条件查询
        //条件构建类,又叫建造者模式,
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //判断实体(搜索条件)不是空进行搜索条件查询
        if(StringUtils.isNotEmpty(searchPageBo.getKey())){
            //builder.withQuery:设置多次是覆盖关系
            //多条件使用bool查询如下:must可以将多条件进行组合
            builder.withQuery(QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchQuery("all",searchPageBo.getKey()))
            );
        }

       //获得实体字段(过滤搜索条件)因为过滤筛选条件都是k:v结构,所以根据实体的类型接收
        Map<String,String> filterMap =searchPageBo.getFilter();
        //判断实体字段(过滤搜索条件)不是空进行过滤筛选条件查询
        if (filterMap!=null && filterMap.size()!=0){
            //创建bool查询,多条件使用bool查询
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            //遍历map,需要使用map类中keyset()方法获得所有key的set集合
            Set<String> filterSet = filterMap.keySet();
            //通过forEach遍历set集合
            for (String key:filterSet){
                //如果是 分类与品牌查询的话,如:{key:cid3,value:分类},{key:brandId,value:小米}
                // 因为分类和品牌通过id获取的value值,
               // - 商品分类和品牌 这两个要特殊展示，因为 key=cid3 brandId  value:需要将id转为 name输出
                // 普通规格，原样输出key和value就可以
                if (key.equals("cid3") || key.equals("brandId")){
                    //多条件使用bool查询如下:must可以将多条件进行组合
                    //使用map的get(key)方法,通过key获得value
                    boolQueryBuilder.must(
                            QueryBuilders.matchQuery(key,filterMap.get(key)));
                }else {//如果是规格条件查询的话 需要在字段上追加spec.key.keyword,因为在es库中规格组参数都是在specs对象中储存
                    //使用map的get(key)方法,通过key获得value
                    boolQueryBuilder.must(
                            QueryBuilders.matchQuery("specs."+key+".keyword",filterMap.get(key)));
                }
            }
            //在结果上过滤,使用过滤不适用搜索,这样不会影响成绩
            builder.withFilter(boolQueryBuilder);
        }


        //当前页,需要修正(因为分页page是从0页开始的,在前台要从第一页显示,所以page页数 -1)
        int page= searchPageBo.getPage();
        //三目运算判断;如果点击第二页时要-1,显示第1页数据;否则等于0,还是显示第0页的数据
        page = page>0? page-1:0;
        //分页设置;参数一第几页(当前页),参数二每页几条+
        builder.withPageable(PageRequest.of(page,searchPageBo.getSize()));
        //结果过滤;取部分字段;要过滤的字段(包含字段用includes))或者(过滤不要的字段(不包含的字段用excludes);
        // 取部分字段,页面商品展示需要的字段内容
        builder.withSourceFilter(new FetchSourceFilter(new String[]{"id","all","skus"},null));

        //筛选条件
        //填充聚合分类与品牌条件(汇总分类与品牌);筛选条件
        //1.根据 分类 增加聚合分组,根据terms词条分组 ( 聚合aggregations);
        builder.addAggregation(AggregationBuilders.terms("category_group").field("cid3"));
        //1.根据品牌增加聚合分组;根据terms词条分组(1.增加聚合的条件,2.查询结果,3.获得结果,4.循环取值)
        builder.addAggregation(AggregationBuilders.terms("brand_group").field("brandId"));

        // 根据es提供的方法,查询es库数据,返回查询结果
        Page<Goods> goodsPage = goodsRepository.search(builder.build());


        //判断实体(搜索条件)不是空; 设置高亮
        if(StringUtils.isNotEmpty(searchPageBo.getKey())){
            //设置高亮;//1.根据搜索内容高亮;2.//设置高亮前缀;3.//设置高亮后缀
            builder.withHighlightFields(new HighlightBuilder.Field("all").preTags("<font color='red'>").postTags("</font>"));

            //参数1.Spring对ES的进行的封装,提供的公共类; 参数2.查询条件(builder.build());
            //参数3:查询那个索引库(实体);参数4:高亮的字段
            //返回高亮数据的id和高亮数据的类型
            //通过工具类设置HighLightUtils;(因为前段传的json类型{key:value}),所以返回值(Long:string)
           Map<Long,String> highLightMap = HighLightUtil.getHignLigntMap(elasticsearchTemplate,builder.build(),Goods.class,"all");

           //循环查询结果的内容
           goodsPage.getContent().forEach(goods->{
               //先获得循环后的每条数据id,
               // 通过高亮加工数据中all,又把高亮的all赋值给循环后的goods的all,最后前台通过显示高亮标题
               goods.setAll(highLightMap.get(goods.getId()));
           });
        }


        //计算总页数 = 总条数/每页条数 80/10 =8  49.0/10=4.9 (用Math.ceil()向上取整)
            Long total=goodsPage.getTotalElements();
            Long totalPage=(long) Math.ceil(total.doubleValue()/searchPageBo.getSize());

        //构建,筛选条件:分类,品牌,规格组参数(1.增加聚合的条件,2.查询结果,3.获得结果,4.循环取值)
        //2.填充聚合分类与品牌,返回查询分类和品牌结果,进行转换聚合结果
        AggregatedPage<Goods> aggregatedPage=
                (AggregatedPage<Goods>) goodsPage;
        //3..获取分类以别名分组的结果(获取根据词条(terms)分组字段的别名)
        LongTerms cidTerms = (LongTerms) aggregatedPage.getAggregation("category_group");
        //3.1.获取存放分类数据的集合
        List<LongTerms.Bucket> cidBucket = cidTerms.getBuckets();

        //假设热度最高的cid=0  maxDocCount商品数量0;
        //分类下有好多不同品牌的商品,计算那个分类下商品最多,就是热度最高,并记录下分类的id
        final List<Long>  maxDocCid=new ArrayList<>();
        maxDocCid.add(0l);
        final  List<Long>  maxDocCount=new ArrayList<>();
        maxDocCount.add(0l);

        //4.循环分类,取出cid集合;(拉不大表达式 引用外部变量需要是常量)
        List<Long> cidList = cidBucket.stream().map(bucket -> {
            //如果商品数量，小于bucket商品数量
            if(maxDocCount.get(0) < bucket.getDocCount()){
                //把bucket中的商品数量赋值给假设的最大商品maxDocCount
                maxDocCount.set(0,bucket.getDocCount());
                //把热度最高的cid赋值给假设maxDocCid
                maxDocCid.set(0,bucket.getKeyAsNumber().longValue());
            }
            return bucket.getKeyAsNumber().longValue();
        }).collect(Collectors.toList());
        //根据cid集合查询对应的分类数据
        List<Category> categoryList = categoryCliente.queryCategoryList(cidList);

        System.out.println("最终汇总的分类数据："+categoryList.get(0).getName());


        //3.获取品牌以别名分组的结果
        LongTerms brandIdTerms = (LongTerms)aggregatedPage.getAggregation("brand_group");
        //3.1.获取别名分组中存放品牌数据的集合(Bucket),
        List<LongTerms.Bucket> brandIdBucket = brandIdTerms.getBuckets();

        //4.循环品牌,通过每次循环的Id集合,查询对应的品牌数据 (第一种写法:根据主键查询,效率低)
       /* List<Brand> brandList = brandIdBucket.stream().map(bucket -> {
                //根据主键查询,效率低,正规:要brand ids批量查询
            return  brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());*/

        //4.循环品牌,通过每次循环的Id集合,查询对应的品牌数据 (第二种写法:正规:要brand ids批量查询)
        List<Long> brandIdList = brandIdBucket.stream().map(bucket -> {
            //根据主键查询,效率低,正规:要批量查询
            return  bucket.getKeyAsNumber().longValue();
        }).collect(Collectors.toList());
        //根据brandId集合查询对应的分类数据(根据ids集合查询效率高)
        List<Brand> brandList = brandClient.queryBrandIdsList(brandIdList);
        System.out.println("最终汇总的品牌数据"+brandList.size());

        //汇总规格组参数筛选条件;如"屏幕尺寸"：[5-6.6-7]
        //可搜索规格组参数;需要传热度最高的cid(就是说:根据分类查询数据,那个商品数量最多 热度最高,就显示最高的可搜索规格组参数)
        List<Map<String,Object>> specMapList=this.getSpecMapList(maxDocCid.get(0),searchPageBo);
        System.out.println("商品数量最多 热度最高的分类是:"+maxDocCid.get(0));
        // 从返回查询结果的数据中设置总条数，总页数，集合数据(就是查询的当前页数据,),追加商品分类和品牌的集合
        return new SearchResult(goodsPage.getTotalElements(), totalPage,goodsPage.getContent(),categoryList,brandList,specMapList);
    }
    /**
     * 可搜索规格组参数
     * 查询分类下的 可搜索规格组参数  规格组参数的值 他们是k:v结构
     * @return
     */

    public List<Map<String,Object>> getSpecMapList(Long cid,SearchPageBo searchPageBo){
        //获取k:v结构map结合
        List<Map<String,Object>> specMapList=new ArrayList<>();
        //根据cid   查询哪个规格组参数被筛选
        List<SpecParam> specParamList = specClient.querySpecParam(null,cid,true,null);

        //条件构建类,又叫建造者模式,
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

          //判断实体(搜索条件key)不是空;设置查询条件;
       if(StringUtils.isNotEmpty(searchPageBo.getKey())){
            //多条件使用bool查询如下:must可以将多条件进行组合;
            builder.withQuery(QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchQuery("all",searchPageBo.getKey()))
            );
            //分页设置;参数一第几页(当前页),参数二每页几条
            builder.withPageable(PageRequest.of(0,1));
        }

        //循环规格集合，得到规格名称， 根据规格名称做聚合
        //规格：操作系统 CPU品牌 CPU核数 CPU频率 内存  机身存储  主屏幕尺寸（英寸）  前置摄像头  后置摄像头 电池容量（mAh）
        specParamList.forEach(specParam -> {
            //将规格组参数的名称(name)作为别名,获得数据,参数一:别名,参数二;实体字段,在es库中规格组参数都是在specs中储存(不分词)
            builder.addAggregation(AggregationBuilders.terms(specParam.getName())
                    .field("specs."+specParam.getName()+".keyword"));
        });
        //查询得到聚合(分组)
        AggregatedPage<Goods> page= (AggregatedPage<Goods>) goodsRepository.search(builder.build());

        //循环规格组参数名称,得到规格组参数名称聚合的数据
        specParamList.forEach(specParam -> {
            //通过聚合分组,获取以别名分组的结果(规格组参数的值【5-6，6-7】)
           StringTerms specTerm = (StringTerms) page.getAggregation(specParam.getName());
            //获取别名分组中存放数据的集合(Bucket),返回的List<Bucket>集合通过流转换List<String>集合;键值对格式:k:v,获得key,就获得啦values
            List<String> specValueList = specTerm.getBuckets().stream().map(bucket -> {
                return bucket.getKeyAsString();
            }).collect(Collectors.toList());

            //将数据，放入集合(注意:前台是键值对结构(k:v))
            Map<String,Object> specMap = new HashMap<>();
            specMap.put("key",specParam.getName());
            specMap.put("values",specValueList);
            //可能多条数据,所以把map放入到集合中
            specMapList.add(specMap);
        });
        return specMapList;
    }

    public void createIndex(Long id) throws IOException {

        Spu spu = this.goodsClient.querySpuById(id);
        // 构建商品
        Goods goods = this.buildGoodsBySpu(spu);

        // 保存数据到索引库
        this.goodsRepository.save(goods);
    }

    public void deleteIndex(Long id) {
        this.goodsRepository.deleteById(id);
    }
}
