package com.app.logrecord.translator;

import com.app.core.mvc.serialization.EnumDefinition;
import lombok.NoArgsConstructor;

/**
 * @author qiangt
 * @date 2023/9/29
 * @apiNote 枚举日志描述转换
 */
@NoArgsConstructor
public class EnumTranslator implements Translator<EnumDefinition, String> {

    @Override
    public String translate(EnumDefinition var) {
        return var.getComment();
    }
}
