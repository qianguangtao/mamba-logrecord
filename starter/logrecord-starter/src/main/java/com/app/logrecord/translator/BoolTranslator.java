package com.app.logrecord.translator;

import lombok.NoArgsConstructor;

/**
 * @author qiangt
 * @date 2023/9/29
 * @apiNote bool日志描述转换
 */
@NoArgsConstructor
public class BoolTranslator implements Translator<Boolean, String> {

    @Override
    public String translate(Boolean var) {
        return var ? "是" : "否";
    }
}
