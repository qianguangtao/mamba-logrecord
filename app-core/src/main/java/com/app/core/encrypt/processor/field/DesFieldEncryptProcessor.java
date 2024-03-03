package com.app.core.encrypt.processor.field;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;
import com.app.core.encrypt.Encrypt;
import com.app.kit.Base64;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DesFieldEncryptProcessor implements FieldEncryptProcessor {

    private final DES des;

    public DesFieldEncryptProcessor(final String key) {
        this.des = SecureUtil.des(Base64.decode(key));
    }

    @Override
    public String encode(final Encrypt encrypt) {
        log.info("加密前：{}", encrypt.getValue());
        final String encryptValue = this.des.encryptHex(encrypt.getValue());
        log.info("加密后：{}", encryptValue);
        return encryptValue;
    }

    @Override
    public Encrypt decode(final String value) {
        log.info("解密前：{}", value);
        final String decryptValue = this.des.decryptStr(value);
        log.info("解密后：{}", decryptValue);
        return new Encrypt(decryptValue);
    }

}
