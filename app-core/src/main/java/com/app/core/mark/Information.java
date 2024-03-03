package com.app.core.mark;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 信息接口，凡实现此接口者必须要提供内部信息供外部使用
 *
 * @author qiangt
 * @since 2023-07-10
 */
public interface Information {

    default String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 对外提供信息
     */
    default String info() {
        return JSON.toJSONString(this, SerializerFeature.PrettyFormat);
    }

}
