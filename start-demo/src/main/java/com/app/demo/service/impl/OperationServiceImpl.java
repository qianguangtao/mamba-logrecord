package com.app.demo.service.impl;

import cn.hutool.core.convert.Convert;
import com.app.demo.entity.Operation;
import com.app.demo.entity.OperationField;
import com.app.demo.mapper.OperationMapper;
import com.app.demo.service.OperationFieldService;
import com.app.demo.service.OperationService;
import com.app.logrecord.pojo.FieldDiffDTO;
import com.app.logrecord.pojo.ObjectDiffDTO;
import com.app.logrecord.service.ObjectDiffService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/5 11:11
 * @description: 操作记录service
 */
@RequiredArgsConstructor
@Validated
@Service
public class OperationServiceImpl extends ServiceImpl<OperationMapper, Operation>
        implements OperationService, ObjectDiffService {

    private final OperationFieldService operationFieldService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(ObjectDiffDTO objectDiffDTO) {
        // 存入操作记录表
        Operation operation = new Operation();
        operation.setBusinessId(Convert.toLong(objectDiffDTO.getBusinessId()));
        operation.setType(objectDiffDTO.getLogOperate());
        operation.setDescription(objectDiffDTO.getDescription());
        operation.setClassBefore(objectDiffDTO.getOldClassName());
        operation.setClassAfter(objectDiffDTO.getNewClassName());
        operation.setJsonBefore(objectDiffDTO.getJsonBefore());
        operation.setJsonAfter(objectDiffDTO.getJsonAfter());
        operation.setOperatorName(objectDiffDTO.getOperatorName());
        this.save(operation);
        // 存入操作记录字段表
        List<FieldDiffDTO> fieldDiffDTOList = objectDiffDTO.getFieldDiffDTOList();
        for (FieldDiffDTO fieldDiffDTO : fieldDiffDTOList) {
            OperationField operationField = new OperationField();
            operationField.setOperationId(operation.getId());
            operationField.setFieldName(fieldDiffDTO.getFieldName());
            operationField.setFieldNameShow(fieldDiffDTO.getName());
            operationField.setChangeType(fieldDiffDTO.getEditType());
            operationField.setFieldBefore(Convert.toStr(fieldDiffDTO.getOldValue()));
            operationField.setFieldBeforeShow(Convert.toStr(fieldDiffDTO.getOldValueShow()));
            operationField.setFieldAfter(Convert.toStr(fieldDiffDTO.getNewValue()));
            operationField.setFieldAfterShow(Convert.toStr(fieldDiffDTO.getNewValueShow()));
            operationFieldService.save(operationField);
        }
    }
}
