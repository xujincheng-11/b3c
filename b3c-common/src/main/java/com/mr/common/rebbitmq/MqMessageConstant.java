package com.mr.common.rebbitmq;

/**
 * @ClassName MqMessageConstant
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/24
 * @Version V1.0
 **/
public class MqMessageConstant {
    //spu交换机，routingkey(方法)
    public static  final String SPU_EXCHANGE_NAME="item_spu_exchange";

    //routingkey(方法)
    public static final String SPU_ROUT_KEY_SAVE="spu.save";
    public static final String SPU_ROUT_KEY_UPDATE="spu.update";
    public static final String SPU_ROUT_KEY_DELETE="spu.delete";

    //spu-es的队列
    public static  final String SPU_QUEUE_SEARCH_SAVEORUPDATE="spu_queue_es_saveorudate";
    public static  final String SPU_QUEUE_SEARCH_SAVE="spu_queue_es_save";
    public static  final String SPU_QUEUE_SEARCH_UPDATE="spu_queue_es_update";
    public static  final String SPU_QUEUE_SEARCH_DELETE="spu_queue_es_delete";

    //spu-page的队列
    public static  final String SPU_QUEUE_PAGE_SAVEORUPDATE="spu_queue_page_saveorudate";
    public static  final String SPU_QUEUE_PAGE_SAVE="spu_queue_page_save";
    public static  final String SPU_QUEUE_PAGE_UPDATE="spu_queue_page_update";
    public static  final String SPU_QUEUE_PAGE_DELETE="spu_queue_page_delete";
}
