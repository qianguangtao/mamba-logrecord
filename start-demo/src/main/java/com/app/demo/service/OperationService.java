package com.app.demo.service;

import com.app.demo.entity.Operation;
import com.app.logrecord.pojo.ObjectDiffDTO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/5 11:11
 * @description: 操作记录service
 */
public interface OperationService extends IService<Operation> {
    /**
     * 操作记录存表
     * @param objectDiffDTO
     */
    void save(ObjectDiffDTO objectDiffDTO);
}
