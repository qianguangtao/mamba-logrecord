package com.app.dictionary.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.app.core.enums.EnabledEnum;
import com.app.core.mybatis.concurrent.AsyncTaskHelper;
import com.app.dictionary.DictCallback;
import com.app.dictionary.entity.Dict;
import com.app.dictionary.entity.DictEasyExcel;
import com.app.dictionary.entity.DictItem;
import com.app.dictionary.mapper.DictItemMapper;
import com.app.dictionary.mapper.DictMapper;
import com.app.dictionary.service.DictItemService;
import com.app.dictionary.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/16 15:29
 * @description: 字典service impl
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    private final DictItemService dictItemService;

    private final DictMapper dictMapper;
    private final DictItemMapper dictItemMapper;
    private final ExecutorService ttlExecutorService;
    private final PlatformTransactionManager transactionManager;

    @Override
    public void importExcel(MultipartFile file) throws Exception {
        EasyExcel.read(file.getInputStream(), DictEasyExcel.class, new ReadListener<DictEasyExcel>() {

            /**
             * 单次缓存的数据量
             */
            public static final int BATCH_COUNT = 1000;
            /**
             *临时存储
             */
            private List<DictEasyExcel> dataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

            @Override
            public void invoke(DictEasyExcel data, AnalysisContext analysisContext) {
                dataList.add(data);
                if (dataList.size() >= BATCH_COUNT) {
                    saveData();
                    // 存储完成清理 list
                    dataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                saveData();
            }

            private void saveData() {
                // 保存DICT
                // DictEasyExcel dictEasyExcel = dataList.get(0);
                // deleteByCode(dictEasyExcel.getDictCode());
                // Dict dict = new Dict();
                // dict.setDictCode(dictEasyExcel.getDictCode());
                // dict.setDictName(dictEasyExcel.getDictName());
                // dictMapper.insert(dict);

                List<Dict> dictList = dataList.stream().map(e -> {
                    Dict dict = new Dict();
                    dict.setDictCode(e.getDictCode());
                    dict.setDictName(e.getDictName());
                    return dict;
                }).collect(Collectors.toList());
                getDictService().saveBatch(dictList);

                // 保存DictItem数据
                List<DictItem> dictItemList = dataList.stream().map(e -> {
                    DictItem dictItem = new DictItem();
                    dictItem.setDictCode(e.getDictCode());
                    dictItem.setItemKey(e.getItemKey());
                    dictItem.setItemValue(e.getItemValue());
                    dictItem.setDescription(e.getDescription());
                    dictItem.setSortOrder(e.getSortOrder());
                    dictItem.setEnabled(EnabledEnum.Enabled);
                    return dictItem;
                }).collect(Collectors.toList());
                dictItemService.saveBatch(dictItemList);
            }
        }).sheet().doRead();
    }

    private DictService getDictService() {
        return this;
    }

    @Override
    public void importExcelMapper(MultipartFile file) throws Exception {
        EasyExcel.read(file.getInputStream(), DictEasyExcel.class, new ReadListener<DictEasyExcel>() {

            /**
             * 单次缓存的数据量
             */
            public static final int BATCH_COUNT = 1000;
            /**
             *临时存储
             */
            private final List<DictEasyExcel> dataList = new ArrayList<>();

            @Override
            public void invoke(DictEasyExcel data, AnalysisContext analysisContext) {
                dataList.add(data);
                if (dataList.size() >= BATCH_COUNT) {
                    try {
                        saveData();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    } finally {
                        // 存储完成清理 list
                        dataList.clear();
                    }

                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                if (CollectionUtil.isNotEmpty(dataList)) {
                    saveData();
                }
            }

            private void saveData() {
                // 保存DICT
                // DictEasyExcel dictEasyExcel = dataList.get(0);
                // deleteByCode(dictEasyExcel.getDictCode());
                // Dict dict = new Dict();
                // dict.setDictCode(dictEasyExcel.getDictCode());
                // dict.setDictName(dictEasyExcel.getDictName());
                // dictMapper.insert(dict);

                List<Dict> dictList = dataList.stream().map(e -> {
                    Dict dict = new Dict();
                    dict.setDictCode(e.getDictCode());
                    dict.setDictName(e.getDictName());
                    return dict;
                }).collect(Collectors.toList());
                dictMapper.insertBatch(dictList);

                // 保存DictItem数据
                List<DictItem> dictItemList = dataList.stream().map(e -> {
                    DictItem dictItem = new DictItem();
                    dictItem.setDictCode(e.getDictCode());
                    dictItem.setItemKey(e.getItemKey());
                    dictItem.setItemValue(e.getItemValue());
                    dictItem.setDescription(e.getDescription());
                    dictItem.setSortOrder(e.getSortOrder());
                    dictItem.setEnabled(EnabledEnum.Enabled);
                    return dictItem;
                }).collect(Collectors.toList());
                dictItemMapper.insertBatch(dictItemList);
            }
        }).sheet().doRead();
    }

    @Override
    public void importExcelThread(MultipartFile file) throws Exception {

        List<List<DictEasyExcel>> batchList = new ArrayList<>();
        EasyExcel.read(file.getInputStream(), DictEasyExcel.class, new ReadListener<DictEasyExcel>() {

            /**
             * 单次缓存的数据量
             */
            public static final int BATCH_COUNT = 1000;
            /**
             *临时存储
             */
            private final List<DictEasyExcel> dataList = new ArrayList<>();

            @Override
            public void invoke(DictEasyExcel data, AnalysisContext analysisContext) {
                dataList.add(data);
                if (dataList.size() >= BATCH_COUNT) {
                    List<DictEasyExcel> temp = new ArrayList<DictEasyExcel>();
                    temp.addAll(dataList);
                    batchList.add(temp);
                    // 存储完成清理 list
                    dataList.clear();
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }

        }).sheet().doRead();

        List<Callable<Boolean>> callableList = new ArrayList<>();
        for (int i = 0; i < batchList.size(); i++) {
            List<DictEasyExcel> dictEasyExcels = batchList.get(i);
            Callable<Boolean> callable = () -> {
                return getDictService().batchSaveDictAndItem(dictEasyExcels);
            };
            callableList.add(callable);
        }
        List<Future<Boolean>> futures = ttlExecutorService.invokeAll(callableList);
        for (Future<Boolean> future : futures) {
            log.info("多线程执行batch save结果：{}", future.get());
        }
    }

    @Override
    public void importExcelThreadTransactional(MultipartFile file) throws Exception {

        List<List<DictEasyExcel>> batchList = new ArrayList<>();
        EasyExcel.read(file.getInputStream(), DictEasyExcel.class, new ReadListener<DictEasyExcel>() {

            /**
             * 单次缓存的数据量
             */
            public static final int BATCH_COUNT = 2000;
            /**
             *临时存储
             */
            private final List<DictEasyExcel> dataList = new ArrayList<>();

            @Override
            public void invoke(DictEasyExcel data, AnalysisContext analysisContext) {
                dataList.add(data);
                if (dataList.size() >= BATCH_COUNT) {
                    List<DictEasyExcel> temp = new ArrayList<DictEasyExcel>();
                    temp.addAll(dataList);
                    batchList.add(temp);
                    // 存储完成清理 list
                    dataList.clear();
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }

        }).sheet().doRead();

        List<DictCallback> list = new ArrayList<DictCallback>();
        for (int i = 0; i < batchList.size(); i++) {
            DictCallback dictCallback = new DictCallback(batchList.get(i));
            list.add(dictCallback);
        }
        AsyncTaskHelper.doWork(list, transactionManager);
    }

    @Override
    public void deleteByCode(String code) {
        List<Dict> dictList = this.lambdaQuery()
                .eq(Dict::getDictCode, code).list();
        if (ObjectUtil.isNotEmpty(dictList)) {
            dictList.stream().forEach(dict -> this.baseMapper.deleteById(dict));
        }

        List<DictItem> itemList = dictItemService.lambdaQuery()
                .eq(DictItem::getDictCode, code).list();
        if (ObjectUtil.isNotEmpty(itemList)) {
            itemList.stream().forEach(dictItem -> dictItemService.getBaseMapper().deleteById(dictItem));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean batchSaveDictAndItem(List<DictEasyExcel> dictEasyExcelList) {
        List<Dict> dictList = dictEasyExcelList.stream().map(e -> {
            Dict dict = new Dict();
            dict.setDictCode(e.getDictCode());
            dict.setDictName(e.getDictName());
            return dict;
        }).collect(Collectors.toList());

        // 保存DictItem数据
        List<DictItem> dictItemList = dictEasyExcelList.stream().map(e -> {
            DictItem dictItem = new DictItem();
            dictItem.setDictCode(e.getDictCode());
            dictItem.setItemKey(e.getItemKey());
            dictItem.setItemValue(e.getItemValue());
            dictItem.setDescription(e.getDescription());
            dictItem.setSortOrder(e.getSortOrder());
            dictItem.setEnabled(EnabledEnum.Enabled);
            return dictItem;
        }).collect(Collectors.toList());
        int i = dictMapper.insertBatch(dictList);
        int j = dictItemMapper.insertBatch(dictItemList);
        return i == dictList.size() && j == dictItemList.size();
    }

    @Override
    public boolean batchSaveDictAndItemTransactional(List<DictEasyExcel> dictEasyExcelList) {
        List<Dict> dictList = dictEasyExcelList.stream().map(e -> {
            Dict dict = new Dict();
            dict.setDictCode(e.getDictCode());
            dict.setDictName(e.getDictName());
            return dict;
        }).collect(Collectors.toList());

        // 保存DictItem数据
        List<DictItem> dictItemList = dictEasyExcelList.stream().map(e -> {
            DictItem dictItem = new DictItem();
            dictItem.setDictCode(e.getDictCode());
            dictItem.setItemKey(e.getItemKey());
            dictItem.setItemValue(e.getItemValue());
            dictItem.setDescription(e.getDescription());
            dictItem.setSortOrder(e.getSortOrder());
            dictItem.setEnabled(EnabledEnum.Enabled);
            return dictItem;
        }).collect(Collectors.toList());
        int i = dictMapper.insertBatch(dictList);
        int j = dictItemMapper.insertBatch(dictItemList);
        return i == dictList.size() && j == dictItemList.size();
    }
}
