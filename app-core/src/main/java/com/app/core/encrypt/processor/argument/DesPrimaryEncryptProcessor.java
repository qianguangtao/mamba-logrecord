package com.app.core.encrypt.processor.argument;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;
import com.app.kit.Base64;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;

@Slf4j
public class DesPrimaryEncryptProcessor implements PrimaryEncryptProcessor {

    private final DES des;

    public DesPrimaryEncryptProcessor(final String key) {
        this.des = SecureUtil.des(Base64.decode(key));
    }

    @Override
    public Serializable encode(final Serializable value) {
        if (Objects.isNull(value)) {
            return null;
        }
        log.debug("加密前：{}", value);
        final String encryptValue = this.des.encryptHex(value.toString());
        log.debug("加密后：{}", encryptValue);
        return encryptValue;
    }

    @Override
    public Serializable decode(final Serializable value) {
        if (Objects.isNull(value)) {
            return null;
        }
        log.debug("解密前：{}", value);
        final String decryptValue = this.des.decryptStr(value.toString());
        log.debug("解密后：{}", decryptValue);
        try {
            return Long.valueOf(decryptValue);
        } catch (final Exception e) {
            log.error("解密出的值无法解析为Long数据类型，{}->{}", value, decryptValue);
            return null;
        }
    }

}
