package com.app.core.mvc.serialization;

import javax.validation.constraints.NotEmpty;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/19 14:50
 * @description: 字典翻译接口
 */
public interface DictionaryTranslator {

    String translate(@NotEmpty final String dictCode, @NotEmpty final String itemKey);

}
