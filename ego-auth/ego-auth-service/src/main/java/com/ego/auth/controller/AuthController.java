package com.ego.auth.controller;

import com.ego.auth.entity.UserInfo;
import com.ego.auth.service.AuthService;
import com.ego.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/20
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    @Value(value = "${ego.jwt.cookieName}")
    private String cookieName;
    @Value(value = "${ego.jwt.cookieMaxage}")
    private Integer cookieMaxage;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam("username")String username, @RequestParam("password")String password, HttpServletRequest request, HttpServletResponse response)
    {
        try{
            //生成token
            String token = authService.login(username, password);
            //将token写入cookie
            CookieUtils.setCookie(request,response,cookieName,token,cookieMaxage);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("EGO_TOKEN")String token,HttpServletRequest request,HttpServletResponse response)
    {
        try
        {
            UserInfo userInfo = authService.verify(token);

            //刷新token
            String newToken = authService.refreshToken(userInfo);

            //将token写入cookie
            CookieUtils.setCookie(request,response,cookieName,newToken,cookieMaxage);
            if(userInfo==null)
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(userInfo);
        }catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
