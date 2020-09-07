package com.mr.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mr.common.rebbitmq.MqMessageConstant;
import com.mr.common.utils.PageResult;
import com.mr.mapper.*;
import com.mr.service.bo.SpuBo;
import com.mr.service.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsService
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/27
 * @Version V1.0
 **/
@Slf4j
@Service
public class GoodsService {

    @Resource
    private SpuMapper spuMapper;
    @Resource
    private BrandMapper brandMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private SpuDetailMapper spuDetailMapper;
    @Resource
    private SkuMapper skuMapper;
    @Resource
    private StockMapper stockMapper;

    @Autowired //rabbitmq提供的类
    private AmqpTemplate amqpTemplate;

    public PageResult<SpuBo> querySpuPage(Integer page, Integer rows, Long saleable, String search) {
        //分页 : 使用的是分页助手启动器 pagehelper ;pom.xml引入的依赖
        //设置当前页和每页条数; limit第一个参数表示从该参数的下一条数据开始，第二个参数表示每次返回的数据条数。
        PageHelper.startPage(page,rows);

        //过滤条件 前台搜索searchKey使用的模糊查询
        //创建过滤条件类
        Example example = new Example(Spu.class);
        //通过过滤条件类,构建实例,
        Example.Criteria criteria =example.createCriteria();

        // 是否过滤上下架(实体字段,参数):controller默认为true,不用加判断
        //andEqualTo:数据库(实体)与输入值(参数)做对比
        if(saleable !=2 && saleable != null){

            //上架:1;下架:0;展示全部:2
            criteria.andEqualTo("saleable", saleable);
        }

        //设置模糊查询;根据title名称,进行模糊查询
        if(StringUtil.isNotEmpty(search)){//判断search搜索不为空
            criteria.andLike("title","%"+search+"%");
        }

        //使用分页助手提供的类接收分页参数和实体参数; Page拦截这个方法,会追加一个count统计数据的sql
        //mapper查询只能返回是Spu,因为查询数据,实体Spu对应数据库
        Page<Spu> pageData = (Page<Spu>) spuMapper.selectByExample(example);
        //返回值加工Spu-->SpuBo 返回的是分页的信息
        List<Spu> spuList = pageData.getResult();
        System.out.println(spuList);

        //拉姆达表达式循环 数据,填充品牌名 分类三级名
        //原来pageData.getResult()的返回值是List<Spu>(分页的信息)类型的数组;
        // 通过stream将数组转成流 循环操作;返回填充信息
        List<SpuBo> spuBoList = spuList.stream().map(spu->{ //spu循环体
            SpuBo spuBo = new SpuBo();
            //通过工具类,复制对象中的属性,第一个是要复制的属性,第二个是目标属性
            BeanUtils.copyProperties(spu,spuBo);

            //填充品牌brand
            spuBo.setBrandName(this.brandMapper.selectByPrimaryKey(spuBo.getBrandId()).getName());

            //填充分类三级名  "/手机/手机通讯/手机"
            //使用mapper批量查询分类category实体的3个id
            List<Category> categorylist=this.categoryMapper.selectByIdList(
                    Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3())
            );
            //循环获取的3个id提取Name列; 通过stream将数组转成流 循环操作;
            List<String> categoryName=categorylist.stream().map(category ->
                category.getName()
            ).collect(Collectors.toList());

            //设置到bo中;将List join  / 链接
            spuBo.setCategoryName(StringUtils.join(categoryName,"/"));
            return spuBo;
        }).collect(Collectors.toList());//最后通过流转成后返回想要的SpuBo泛型数组(List<SpuBo>)

        //spuBoList 最后输出的是填充信息
        System.out.println(spuBoList);
        return new PageResult<SpuBo>(pageData.getTotal(),spuBoList);
    }

    /**
     * 商品增加
     * */
    public void saveGoods(SpuBo spuBo) {
        //除了要对SPU新增以外，还要对SpuDetail、Sku、Stock进行保存
        Date now =new Date();
        Spu spu=new Spu();
        //通过工具类,复制对象中的属性值,第一个是要复制的属性,第二个是目标属性
        BeanUtils.copyProperties(spuBo,spu);
        spu.setValid(true);
        spu.setSaleable(true);
        spu.setCreateTime(now);
        spu.setLastUpdateTime(now);
        //spu保存数据库
        spuMapper.insert(spu);
        //获得SpuDetail的数据保存数据库 根据实体获得对应的数据
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        spuDetailMapper.insert(spuDetail);

        //rabbitmq消息生产者


        //调用增加sku stock 公共类或公共方法
        this.saveSkuAndStock(spuBo.getSkus(),spu.getId());

        //amqpTemplate 发送一个消息 指定：交换机名称， routingkey(方法) 参数
        this.sendMessage(MqMessageConstant.SPU_EXCHANGE_NAME,MqMessageConstant.SPU_ROUT_KEY_SAVE,spu.getId());
        //this.amqpTemplate.convertAndSend(MqMessageConstant.SPU_EXCHANGE_NAME,MqMessageConstant.SPU_ROUT_KEY_SAVE,spu.getId());
        System.out.println("增加");

        //保存Sku集合
      /*  List<Sku> skuList = spuBo.getSkus();
        //循环集合保存数据库sku和Stock
        //因为一个sku对应一个库存,所以要保存库存
        skuList.forEach(sku -> {
            if(sku.getEnable()) {//Enable:是否有效
                //根据实体sku获得对应的数据,保存
                sku.setSpuId(spu.getId());
                sku.setCreateTime(now);
                sku.setLastUpdateTime(now);
                skuMapper.insert(sku);

                //根据实体Stock获得对应的数据
                Stock stock = new Stock();
                stock.setSkuId(sku.getId());
                stock.setStock(sku.getStock());
                stockMapper.insert(stock);
            }
        });*/

    }

    /**
     *查询商品详情detail表信息回显  (修改回显detail表 ++ sku表:第一种方法)
     * */
    public SpuDetail queryDetail(Long spuId) {
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     *查询sku表信息回显 sku集合
     * */
    public List<Sku> querySku(Long spuId) {
        //创建过滤条件类  第-种方法;
        /*Example example = new Example(Sku.class);

        //通过过滤条件类,构建实例,字段和参数相等
        example.createCriteria().andEqualTo("spuId",spuId);
        //设置条件查询 查询sku表
         List<Sku> skuList = skuMapper.selectByExample(example);
         //查询stock表,赋值给sku中的stock
          skuList.forEach(sku -> {
            sku.setStock(stockMapper.selectByPrimaryKey(sku.getId()).getStock());
        });
        return skuList;*/


        //第二种写法
        //用实体类当查询条件
        Sku skuEx = new Sku();
        skuEx.setSpuId(spuId);
        //select是根据实体类有值的字段当做查询条件 skus集合
        List<Sku> skuList = skuMapper.select(skuEx);
        //循环集合 获得集合中的对象
        skuList.forEach(sku->{
            //给每个对象赋值库存值; 根据skuId 查询对应库存
            sku.setStock(this.stockMapper.selectByPrimaryKey(sku.getId()).getStock());
        });
        return skuList;
    }


    /**
     * 修改回显详细数据(第二种写法)
     */
   /* public SpuBo queryDetail(Long spuId) {
        SpuBo spuBo = new SpuBo();
        //查询detail表
        spuBo.setSpuDetail(spuDetailMapper.selectByPrimaryKey(spuId));
        //查询sku表
        Example example = new Example(Sku.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<Sku> skuList =  skuMapper.selectByExample(example);
        //查询stock表,赋值给sku中的stock
        skuList.forEach(sku -> {
            sku.setStock(stockMapper.selectByPrimaryKey(sku.getId()).getStock());
        });
        //然后将整体的Skus赋值
        spuBo.setSkus(skuList);
        return spuBo;
    }*/

    /***
     * 修改数据 //修改第一种写法
     */
    public void updateGoods(SpuBo spuBo) {
        //修改spu 因为是修改，所以创建时间不改给null，为避免冲突，Valid(0已删除，1有效)，Saleable(上下架) 也给null
        Spu spu = new Spu();
        BeanUtils.copyProperties(spuBo,spu);
        spu.setSaleable(null);
        spu.setValid(null);
        spu.setCreateTime(null);
        spu.setLastUpdateTime(new Date());
        spuMapper.updateByPrimaryKeySelective(spu);

        //修改spuDetail详情
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetailMapper.updateByPrimaryKeySelective(spuDetail);

        //删除sku和stock公用方法
        //先删除 库存表(需要spuId) 再删除sku表
        deleteSkuAndStock(spu.getId());

        //增加sku stock 公共类
        this.saveSkuAndStock(spuBo.getSkus(),spuBo.getId());

        //amqpTemplate 发送一个消息 指定：交换机名称， routingkey(方法) 参数
        this.sendMessage(MqMessageConstant.SPU_EXCHANGE_NAME,MqMessageConstant.SPU_ROUT_KEY_UPDATE,spu.getId());
        //this.amqpTemplate.convertAndSend(MqMessageConstant.SPU_EXCHANGE_NAME,MqMessageConstant.SPU_ROUT_KEY_UPDATE,spu.getId());
        System.out.println("修改");
    }
    /**
     * 保存sku和stock公用方法   List<Sku>:相当于new啦一个sku实体类
     * */
    public void saveSkuAndStock(List<Sku> skuList,Long spuId){
        Date now = new Date();

            //循环保存sku
        skuList.forEach(sku -> {
            if (sku.getEnable()) {//Enable:是否有效
                //根据实体sku获得对应的数据,保存
               sku.setSpuId(spuId);
               sku.setCreateTime(now);
               sku.setLastUpdateTime(now);
               skuMapper.insertSelective(sku);

               //保存stock信息
               Stock stock = new Stock();
               stock.setSkuId(sku.getId());
               stock.setStock(sku.getStock());
               stockMapper.insertSelective(stock);
            }
        });
    }

    /**
     * 删除sku和stock公用方法
     * */
    public void deleteSkuAndStock(Long id){
        //修改库存sku和stock表 由于前端数据可能会新增删除数据,避免垃圾数据, 先删除 再增加
        //先删除 库存表(需要spuId) 再删除sku表
        //实体类条件查询 sku(集合)
       Sku skuEx = new Sku();
        skuEx.setSpuId(id);
        List<Sku> skuList = skuMapper.select(skuEx);
        //获取skuId集合
        List<Long> skuIds = skuList.stream().map(sku->{
            return sku.getId();
        }).collect(Collectors.toList());

        //先根据skuIds删除stock
        stockMapper.deleteByIdList(skuIds);
        //再删除sku 根据spuId
        skuMapper.delete(skuEx);
    }
    /***
     * 删除数据
     */
    public void deleteGoods(Long id) {
        //删除spu表
        spuMapper.deleteByPrimaryKey(id);
        //删除spuDetail表
        spuDetailMapper.deleteByPrimaryKey(id);
        //先删除库存表;再删除sku表
        //调用删除sku和stock公用方法
        deleteSkuAndStock(id);

        //amqpTemplate 发送一个消息 指定：交换机名称， routingkey(方法) 参数
        this.sendMessage(MqMessageConstant.SPU_EXCHANGE_NAME,MqMessageConstant.SPU_ROUT_KEY_DELETE,id);
    }

    /***
     *saleable数据商品上架或下架
     */
    public void saleableGoods(Spu spu) {
        if (null != spu){
            if (spu.getSaleable()){//false:0下架，true:1上架 要么上架,要么下架,不能同时上下架
                spu.setSaleable(false);
                //spuMapper.updateByPrimaryKeySelective(spu);
            }else {
                spu.setSaleable(true);
                //spuMapper.updateByPrimaryKeySelective(spu);
            }
            //相同代码提出来
            spuMapper.updateByPrimaryKeySelective(spu);
        }
    }

    //消息队列:amqpTemplate 发送一个消息 指定：交换机名称， routingkey(方法) 参数
    private void sendMessage(String exchangeName, String routIngKey,Object message){
        // 发送消息
        try {
            this.amqpTemplate.convertAndSend(exchangeName,routIngKey, message);
        } catch (Exception e) {
            log.error("{}商品消息发送异常，商品id：{}",exchangeName, routIngKey, message, e);
            System.out.println("错误信息");
        }
    }
    /*
     * 通过商品spuId查询数据(用于前台查询商品详情)
     * */
    public Spu querySpuById(Long id) {
       return this.spuMapper.selectByPrimaryKey(id);
    }

    /**
     *通过skuid查询sku的数据,用于购物车增加sku商品
     * */
    public Sku querySkuById(Long skuId) {
        return  skuMapper.selectByPrimaryKey(skuId);
    }

    /***
     * 修改数据 //修改第二种写法
     */
  /* public void updateGoods(SpuBo spuBo) {
        //修改spu 因为是修改，所以创建时间不改，为避免冲突，Valid，Saleable 也给null
        spuBo.setSaleable(null);
        spuBo.setValid(null);
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        spuMapper.updateByPrimaryKeySelective(spuBo);

        //保存spuDetail详情
        spuBo.getSpuDetail().setSpuId(spuBo.getId());
        spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        //因为sku和stock表是多项的存在条数变更，所以先删后增，确保不出现废数据
        deleteSkuAndStock(spuBo.getId());

        //增加sku和stock表
        saveSkuAndStock(spuBo.getSkus(),spuBo.getId());
    }

    //删除sku和stock公用方法
    public void deleteSkuAndStock(Long id){
        //因为stock表通过id删除需要通过sku表查询id所以要先删除stock表
        Example example = new Example(Sku.class);
        example.createCriteria().andEqualTo("spuId",id);
        List<Sku> skuList = skuMapper.selectByExample(example);
        //获取skuId集合
        List<Long> idList = skuList.stream().map(sku -> sku.getId()).collect(Collectors.toList());
        //删除stock表
        stockMapper.deleteByIdList(idList);
        //删除sku表
        skuMapper.deleteByExample(example);
    }

    //保存sku和stock公用方法
    public void saveSkuAndStock(List<Sku> skuList,Long spuId){
        for(Sku sku : skuList) {
            if (!sku.getEnable()) {
                continue;
            }
            //保存sku
            sku.setSpuId(spuId);
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insertSelective(sku);

            //保存stock信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockMapper.insertSelective(stock);
        }
    }*/

    /**
     * 上下架 ;第二种写法
     * */

  /*public void saleable(Long id) {
        //先去数据库查询;获得上下架 ;如果获取是true ->给false;如果false ->true
        Boolean saleable = spuMapper.selectByPrimaryKey(id).getSaleable();
        Spu spu = new Spu();
        //赋值操作,true:1上架; false:0下架;;根据查询数据库的上下架,取反;
        //如果获取是true ->给false;如果false ->true
        spu.setSaleable(!saleable);
        spu.setId(id);
        spuMapper.updateByPrimaryKeySelective(spu);
    }*/
}
