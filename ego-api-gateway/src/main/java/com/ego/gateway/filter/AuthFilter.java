package com.ego.gateway.filter;

import com.ego.auth.utils.JwtUtils;
import com.ego.common.utils.CookieUtils;
import com.ego.gateway.config.FilterProperties;
import com.ego.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/20
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Component
@EnableConfigurationProperties({JwtProperties.class,FilterProperties.class})
public class AuthFilter extends ZuulFilter {


    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        //判断当前请求是否在白名单当中，如果是-->直接放行(返回false)
        //api/auth/login
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String requestURI = request.getRequestURI();
        boolean b = filterProperties.getAllowPaths().stream().anyMatch(uri -> requestURI.startsWith(uri));
        return !b;
    }

    @Override
    public Object run() throws ZuulException {
        //验证token是否有效
        //获取cookie中的EGO_TOKEN
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());

        try {
            JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        } catch (Exception e) {
            //说明token无效-->提示没有权限
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
        }
        return null;
    }
}
