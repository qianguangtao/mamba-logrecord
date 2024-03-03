package com.app.dictionary.service;

import com.app.dictionary.entity.Dict;
import com.app.dictionary.entity.DictEasyExcel;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/16 15:29
 * @description: 字典service
 */
public interface DictService extends IService<Dict> {
    /**
     * 导入excel
     */
    void importExcel(MultipartFile file) throws Exception;

    void importExcelMapper(MultipartFile file) throws Exception;

    void importExcelThread(MultipartFile file) throws Exception;

    void importExcelThreadTransactional(MultipartFile file) throws Exception;

    void deleteByCode(String code);

    boolean batchSaveDictAndItem(List<DictEasyExcel> dictEasyExcelList);

    boolean batchSaveDictAndItemTransactional(List<DictEasyExcel> dictEasyExcelList);
}
