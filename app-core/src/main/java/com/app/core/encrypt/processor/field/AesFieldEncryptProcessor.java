package com.app.core.encrypt.processor.field;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.app.core.encrypt.Encrypt;
import com.app.kit.Base64;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AesFieldEncryptProcessor implements FieldEncryptProcessor {

    private final AES aes;

    public AesFieldEncryptProcessor(final String key) {
        this.aes = SecureUtil.aes(Base64.decode(key));
    }

    @Override
    public String encode(final Encrypt encrypt) {
        log.info("加密前：{}", encrypt.getValue());
        final String encryptValue = this.aes.encryptHex(encrypt.getValue());
        log.info("加密后：{}", encryptValue);
        return encryptValue;
    }

    @Override
    public Encrypt decode(final String value) {
        log.info("解密前：{}", value);
        final String decryptValue = this.aes.decryptStr(value);
        log.info("解密后：{}", decryptValue);
        return new Encrypt(decryptValue);
    }

}
