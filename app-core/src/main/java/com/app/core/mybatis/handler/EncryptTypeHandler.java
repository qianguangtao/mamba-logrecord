package com.app.core.mybatis.handler;

import com.app.core.encrypt.Encrypt;
import com.app.core.encrypt.processor.field.FieldEncryptProcessor;
import com.app.kit.SpringKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(Encrypt.class)
public class EncryptTypeHandler extends BaseTypeHandler<Encrypt> {

    private FieldEncryptProcessor fieldEncryptProcessor;

    @Override
    public void setNonNullParameter(final PreparedStatement ps, final int i, final Encrypt parameter, final JdbcType jdbcType) throws SQLException {
        ps.setString(i, this.encrypt(parameter));
    }

    @Override
    public Encrypt getNullableResult(final ResultSet rs, final String columnName) throws SQLException {
        return this.decrypt(rs.getString(columnName));
    }

    @Override
    public Encrypt getNullableResult(final ResultSet rs, final int columnIndex) throws SQLException {
        return this.decrypt(rs.getString(columnIndex));
    }

    @Override
    public Encrypt getNullableResult(final CallableStatement cs, final int columnIndex) throws SQLException {
        return this.decrypt(cs.getString(columnIndex));
    }

    public Encrypt decrypt(final String value) {
        if (null == value) {
            return null;
        }
        return this.doDecrypt(value);
    }

    protected Encrypt doDecrypt(final String value) {
        return this.getSecurityHandler().decode(value);
    }

    public String encrypt(final Encrypt encrypt) {
        if (encrypt == null || StringUtils.isEmpty(encrypt.getValue())) {
            return null;
        }
        return this.doEncrypt(encrypt);
    }

    protected String doEncrypt(final Encrypt encrypt) {
        return this.getSecurityHandler().encode(encrypt);
    }

    public FieldEncryptProcessor getSecurityHandler() {
        if (Objects.isNull(this.fieldEncryptProcessor)) {
            this.fieldEncryptProcessor = SpringKit.getBean(FieldEncryptProcessor.class);
        }
        return this.fieldEncryptProcessor;
    }

}
