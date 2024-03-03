package com.app.core.mvc.serialization.desensitization.strategy;

import org.apache.commons.lang3.StringUtils;

/**
 * @author qiangt
 * @since 2022/12/6 19:36
 */
public class DefaultDesensitizationStrategy implements DesensitizationStrategy {

    @Override
    public String doDesensitization(final String source) {
        if (StringUtils.isBlank(source)) {
            return source;
        }
        return source.replaceAll("[\\s\\S]", "*");
    }

}
