package com.app.core.mybatis.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/1/8 12:36
 * @description: 逗号拼接字符串转集合
 * "a,b,c"和List互转
 */
public class StringListTypeHandler extends BaseTypeHandler<List<String>> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<String> strings, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, CollectionUtil.isNotEmpty(strings) ? String.join(",", strings) : null);
    }

    @Override
    public List<String> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String str = resultSet.getString(s);
        return StrUtil.isNotBlank(str) && str.indexOf(",") > -1 ? new ArrayList<>(Arrays.asList(str.split(","))) : new ArrayList<>();
    }

    @Override
    public List<String> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String str = resultSet.getString(i);
        return StrUtil.isNotBlank(str) && str.indexOf(",") > -1 ? new ArrayList<>(Arrays.asList(str.split(","))) : new ArrayList<>();
    }

    @Override
    public List<String> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String str = callableStatement.getString(i);
        return StrUtil.isNotBlank(str) && str.indexOf(",") > -1 ? new ArrayList<>(Arrays.asList(str.split(","))) : new ArrayList<>();
    }
}
