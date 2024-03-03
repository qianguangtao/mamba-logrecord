package com.app.core.mybatis;

import com.app.core.mvc.serialization.EnumDefinition;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/17 16:39
 * @description: mybatis自定义的枚举TypeHandler
 * 注意：枚举是单独使用mybatis plus的配置mybatis-plus.configuration.default-enum-type-handler，
 * 不用放入package com.app.core.mybatis.handler，放入启动报错
 */
public class CustomizeEnumTypeHandler<E extends Enum<E> & EnumDefinition> extends BaseTypeHandler<E> {

    private final Class<E> type;

    public CustomizeEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, E e, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, e.getCode());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object s = rs.getObject(columnName);
        return s == null ? null : this.parse(type, s);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object s = rs.getObject(columnIndex);
        return s == null ? null : this.parse(type, s);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object s = cs.getObject(columnIndex);
        return s == null ? null : this.parse(type, s);
    }

    /**
     * 将数据库中查询出的值解析为枚举
     * @param type  枚举类型
     * @param value 数据库中的值
     * @return 转化后的枚举对象
     */
    private E parse(Class<E> type, Object value) {
        try {
            Field field = type.getDeclaredField(EnumDefinition.DB_FIELD);
            field.setAccessible(true);
            for (E e : type.getEnumConstants()) {
                Object fieldValue = field.get(e);
                if (Objects.equals(fieldValue.toString(), this.parseDataType(value))) {
                    return e;
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return null;
    }

    private String parseDataType(Object value) {
        if (value instanceof Boolean) {
            return String.valueOf((Boolean) value ? 1 : 0);
        } else {
            return value.toString();
        }
    }
}
