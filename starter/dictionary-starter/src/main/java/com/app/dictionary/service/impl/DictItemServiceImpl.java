package com.app.dictionary.service.impl;

import com.app.dictionary.entity.DictItem;
import com.app.dictionary.mapper.DictItemMapper;
import com.app.dictionary.service.DictItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import java.util.Optional;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/16 15:29
 * @description: 字典项service impl
 */
@Service
public class DictItemServiceImpl extends ServiceImpl<DictItemMapper, DictItem> implements DictItemService {
    @Override
    public String translate(@NotEmpty final String dictCode, @NotEmpty final String itemKey) {
        DictItem dictItem = this.lambdaQuery()
                .eq(DictItem::getDictCode, dictCode)
                .eq(DictItem::getItemKey, itemKey)
                .one();
        return Optional.ofNullable(dictItem).map(d -> d.getItemValue()).orElse(null);
    }
}
