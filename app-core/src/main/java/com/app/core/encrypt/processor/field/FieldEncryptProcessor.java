package com.app.core.encrypt.processor.field;


import com.app.core.encrypt.Encrypt;

/**
 * 加密处理器
 * @author qiangt
 * @since 2022-10-22
 */
public interface FieldEncryptProcessor {

    /**
     * 加密
     * @param encrypt 待加密的内容
     * @return 加密后的内容
     */
    String encode(Encrypt encrypt);

    /**
     * 解密
     * @param value 待解密的内容
     * @return 解密后的内容
     */
    Encrypt decode(String value);

}
