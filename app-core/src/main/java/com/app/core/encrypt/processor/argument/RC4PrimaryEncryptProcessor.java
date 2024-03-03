package com.app.core.encrypt.processor.argument;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.RC4;
import com.app.kit.Base64;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class RC4PrimaryEncryptProcessor implements PrimaryEncryptProcessor {

    private final RC4 rc4;

    public RC4PrimaryEncryptProcessor(final String key) {
        this.rc4 = SecureUtil.rc4(key);
    }

    @Override
    public Serializable encode(final Serializable value) {
        if (Objects.isNull(value)) {
            return null;
        }
        log.debug("加密前：{}", value);
        final String encryptValue = this.rc4.encryptBase64(value.toString().getBytes(StandardCharsets.UTF_8));
        log.debug("加密后：{}", encryptValue);
        return encryptValue;
    }

    @Override
    public Serializable decode(final Serializable value) {
        if (Objects.isNull(value)) {
            return null;
        }
        log.debug("解密前：{}", value);
        final String decryptValue = this.rc4.decrypt(Base64.decode(value.toString()));
        log.debug("解密后：{}", decryptValue);
        try {
            return Long.valueOf(decryptValue);
        } catch (final Exception e) {
            log.error("解密出的值无法解析为Long数据类型，{}->{}", value, decryptValue);
            return null;
        }
    }

}
