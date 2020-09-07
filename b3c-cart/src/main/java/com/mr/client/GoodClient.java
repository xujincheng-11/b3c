package com.mr.client;

import com.mr.service.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName GoodClient
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/29
 * @Version V1.0
 **/
@FeignClient(name="item-service")
public interface GoodClient extends GoodsApi {
}
