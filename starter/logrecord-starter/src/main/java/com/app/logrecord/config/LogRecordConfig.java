package com.app.logrecord.config;

import com.app.logrecord.translator.BoolTranslator;
import com.app.logrecord.translator.EnumTranslator;
import com.app.logrecord.translator.ListTranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/25 18:42
 * @description: 自定义Translator构造spring bean
 */
@ComponentScan("com.app.logrecord")
@Configuration
public class LogRecordConfig {

    @Bean
    public EnumTranslator enumTranslator() {
        return new EnumTranslator();
    }

    @Bean
    public BoolTranslator boolTranslator() {
        return new BoolTranslator();
    }

    @Bean
    public ListTranslator listTranslator() {
        return new ListTranslator();
    }
}
