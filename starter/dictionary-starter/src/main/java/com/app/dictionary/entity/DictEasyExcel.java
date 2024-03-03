package com.app.dictionary.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DictEasyExcel {

    @ExcelProperty(value = {"dict code"}, index = 0)
    private String dictCode;

    @ExcelProperty(value = {"dict name"}, index = 1)
    private String dictName;

    @ExcelProperty(value = {"item key"}, index = 2)
    private String itemKey;

    @ExcelProperty(value = {"item value"}, index = 3)
    private String itemValue;

    @ExcelProperty(value = {"description"}, index = 4)
    private String description;

    @ExcelProperty(value = {"sort order"}, index = 5)
    private Integer sortOrder;
}
