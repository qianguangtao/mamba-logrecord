package com.app.core.configuration;

import com.app.core.encrypt.EncryptProperties;
import com.app.core.encrypt.processor.field.*;
import com.app.core.mark.AppConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "app.encrypt", name = "fieldEncryptEnabled", havingValue = "true")
public class EncryptFieldAutoConfiguration implements AppConfiguration {

    @Resource
    private EncryptProperties encryptProperties;

    @PostConstruct
    public void init() {
        log.info("配置 EncryptFieldAutoConfiguration");
        if (this.encryptProperties.isFieldEncryptEnabled()) {
            log.info("字段加密已开启，类型：{}", this.encryptProperties.getFieldEncryptType());
        } else {
            log.info("字段加密已关闭");
        }
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.encrypt", name = "fieldEncryptType", havingValue = "aes")
    public FieldEncryptProcessor aesFieldEncryptHandler() {
        return new AesFieldEncryptProcessor(this.encryptProperties.getFieldEncryptKey());
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.encrypt", name = "fieldEncryptType", havingValue = "des")
    public FieldEncryptProcessor desFieldEncryptHandler() {
        return new DesFieldEncryptProcessor(this.encryptProperties.getFieldEncryptKey());
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.encrypt", name = "fieldEncryptType", havingValue = "desede")
    public FieldEncryptProcessor desedeFieldEncryptHandler() {
        return new DESedeFieldEncryptProcessor(this.encryptProperties.getFieldEncryptKey());
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.encrypt", name = "fieldEncryptType", havingValue = "rc4")
    public FieldEncryptProcessor rc4FieldEncryptHandler() {
        return new RC4FieldEncryptProcessor(this.encryptProperties.getFieldEncryptKey());
    }

}
