package com.app.dictionary.mapper;

import com.app.dictionary.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/16 15:29
 * @description: 字典mapper
 */
public interface DictMapper extends BaseMapper<Dict> {
    int insertBatch(List<Dict> dictList);

    Dict selectByCode(@Param("code") String code);
}
