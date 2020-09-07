package com.mr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "b2c.filter")
public class FilterProperties {
    //私有化白名单
    private List<String> allowPaths;
    //get方法
    public List<String> getAllowPaths() {
        return allowPaths;
    }
    //set方法
    public void setAllowPaths(List<String> allowPaths) {
        this.allowPaths = allowPaths;
    }

}