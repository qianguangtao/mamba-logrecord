package com.app.core.mvc.converter;


import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.util.StdConverter;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/15 14:12
 * @description:
 */
public class ThrowableConverter extends StdConverter<Throwable, String> {
    @Override
    public String convert(Throwable throwable) {
        return ObjectUtil.isNotNull(throwable) ? throwable.toString() : null;
    }
}
