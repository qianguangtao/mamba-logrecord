package com.app.dictionary;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/16 15:29
 * @description: 字典配置类
 */
@ComponentScan("com.app.dictionary")
@MapperScan(basePackages = "com.app.**.mapper")
@Configuration
public class DictionaryAutoConfiguration {

}

