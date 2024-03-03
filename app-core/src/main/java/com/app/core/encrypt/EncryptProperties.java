package com.app.core.encrypt;

import com.app.core.mark.AppProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.encrypt")
@Component
@Setter
@Getter
public class EncryptProperties implements AppProperties {

    /**
     * 是否开启字段加密
     */
    private boolean fieldEncryptEnabled;

    /**
     * 字段加密类型
     */
    private String fieldEncryptType;

    /**
     * 字段加密密钥
     */
    private String fieldEncryptKey;

    /**
     * 是否开启主键加密
     */
    private boolean primaryEncryptEnabled;

    /**
     * 主键加密类型
     */
    private String primaryEncryptType;

    /**
     * 主键加密密钥
     */
    private String primaryEncryptKey;

}
