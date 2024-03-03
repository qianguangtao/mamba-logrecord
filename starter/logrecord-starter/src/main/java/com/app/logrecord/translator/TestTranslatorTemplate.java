package com.app.logrecord.translator;

import cn.hutool.core.collection.ListUtil;
import com.app.logrecord.annotation.LogRecordField;
import com.app.logrecord.enums.EditType;
import com.app.logrecord.pojo.FieldDiffDTO;

import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/27 15:41
 * @description: 测试翻译模板
 */
public class TestTranslatorTemplate extends TranslatorTemplate {
    @Override
    public List<FieldDiffDTO> translate(Object oldObject, Object newObject, LogRecordField logRecordField) {
        FieldDiffDTO fieldDiffDTO = new FieldDiffDTO();
        fieldDiffDTO.setFieldName("age")
                .setName("年龄")
                .setOldValue(12)
                .setNewValue(13)
                .setNewValueShow(13)
                .setOldValueShow(12)
                .setEditType(EditType.UPDATE);
        return ListUtil.of(fieldDiffDTO);
    }
}
