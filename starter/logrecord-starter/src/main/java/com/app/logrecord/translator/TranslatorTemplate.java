package com.app.logrecord.translator;

import com.app.logrecord.annotation.LogRecordField;
import com.app.logrecord.pojo.FieldDiffDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/27 15:31
 * @description: 自定义翻译器抽象模板
 */
public abstract class TranslatorTemplate {
    /**
     * 自定义翻译器抽象模板方法
     * @param oldObject
     * @param newObject
     * @return
     */
    public abstract List<FieldDiffDTO> translate(Object oldObject, Object newObject, LogRecordField logRecordField);

    public abstract class None extends TranslatorTemplate {
        @Override
        public List<FieldDiffDTO> translate(Object oldObject, Object newObject, LogRecordField logRecordField) {
            return new ArrayList<>();
        }
    }
}
