package com.app.core.encrypt.processor.field;

import com.app.core.encrypt.Encrypt;
import lombok.extern.slf4j.Slf4j;

/**
 * 非加密操作
 */
@Slf4j
public class NoOpFieldEncryptProcessor implements FieldEncryptProcessor {

    @Override
    public String encode(final Encrypt encrypt) {
        log.info("加密前：{}", encrypt.getValue());
        log.info("加密后：{}", encrypt.getValue());
        return encrypt.getValue();
    }

    @Override
    public Encrypt decode(final String value) {
        log.info("解密前：{}", value);
        log.info("解密后：{}", value);
        return new Encrypt(value);
    }

}
