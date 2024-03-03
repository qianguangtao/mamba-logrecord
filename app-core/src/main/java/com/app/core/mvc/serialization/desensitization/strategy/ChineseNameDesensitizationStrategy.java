package com.app.core.mvc.serialization.desensitization.strategy;

import cn.hutool.core.util.DesensitizedUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 中文名脱敏策略
 * @author qiangt
 * @since 2022/12/6 19:36
 */
public class ChineseNameDesensitizationStrategy implements DesensitizationStrategy {

    @Override
    public String doDesensitization(final String source) {
        if (StringUtils.isBlank(source)) {
            return source;
        }
        return DesensitizedUtil.desensitized(source, DesensitizedUtil.DesensitizedType.CHINESE_NAME);
    }

}