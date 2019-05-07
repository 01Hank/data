package com.ego.sms.utils;

import com.aliyuncs.exceptions.ClientException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/18
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SmsUtilsTest {
    @Autowired
    private SmsUtils smsUtils;
    @Test
    public void testSendSms() throws ClientException {
        smsUtils.sendSms("18181423086","123456");
    }
}
