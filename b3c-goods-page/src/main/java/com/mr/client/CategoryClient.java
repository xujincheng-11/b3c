package com.mr.client;

import com.mr.service.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName GoodsClient
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/6
 * @Version V1.0
 **/
@FeignClient(value="item-service")
public interface CategoryClient extends CategoryApi {

}