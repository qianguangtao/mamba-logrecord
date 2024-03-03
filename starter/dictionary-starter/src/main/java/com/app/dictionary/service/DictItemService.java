package com.app.dictionary.service;

import com.app.core.mvc.serialization.DictionaryTranslator;
import com.app.dictionary.entity.DictItem;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/16 15:29
 * @description: 字典项service
 */
public interface DictItemService extends IService<DictItem>, DictionaryTranslator {
}
