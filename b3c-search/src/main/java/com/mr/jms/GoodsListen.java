package com.mr.jms;

import com.mr.common.rebbitmq.MqMessageConstant;
import com.mr.dao.GoodsRepository;
import com.mr.service.GoodsIndexService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName GoodsListen
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/21
 * @Version V1.0
 **/
@Component
public class GoodsListen {

    @Autowired
    private GoodsIndexService goodsIndexService;

    /**
     *创建 rabbit监听，创建队列,持久化，绑定到交换机，
     * 设置持久化，以及，交换机类型等
     *
     声明这个方法是一个消费者方法，需要指定下面的属性
    *`bindings`：指定绑定关系，可以有多个。值是`@QueueBinding`的数组
    * `@QueueBinding`包含下面属性:
    *   - `value`：这个消费者关联的队列。值是`@Queue`，代表一个队列
        - `exchange`：队列所绑定的交换机，值是`@Exchange`类型
        - `key`：队列和交换机绑定的`RoutingKey`
    *
    *  `#`：匹配一个或多个词
    *
​        `*`：匹配不多不少恰好1个词
    * */
    //处理save和update的消息
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqMessageConstant.SPU_QUEUE_SEARCH_SAVEORUPDATE, durable = "true"),
            exchange = @Exchange(
                    value = MqMessageConstant.SPU_EXCHANGE_NAME,
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {MqMessageConstant.SPU_ROUT_KEY_SAVE,MqMessageConstant.SPU_ROUT_KEY_UPDATE}))//routingkey(方法)
    //listen这样的方法在一个类中可以写多个，就代表多个消费者
    public void listenCreate(Long id){
        if (id == null) {
            return;
        }
        try {
            // 创建或更新索引
            this.goodsIndexService.createIndex(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理delete的消息
     *
     * @param id
     */
    //PU_ROUT_KEY_DELETE:方法
    //SPU_QUEUE_SEARCH_DELETE: spu-es的delete队列
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqMessageConstant.SPU_QUEUE_SEARCH_DELETE, durable = "true"),
            exchange = @Exchange(
                    value = MqMessageConstant.SPU_EXCHANGE_NAME,
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = MqMessageConstant.SPU_ROUT_KEY_DELETE))
    public void listenDelete(Long id) {
        if (id == null) {
            return;
        }
        // 删除索引
        try {
            this.goodsIndexService.deleteIndex(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
