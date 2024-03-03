package com.app.core.encrypt.processor.argument;

import java.io.Serializable;

public interface PrimaryEncryptProcessor {

    /**
     * 加密
     * @param value 待加密的内容
     * @return 加密后的内容
     */
    Serializable encode(Serializable value);

    /**
     * 解密
     * @param value 待解密的内容
     * @return 解密后的内容
     */
    Serializable decode(Serializable value);

}
