package com.app.core.configuration;

import com.app.core.encrypt.EncryptProperties;
import com.app.core.encrypt.processor.argument.AesPrimaryKeyEncryptProcessor;
import com.app.core.encrypt.processor.argument.PrimaryEncryptProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "app.encrypt", name = "primaryEncryptEnabled", havingValue = "true")
public class EncryptPrimaryAutoConfiguration {

    @Resource
    private EncryptProperties encryptProperties;

    @PostConstruct
    public void init() {
        log.info("配置 EncryptPrimaryAutoConfiguration");
        if (this.encryptProperties.isPrimaryEncryptEnabled()) {
            log.info("主键加密已开启，类型：{}", this.encryptProperties.getFieldEncryptType());
        } else {
            log.info("主键加密已关闭");
        }
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.encrypt", name = "primaryEncryptType", havingValue = "aes")
    public PrimaryEncryptProcessor aesPrimaryEncryptHandler() {
        return new AesPrimaryKeyEncryptProcessor(this.encryptProperties.getPrimaryEncryptKey());
    }

}
