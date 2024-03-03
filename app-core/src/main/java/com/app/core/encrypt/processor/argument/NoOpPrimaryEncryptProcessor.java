package com.app.core.encrypt.processor.argument;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 非加密操作
 */
@Slf4j
public class NoOpPrimaryEncryptProcessor implements PrimaryEncryptProcessor {

    @Override
    public Serializable encode(final Serializable value) {
        log.debug("加密前：{}", value);
        log.debug("加密后：{}", value);
        return value;
    }

    @Override
    public Serializable decode(final Serializable value) {
        log.debug("解密前：{}", value);
        log.debug("解密后：{}", value);
        return value;
    }

}
