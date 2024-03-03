package com.app.core.mvc.serialization.desensitization.strategy;

/**
 * 脱敏策略接口
 * @author qiangt
 * @since 2022-12-06
 */
public interface DesensitizationStrategy {

    String doDesensitization(String source);

}
