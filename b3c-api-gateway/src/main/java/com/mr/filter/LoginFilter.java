package com.mr.filter;

import com.mr.common.utils.CookieUtils;
import com.mr.config.FilterProperties;
import com.mr.config.JwtConfig;
import com.mr.util.JwtUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component
//此注解,让JwtConfig和FilterProperties中@ConfigurationProperties 注解的类生效
//@EnableConfigurationProperties相当于把使用@ConfigurationProperties的类进行了一次注入。
@EnableConfigurationProperties({JwtConfig.class,FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Resource
    private JwtConfig jwtConfig;

    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);
    /**
     * `filterType`：返回字符串，代表过滤器的类型。包含以下4种：
     * - 1.`pre`：请求在被路由之前执行;
     * - 2.`routing`：在路由请求时调用;到web-service再返回routing
     * - 4.`post`：在routing和errror过滤器之后调用;在ROUTING返回Response后执行。用来实现对Response结果进行修改，收集统计数据以及把Response传输会客户端。
     * - 3.(4.)`error`：处理请求时发生错误调用
     * */
    //请求鉴权：一般放在pre类型，如果发现没有访问权限，直接就拦截了
    @Override
    public String filterType() {
        //请求在被路由之前执行,拦截
        return "pre";
    }
    //`filterOrder`：通过返回的int值来定义过滤器的执行顺序，数字越小优先级越高
    @Override
    public int filterOrder() {
        return 5;
    }

    @Override //白名单则放过,不进行拦截
    //shouldFilter:返回一个`Boolean`值，判断该过滤器是否需要执行。返回true执行拦截，返回false不执行拦截。
    public boolean shouldFilter() {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest req = ctx.getRequest();
        // 获取请求的url
        String requestURI = req.getRequestURI();
        //当前请求如果不再白名单内则开启拦截器
        //调用isAllowPath判断是否是在白名单内
        return !isAllowPath(requestURI);
    }

    @Resource
    private FilterProperties filterProperties;
    //传入url判断是否是在白名单内
    private boolean isAllowPath(String requestURI) {
        boolean flag = false;
        // 循环白名单判断
        for (String path : this.filterProperties.getAllowPaths()) {
            // 如果存在于白名单，立即返回true，结束循环
            if(requestURI.startsWith(path)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    @Override //主要的处理逻辑的地方，我们做权限控制、日志等都是在这里
    public Object run() throws ZuulException {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = ctx.getRequest();
        System.out.println("拦截到请求"+request.getRequestURI());
        // 获取token
        String token = CookieUtils.getCookieValue(request, jwtConfig.getCookieName());
        System.out.println("token信息"+token);
         // 校验
        try {
            // 通过公钥解密，如果成功，就放行，失败就拦截
            JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
        } catch (Exception e) {
            System.out.println("解析失败 拦截"+token);
            // 校验出现异常，返回403
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(403);
        }
        return null;
    }
}