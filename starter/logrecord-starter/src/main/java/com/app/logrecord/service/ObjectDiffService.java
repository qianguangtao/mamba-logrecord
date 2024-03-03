package com.app.logrecord.service;


import com.app.logrecord.pojo.ObjectDiffDTO;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/27 17:08
 * @description: ObjectDiffDTO入库
 */
public interface ObjectDiffService {
    /**
     * 新增ObjectDiffDTO
     * @param objectDiffDTO
     */
    void save(ObjectDiffDTO objectDiffDTO);
}
