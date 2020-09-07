package com.test;

import com.mr.SearchApplication;
import com.mr.client.BrandClient;
import com.mr.client.GoodsClient;
import com.mr.common.utils.PageResult;
import com.mr.dao.GoodsRepository;
import com.mr.espojo.Goods;
import com.mr.service.GoodsIndexService;
import com.mr.service.bo.SpuBo;
import com.mr.service.pojo.Brand;
import com.mr.service.pojo.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName TestClient
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/6
 * @Version V1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class TestClient {
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired//es库提供的方法
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired//注入Goods商品的service
    private GoodsIndexService goodsIndexService;

    @Autowired //自动注入 增删改查
    private GoodsRepository goodsRepository;

    @Test
    public void testGoodsClient(){
        //定义算法分批导入数据
        int page = 1;//从第0页导入数据
        boolean load = true; //判断状态
        int count = 0;//统计总数据
        while (load) {
            /*参数:page:页数
             *      rows:每页条数
             *      saleable:是否上下架(原来布尔类型,根据需求转换为Long类型)
             *      search: 搜索
             * */
            //es商品集合
            List<Goods> goodsList = new ArrayList<>();
            //私有化GoodsClient调用interface中Api中的接口方法,因为GoodsClient接口继承啦GoodsApi接口
            PageResult<SpuBo> pageResult = goodsClient.querySpuByPage(page++, 10, 2l, "");
            pageResult.getItems().forEach(spuBo -> {
                System.out.println(spuBo.getTitle() + "  " + spuBo.getBrandName());
                //因为spuBo实体继承啦spu,完成属性覆盖(加工spu数据);就是把值(数据)赋值给goods实体类字段
                // 把获得数据增加到集合中,然后通过es增加方法保存到es库,
                //调用实体字段赋值的方法
                goodsList.add(goodsIndexService.buildGoodsBySpu(spuBo));
            });
            System.out.println("商品成功");
            //用自动注入的自定义增删改查方法;将集合中的数据保存到es库
            goodsRepository.saveAll(goodsList);

            //统计数据总条数
            count+=goodsList.size();
            //最后一页结束循环
            //根据判断查询的条数大小结束循环;(每页10,到最后一页有可能不够10条,或少于10条)
            if(pageResult.getItems().size()<10){
                    load =false;
            }
            System.out.println("一共增加了"+count+"条");
        }
    }


    @Test //创建商品索引
    public void createGoodIndex(){
        //创建商品索引
        elasticsearchTemplate.createIndex(Goods.class);
        //有索引就修改,没有择创建
        elasticsearchTemplate.putMapping(Goods.class);
        System.out.println("索引创建成功");
    }

    @Test
    public void testGoodsAndBrand(){
        //spu商品分页
        PageResult<SpuBo> pageResult = goodsClient.querySpuByPage(0,10,2l,"华为");
        pageResult.getItems().forEach(spuBo -> {
            System.out.println(spuBo.getTitle()+"  "+spuBo.getBrandName());
        });

        //品牌测试
        /**
         *                   page//分页
         *                  row//一页显示多少条
         *                  searchKey//搜索条件
         *                  sortBy//根据谁降序或升序
         *                  desc//降序或升序
         *                     */
        PageResult<Brand> brandPageResult = brandClient.queryBrandPage(0,10,"米",null,null);
        brandPageResult.getItems().forEach(brand -> {

            System.out.println("品牌成功"+"品牌名称"+brand.getName());
        });
    }

    @Test
    public void aaa() {//数组倒序输出
        int num[] = {1, 4, 6, 78, 23, 9};
        String[] str = {"a", "b", "c", "d"};
        for (int i = num.length - 1; i >= 0; i--) {//倒序bai输du出zhidao
            System.out.print(num[i] + "\t");
        }
        for (int i = str.length - 1; i >= 0; i--) {
            System.out.print(str[i] + "\t");
        }
    }
    @Test
    //把一段逗号分割的字符串转换成数组
    public void ttt(){
        String orgStr="a,b,c,d,e,f";
        String [] result = orgStr.split(",");

        for(int a = 0;a<result.length;a++){
            System.out.print(result[a]+"\t");
        }
    }

}
