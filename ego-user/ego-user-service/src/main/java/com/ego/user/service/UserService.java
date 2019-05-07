package com.ego.user.service;

import com.ego.common.utils.CodecUtils;
import com.ego.user.mapper.UserMapper;
import com.ego.user.pojo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/18
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;

    private static  final String codeKey = "ego:sms:code:";

    public Boolean check(String data, Integer type) {
        User user = new User();
        //如果type=1,查询用户名是否存在
        if(type.equals(1))
        {
            user.setUsername(data);
        }
        else if(type.equals(2))
        {
            user.setPhone(data);
        }
        else
        {
            throw new RuntimeException("类型不匹配");
        }
        //如果type=2,查询手机是否存在
        return userMapper.selectCount(user)==0;
    }

    public void sendCode(String phone) {

        //发送验证码(生成随机验证码)
        String code = generateCode(6);

        //将验证码存储到redis(5分钟有效)  key:ego.sms.code.phone  value:code
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        stringValueOperations.set(codeKey + phone, code,5, TimeUnit.MINUTES);

        //通过mq发送消息
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("code", code);
        amqpTemplate.convertAndSend("ego.exchange.sms","sms.send",map);
    }

    /**
     * 生成指定位数的随机数字
     * @param len 随机数的位数
     * @return 生成的随机数
     */
    public static String generateCode(int len){
        len = Math.min(len, 8);
        int min = Double.valueOf(Math.pow(10, len - 1)).intValue();
        int num = new Random().nextInt(
                Double.valueOf(Math.pow(10, len + 1)).intValue() - 1) + min;
        return String.valueOf(num).substring(0,len);
    }



    public void register(User user, String code) {
        //校验验证码是否正确
            //从redis中获取验证码
        String redisCode = stringRedisTemplate.opsForValue().get(codeKey + user.getPhone());
        if(StringUtils.isBlank(redisCode))
        {
            throw new RuntimeException("验证码无效");
        }
        if(redisCode.equals(code))
        {
            //对密码加密
            String password = CodecUtils.passwordBcryptEncode(user.getUsername(), user.getPassword());
            user.setPassword(password);
            user.setCreated(new Date());

            //保存信息到数据库
            userMapper.insertSelective(user);
            //删除验证码
            stringRedisTemplate.delete(codeKey + user.getPhone());
        }
        else
        {
            throw new RuntimeException("验证码无效");
        }

    }

    public User findByUP(String username, String password) {


        //在到数据库中查询
        User user = new User();
        user.setUsername(username);
        User result = userMapper.selectOne(user);

        //判断密码是否正确
        Boolean confirm = CodecUtils.passwordConfirm(username + password, result.getPassword());
        if(!confirm)
        {
            return null;
        }
        return  result;
    }
}
