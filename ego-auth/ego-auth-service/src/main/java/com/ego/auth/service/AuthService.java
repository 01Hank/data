package com.ego.auth.service;

import com.ego.auth.client.UserClient;
import com.ego.auth.config.JwtProperties;
import com.ego.auth.entity.UserInfo;
import com.ego.auth.utils.JwtUtils;
import com.ego.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/20
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Service
@Slf4j
@EnableConfigurationProperties({JwtProperties.class})
public class AuthService {
    @Autowired
    private UserClient userClient;


    @Autowired
    private JwtProperties jwtProperties;

    public String login(String username, String password) {
        String result = null;
        //查询用户名密码是否正确(通过user-service)
        User user = userClient.query(username, password).getBody();
        //用户有效,生成token
        if(user !=null)
        {
            try{
                UserInfo userInfo = new UserInfo(user.getId(),user.getUsername());
                result = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            }
            catch (Exception e)
            {
                log.error("生成token异常",e);
                throw new RuntimeException("生成token异常");
            }
        }
        //用户无效,直接报错
        else {
            throw new RuntimeException("用户名密码错误");
        }
        return result;
    }

    public UserInfo verify(String token) {
        UserInfo result = null;
        try {
             result = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        } catch (Exception e) {
            log.error("获取用户信息错误", e);
            throw new RuntimeException("获取用户信息错误", e);
        }
        return result;
    }

    public String refreshToken(UserInfo userInfo) throws Exception {
        return JwtUtils.generateToken(userInfo,jwtProperties.getPrivateKey(),jwtProperties.getExpire());
    }
}
