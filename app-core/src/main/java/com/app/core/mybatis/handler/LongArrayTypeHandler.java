package com.app.core.mybatis.handler;

import com.alibaba.fastjson.JSON;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 字符串数组类型处理器
 * [1,2,3]json数组和Long[]互转
 * @author ybj
 * @version 1.0
 */
@NoArgsConstructor
@MappedJdbcTypes(JdbcType.JAVA_OBJECT)
@MappedTypes(Long[].class)
public class LongArrayTypeHandler extends BaseTypeHandler<Long[]> {

    @Override
    public void setNonNullParameter(final PreparedStatement preparedStatement, final int i, final Long[] strings, final JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, JSON.toJSONString(strings));
    }

    @Override
    public Long[] getNullableResult(final ResultSet resultSet, final String s) throws SQLException {
        return JSON.parseObject(resultSet.getString(s), Long[].class);
    }

    @Override
    public Long[] getNullableResult(final ResultSet resultSet, final int i) throws SQLException {
        return JSON.parseObject(resultSet.getString(i), Long[].class);
    }

    @Override
    public Long[] getNullableResult(final CallableStatement callableStatement, final int i) throws SQLException {
        return JSON.parseObject(callableStatement.getString(i), Long[].class);
    }

}
