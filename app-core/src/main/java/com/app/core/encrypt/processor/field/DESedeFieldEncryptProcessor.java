package com.app.core.encrypt.processor.field;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DESede;
import com.app.core.encrypt.Encrypt;
import com.app.kit.Base64;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DESedeFieldEncryptProcessor implements FieldEncryptProcessor {

    private final DESede desede;

    public DESedeFieldEncryptProcessor(final String key) {
        this.desede = SecureUtil.desede(Base64.decode(key));
    }

    @Override
    public String encode(final Encrypt encrypt) {
        log.info("加密前：{}", encrypt.getValue());
        final String encryptValue = this.desede.encryptHex(encrypt.getValue());
        log.info("加密后：{}", encryptValue);
        return encryptValue;
    }

    @Override
    public Encrypt decode(final String value) {
        log.info("解密前：{}", value);
        final String decryptValue = this.desede.decryptStr(value);
        log.info("解密后：{}", decryptValue);
        return new Encrypt(decryptValue);
    }

}
