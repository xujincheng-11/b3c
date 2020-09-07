package com.mr.dao;

import com.mr.espojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @ClassName GoodsRepository
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/6
 * @Version V1.0
 **/

//增删改查类
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
