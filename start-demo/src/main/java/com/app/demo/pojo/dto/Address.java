package com.app.demo.pojo.dto;

import com.app.logrecord.annotation.LogRecordField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/3/3 12:39
 * @description: 家庭地址
 */
@Data
public class Address implements Serializable {
    @LogRecordField(value = "国家")
    private String country;
    @LogRecordField(value = "省")
    private String province;
    @LogRecordField(value = "市")
    private String city;
}
