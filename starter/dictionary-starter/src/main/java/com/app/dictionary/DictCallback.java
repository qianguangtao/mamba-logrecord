package com.app.dictionary;

import com.app.core.mybatis.concurrent.BaseCallBack;
import com.app.dictionary.entity.DictEasyExcel;
import com.app.dictionary.service.DictService;
import com.app.kit.SpringKit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/1/23 7:46
 * @description: 字典异步批处理
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
public class DictCallback extends BaseCallBack {

    private List<DictEasyExcel> dictEasyExcelList;

    @Override
    protected void doWork() {
        DictService dictService = SpringKit.getBean(DictService.class);
        log.info("处理数据：{}, {}", this.dictEasyExcelList.get(0).getDictCode(), Thread.currentThread().getName());
        dictService.batchSaveDictAndItemTransactional(this.dictEasyExcelList);
    }
}
