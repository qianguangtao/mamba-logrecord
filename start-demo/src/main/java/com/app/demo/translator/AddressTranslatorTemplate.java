package com.app.demo.translator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.app.demo.pojo.dto.Address;
import com.app.logrecord.annotation.LogRecordField;
import com.app.logrecord.pojo.FieldDiffDTO;
import com.app.logrecord.pojo.ObjectDiffDTO;
import com.app.logrecord.translator.TranslatorTemplate;
import com.app.logrecord.utils.ObjectDiffUtil;

import java.util.List;
import java.util.Optional;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/27 15:41
 * @description: 测试翻译模板
 */
public class AddressTranslatorTemplate extends TranslatorTemplate {
    @Override
    public List<FieldDiffDTO> translate(Object oldObject, Object newObject, LogRecordField logRecordField) {
        Address oldAddress = Optional.ofNullable(oldObject).map(obj -> JSON.parseObject(Convert.toStr(obj), Address.class)).orElse(new Address());
        Address newAddress = Optional.ofNullable(newObject).map(obj -> BeanUtil.toBean(newObject, Address.class)).orElse(new Address());
        ObjectDiffDTO objectDiffDTO = ObjectDiffUtil.objectDiff(oldAddress, newAddress);
        return ObjectUtil.isNotNull(objectDiffDTO) ? objectDiffDTO.getFieldDiffDTOList() : null;
    }
}
