package com.app.logrecord.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.app.core.mvc.serialization.DictionaryTranslator;
import com.app.kit.SpringKit;
import com.app.logrecord.annotation.LogRecordField;
import com.app.logrecord.enums.EditType;
import com.app.logrecord.pojo.FieldDiffDTO;
import com.app.logrecord.pojo.ObjectDiffDTO;
import com.app.logrecord.translator.Translator;
import com.app.logrecord.translator.TranslatorTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/25 13:44
 * @description: 对象比较工具类
 */
@Slf4j
public class ObjectDiffUtil {

    /**
     * 比较新旧对象
     * @param oldObject
     * @param newObject
     * @return 比较结果DTO
     */
    public static ObjectDiffDTO objectDiff(Object oldObject, Object newObject) {
        // 若包含null对象，直接返回
        if (ObjectUtil.isNull(oldObject) || ObjectUtil.isNull(newObject)) {
            log.error("null object found [{}] [{}]", oldObject, newObject);
            return null;
        }
        // 对象类名
        String oldClassName = oldObject.getClass().getName();
        String newClassName = newObject.getClass().getName();
        // 遍历旧对象全部加LogRecordField注解字段，获取字段变动list
        List<FieldDiffDTO> fieldDiffDTOList = getFieldDiffDTOList(oldObject, newObject);
        ObjectDiffDTO diffDTO = new ObjectDiffDTO();
        diffDTO.setOldClassName(oldClassName);
        diffDTO.setNewClassName(newClassName);
        diffDTO.setFieldDiffDTOList(fieldDiffDTOList);
        return diffDTO;
    }

    /**
     * 获取字段变动list
     * @param oldObject
     * @param newObject
     * @return
     */
    private static List<FieldDiffDTO> getFieldDiffDTOList(Object oldObject, Object newObject) {
        List<FieldDiffDTO> fieldDiffDTOList = new ArrayList<>();
        Map<String, Field> newFieldMap = getAllFieldMap(newObject);
        Map<String, Field> oldFieldMap = getAllFieldMap(oldObject);
        for (String newFieldKey : newFieldMap.keySet()) {
            try {
                Field newField = newFieldMap.get(newFieldKey);
                LogRecordField newObjectLogRecordDiff = newField.getDeclaredAnnotation(LogRecordField.class);
                // 若没有LogRecordDiff注解，跳过
                if (newObjectLogRecordDiff == null) {
                    continue;
                }
                // 获取@LogRecordField的全部属性
                final String fieldDesc = newObjectLogRecordDiff.value();
                final Class<? extends Translator> translatorClass = newObjectLogRecordDiff.translator();
                Class<? extends TranslatorTemplate> translatorTemplateClass = newObjectLogRecordDiff.translatorTemplate();
                final String nullDesc = newObjectLogRecordDiff.nullDesc();
                // 使用反射获取属性值
                Field oldField = oldFieldMap.get(newField.getName());
                newField.setAccessible(true);
                oldField.setAccessible(true);
                Object newValue = newField.get(newObject);
                Object oldValue = oldField.get(oldObject);
                if (!TranslatorTemplate.None.class.equals(translatorTemplateClass)) {
                    TranslatorTemplate template = translatorTemplateClass.newInstance();
                    List<FieldDiffDTO> fieldList = template.translate(oldValue, newValue, newObjectLogRecordDiff);
                    if (CollectionUtil.isNotEmpty(fieldList)) {
                        fieldList.stream().forEach(fieldDiffDTO -> {
                            if (StrUtil.isEmpty(fieldDiffDTO.getFieldName())) {
                                fieldDiffDTO.setFieldName(newField.getName());
                            }
                            if (StrUtil.isEmpty(fieldDiffDTO.getName())) {
                                fieldDiffDTO.setName(fieldDesc);
                            }
                        });
                        fieldDiffDTOList.addAll(fieldList);
                    }
                    continue;
                }
                Object translatedNewValue = null;
                Object translatedOldValue = null;
                com.app.core.mvc.serialization.Dictionary dictionary = newField.getDeclaredAnnotation(com.app.core.mvc.serialization.Dictionary.class);
                boolean isDictionary = ObjectUtil.isNotNull(dictionary);
                DateTimeFormat dateTimeFormat = newField.getDeclaredAnnotation(DateTimeFormat.class);
                boolean isDate = ObjectUtil.isNotNull(dateTimeFormat) && StrUtil.isNotBlank(dateTimeFormat.pattern());
                if (isDictionary) {
                    // 字典@Dictionary转换属性，数据库中读取字典表处理
                    String dictCode = dictionary.value();
                    translatedNewValue = doTranslateDictionary(newValue, dictCode);
                    translatedOldValue = doTranslateDictionary(oldValue, dictCode);
                } else if (isDate) {
                    // 日期转换
                    translatedNewValue = doTranslateDate(newValue, dateTimeFormat.pattern());
                    translatedOldValue = doTranslateDate(oldValue, dateTimeFormat.pattern());
                } else {
                    // 自定义解释器转换属性，则使用解释器获取转换后的字段描述
                    translatedNewValue = doTranslate(newValue, translatorClass);
                    translatedOldValue = doTranslate(oldValue, translatorClass);
                }
                if ((newField.getType() == String.class) && StrUtil.isBlank(Convert.toStr(newValue)) && StrUtil.isBlank(Convert.toStr(oldValue))) {
                    continue;
                }
                if (!Objects.equals(translatedNewValue, translatedOldValue)) {
                    // 将translatedNewValue，translatedOldValue转成数组传入getEditType为了可以修改原值
                    Object[] oldArr = new Object[]{translatedOldValue};
                    Object[] newArr = new Object[]{translatedNewValue};
                    EditType editType = getEditType(oldArr, newArr, nullDesc);
                    FieldDiffDTO fieldDiffDTO = new FieldDiffDTO();
                    fieldDiffDTO.setFieldName(newField.getName())
                            .setName(fieldDesc)
                            .setEditType(editType)
                            .setOldValue(oldValue)
                            .setNewValue(newValue)
                            .setOldValueShow(oldArr[0])
                            .setNewValueShow(newArr[0]);
                    fieldDiffDTOList.add(fieldDiffDTO);
                }
            } catch (NoSuchFieldException e) {
                log.warn("no field named [{}] in newObject, skip", newFieldKey);
            } catch (Exception e) {
                log.warn("objectDiff error", e);
            }
        }
        return fieldDiffDTOList;
    }

    /**
     * 对象属性转map
     * @param obj
     * @return
     */
    private static Map<String, Field> getAllFieldMap(Object obj) {
        // 获取对象的类
        Class<?> clazz = obj.getClass();

        // 创建一个列表来保存所有字段
        List<Field> fieldList = new ArrayList<>();

        // 遍历类及其所有父类
        while (clazz != null) {
            // 获取当前类的所有字段
            Field[] fields = clazz.getDeclaredFields();
            // 将字段添加到列表中
            fieldList.addAll(Arrays.asList(fields));
            // 获取父类
            clazz = clazz.getSuperclass();
        }
        Map<String, Field> fieldMap = fieldList.stream().collect(Collectors.toMap(Field::getName, field -> {
            return field;
        }));
        return fieldMap;
    }

    /**
     * 翻译字典（比如数据库0|1转中文男|女）
     * @param itemValue 字典值
     * @param dictCode  字典码
     * @return
     * @throws Exception
     */
    private static Object doTranslateDictionary(final Object itemValue, String dictCode) throws Exception {
        if (ObjectUtil.isNull(itemValue)) {
            return null;
        }
        DictionaryTranslator dictionaryTranslator = SpringKit.getBean(DictionaryTranslator.class);
        return dictionaryTranslator.translate(dictCode, Convert.toStr(itemValue));
    }

    /**
     * 日期转换
     * @param obj
     * @param format
     * @return
     * @throws Exception
     */
    private static Object doTranslateDate(final Object obj, String format) throws Exception {
        if (obj instanceof Date) {
            return DateUtil.format((Date) obj, format);
        } else if (obj instanceof LocalDateTime) {
            return DateUtil.format((LocalDateTime) obj, format);
        }
        return obj;
    }

    /**
     * 基于Translator转换器做翻译，比如枚举，boolean
     * @param obj
     * @param translatorClass
     * @return
     * @throws Exception
     */
    private static Object doTranslate(final Object obj, Class<? extends Translator> translatorClass) throws Exception {
        if (ObjectUtil.isNotEmpty(obj) && (!Translator.None.class.equals(translatorClass))) {
            Object tempObj = obj;
            final Translator translator = SpringKit.getBean(translatorClass);
            return translator.translate(tempObj);
        }
        return obj;
    }

    /**
     * 获取字段的修改类型，同时给空值赋@LogRecordField配置的空值描述
     * @param oldArr
     * @param newArr
     * @param nullDesc
     * @return
     */
    private static EditType getEditType(Object[] oldArr, Object[] newArr, final String nullDesc) {
        EditType editType = EditType.UPDATE;
        if (ObjectUtil.isEmpty(newArr[0]) && ObjectUtil.isNotEmpty(oldArr[0])) {
            // 1. oldValue为空，newValue不为空
            newArr[0] = nullDesc;
            editType = EditType.DELETE;
        } else if (ObjectUtil.isEmpty(oldArr[0]) && ObjectUtil.isNotEmpty(newArr[0])) {
            oldArr[0] = nullDesc;
            editType = EditType.SAVE;
        }
        return editType;
    }

}
