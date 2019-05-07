package com.ego.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/18
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Data
@ConfigurationProperties(prefix = "ego.sms")
public class SmsProperties {
    private String accessKeyId;
    private String accessKeySecret;
}
