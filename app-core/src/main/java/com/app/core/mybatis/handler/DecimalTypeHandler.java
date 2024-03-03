package com.app.core.mybatis.handler;

import lombok.NoArgsConstructor;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@NoArgsConstructor
@MappedJdbcTypes(JdbcType.DECIMAL)
@MappedTypes(BigDecimal.class)
public class DecimalTypeHandler extends BaseTypeHandler<BigDecimal> {

    @Override
    public void setNonNullParameter(final PreparedStatement preparedStatement, final int i, final BigDecimal bigDecimal, final JdbcType jdbcType) throws SQLException {
        preparedStatement.setBigDecimal(i, bigDecimal);
    }

    @Override
    public BigDecimal getNullableResult(final ResultSet resultSet, final String s) throws SQLException {
        return this.convert(resultSet.getBigDecimal(s));
    }

    @Override
    public BigDecimal getNullableResult(final ResultSet resultSet, final int i) throws SQLException {
        return this.convert(resultSet.getBigDecimal(i));
    }

    @Override
    public BigDecimal getNullableResult(final CallableStatement callableStatement, final int i) throws SQLException {
        return this.convert(callableStatement.getBigDecimal(i));
    }

    private BigDecimal convert(BigDecimal val) {
        if (Objects.nonNull(val)) {
            // 去除小数点
            val = val.multiply(new BigDecimal(1000000));
            // 防止入参是精度缺失
            final String priceStr = val.setScale(6, RoundingMode.HALF_EVEN).toString();
            // 截取小数点之前的值
            final String priceSubStr = priceStr.substring(0, priceStr.indexOf("."));
            // 将金额准换为万元
            return new BigDecimal(priceSubStr).divide(new BigDecimal(1000000), RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

}
