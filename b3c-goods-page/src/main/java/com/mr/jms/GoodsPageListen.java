package com.mr.jms;

import com.mr.client.GoodsClient;
import com.mr.common.rebbitmq.MqMessageConstant;
import com.mr.common.utils.PageResult;
import com.mr.service.FileStaticService;
import com.mr.service.bo.SpuBo;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName GoodsPageListen
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/21
 * @Version V1.0
 **/
@Component
public class GoodsPageListen {

    @Autowired
    private FileStaticService fileStaticService;

    /**
     *创建 rabbit监听，创建队列,持久化，绑定到交换机，
     * 设置持久化，以及，交换机类型等
     * @param msg
     */
    /*声明这个方法是一个消费者方法，需要指定下面的属性
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
    //新增或修改html页面
    //SPU_QUEUE_PAGE_SAVEORUPDATE: 队列
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqMessageConstant.SPU_QUEUE_PAGE_SAVEORUPDATE, durable = "true"),
            exchange = @Exchange(
                    value = MqMessageConstant.SPU_EXCHANGE_NAME,
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {MqMessageConstant.SPU_ROUT_KEY_SAVE, MqMessageConstant.SPU_ROUT_KEY_UPDATE}))//方法
    //listen这样的方法在一个类中可以写多个，就代表多个消费者
    public void saveOrUpdatePage(Long id){

        if (id == null) {
            return;
        }
        // 创建页面
        try {
            this.fileStaticService.createStaticHtml(id);

            System.out.println("消费者接收到消息：" + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 删除HTML页面
     * */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqMessageConstant.SPU_QUEUE_PAGE_DELETE, durable = "true"),
            exchange = @Exchange(
                    value = MqMessageConstant.SPU_EXCHANGE_NAME,
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = MqMessageConstant.SPU_ROUT_KEY_DELETE))
    public void DeletePage(Long id) {
        if (id == null) {
            return;
        }
        try {
            // 创建页面
            this.fileStaticService.deleteHtml(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
