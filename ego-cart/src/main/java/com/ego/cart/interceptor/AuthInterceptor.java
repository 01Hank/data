package com.ego.cart.interceptor;

import com.ego.auth.entity.UserInfo;
import com.ego.auth.utils.JwtUtils;
import com.ego.cart.config.JwtProperties;
import com.ego.common.utils.CookieUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/21
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */

public class AuthInterceptor implements HandlerInterceptor {

    private JwtProperties jwtProperties;

    private static final ThreadLocal<UserInfo> threadLocal = new ThreadLocal<>();

    public AuthInterceptor(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * 获取当前线程中的用户信息
     * @return
     */
    public static UserInfo getUser() {
        return threadLocal.get();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取用户信息-->如果没有登录-->提示未授权401
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        try
        {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            threadLocal.set(userInfo);
        }
        catch (Exception e)
        {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空当前线程中用户信息
        threadLocal.remove();
    }
}
