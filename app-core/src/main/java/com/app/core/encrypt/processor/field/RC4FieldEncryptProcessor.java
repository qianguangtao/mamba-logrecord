package com.app.core.encrypt.processor.field;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.RC4;
import com.app.core.encrypt.Encrypt;
import com.app.kit.Base64;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class RC4FieldEncryptProcessor implements FieldEncryptProcessor {

    private final RC4 rc4;

    public RC4FieldEncryptProcessor(final String key) {
        this.rc4 = SecureUtil.rc4(key);
    }

    @Override
    public String encode(final Encrypt encrypt) {
        log.info("加密前：{}", encrypt.getValue());
        final String encryptValue = this.rc4.encryptBase64(encrypt.getValue().getBytes(StandardCharsets.UTF_8));
        log.info("加密后：{}", encryptValue);
        return encryptValue;
    }

    @Override
    public Encrypt decode(final String value) {
        log.info("解密前：{}", value);
        final String decryptValue = this.rc4.decrypt(Base64.decode(value));
        log.info("解密后：{}", decryptValue);
        return new Encrypt(decryptValue);
    }

}
