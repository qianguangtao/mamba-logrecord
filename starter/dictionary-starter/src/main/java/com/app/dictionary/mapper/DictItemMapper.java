package com.app.dictionary.mapper;

import com.app.dictionary.entity.DictItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/16 15:29
 * @description: 字典项mapper
 */
public interface DictItemMapper extends BaseMapper<DictItem> {
    int insertBatch(List<DictItem> dictItemList);
}
