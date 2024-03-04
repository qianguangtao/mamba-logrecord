package com.app.demo.service.impl;

import com.app.demo.entity.OperationField;
import com.app.demo.mapper.OperationFieldMapper;
import com.app.demo.service.OperationFieldService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/5 11:11
 * @description: 操作字段service
 */
@Validated
@Service
public class OperationFieldServiceImpl extends ServiceImpl<OperationFieldMapper, OperationField>
        implements OperationFieldService {
}
