package com.app.dictionary.controller;


import com.app.core.mvc.result.Result;
import com.app.dictionary.entity.Dict;
import com.app.dictionary.entity.DictEasyExcel;
import com.app.dictionary.mapper.DictMapper;
import com.app.dictionary.service.DictService;
import com.app.kit.ExcelUtil;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Api(tags = "字典接口")
@RestController
@RequestMapping("/dictionaries/v1")
@RequiredArgsConstructor
public class DictController {

    private final DictService service;
    private final DictMapper mapper;

    @PostMapping("/excel")
    public Result<Void> importExcel(MultipartFile file) {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            service.importExcel(file);
            stopWatch.stop();
            log.info("Successfully imported, 耗时/秒：{}", stopWatch.getTotalTimeSeconds());
        } catch (Exception e) {
            log.error("字典导入失败, {}", e.getMessage(), e);
            return Result.fail();
        }
        return Result.success();
    }

    @PostMapping("/excelMapper")
    public Result<Void> importExcelMapper(MultipartFile file) {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            service.importExcelMapper(file);
            stopWatch.stop();
            log.info("Successfully imported, 耗时/秒：{}", stopWatch.getTotalTimeSeconds());
        } catch (Exception e) {
            log.error("字典导入失败, {}", e.getMessage(), e);
            return Result.fail();
        }
        return Result.success();
    }

    @PostMapping("/excelThread")
    public Result<Void> importExcelThread(MultipartFile file) {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            service.importExcelThread(file);
            stopWatch.stop();
            log.info("Successfully imported, 耗时/秒：{}", stopWatch.getTotalTimeSeconds());
        } catch (Exception e) {
            log.error("字典导入失败, {}", e.getMessage(), e);
            return Result.fail();
        }
        return Result.success();
    }

    @PostMapping("/excelTransactional")
    public Result<Void> importExcelTransactional(MultipartFile file) {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            service.importExcelThreadTransactional(file);
            stopWatch.stop();
            log.info("Successfully imported, 耗时/秒：{}", stopWatch.getTotalTimeSeconds());
        } catch (Exception e) {
            log.error("字典导入失败, {}", e.getMessage(), e);
            return Result.fail();
        }
        return Result.success();
    }

    @PostMapping("/export")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        List<DictEasyExcel> dictList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            String prefixDict = "dict";
            String prefixDictItem = "dictItem";
            DictEasyExcel d = new DictEasyExcel();
            d.setDictCode(prefixDict + i);
            d.setItemKey(prefixDictItem + i);
            d.setDictName("字典" + i);
            d.setItemValue("字典项");
            d.setSortOrder(i);
            d.setDescription("描述");
            dictList.add(d);
        }
        ExcelUtil.doExport(dictList, DictEasyExcel.class, response, "test.xlsx");
    }

    @GetMapping("/testBatchSave")
    public Result<Integer> testBatchSave() {
        Dict dict1 = new Dict();
        dict1.setDictCode("1");
        dict1.setDictName("1");

        Dict dict2 = new Dict();
        dict2.setDictCode("2");
        dict2.setDictName("2");

        List<Dict> dictList = new ArrayList<Dict>();
        dictList.add(dict1);
        dictList.add(dict2);
        int i = mapper.insertBatch(dictList);
        return Result.success(i);
    }

    @GetMapping("/testBatchSave/{code}")
    public Result<Dict> testBatchSave(@PathVariable("code") String code) {
        return Result.success(mapper.selectByCode(code));
    }

    @SneakyThrows
    @GetMapping("/batchSaveDictAndItemTransactional")
    public Result<Boolean> batchSaveDictAndItemTransactional() {
        List<DictEasyExcel> list = new ArrayList<>();
        DictEasyExcel excel = new DictEasyExcel();
        excel.setDictCode("dict1");
        excel.setDictName("dict1");
        excel.setItemKey("dictItem1");
        excel.setItemValue("dictItem1");
        list.add(excel);
        return Result.success(service.batchSaveDictAndItemTransactional(list));
    }
}
