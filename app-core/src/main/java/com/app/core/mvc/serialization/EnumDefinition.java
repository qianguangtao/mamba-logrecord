package com.app.core.mvc.serialization;

import com.app.core.mvc.converter.EnumDeserializer;
import com.app.core.mvc.converter.EnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/17 16:39
 * @description: 枚举定义接口，用于枚举<->数据库字段，枚举<->前端页面序列化/反序列化
 */
@JsonSerialize(using = EnumSerializer.class)
@JsonDeserialize(using = EnumDeserializer.class)
public interface EnumDefinition {
    /**
     * 常量，数据库字段对应枚举类的属性名
     */
    String DB_FIELD = "code";

    /**
     * @return 枚举默认的name
     */
    String name();

    /**
     * @return 枚举在数据库存的值（根据系统设计，有可能是0,1,2的code，有可能直接用枚举name）
     */
    String getCode();

    /**
     * @return 枚举的备注，用户返回前端展示
     */
    String getComment();

}
