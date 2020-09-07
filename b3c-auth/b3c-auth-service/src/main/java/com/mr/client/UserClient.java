package com.mr.client;

import com.mr.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="user-service")
public interface UserClient extends UserApi {
}
