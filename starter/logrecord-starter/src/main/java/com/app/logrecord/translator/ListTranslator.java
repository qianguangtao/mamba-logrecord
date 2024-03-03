package com.app.logrecord.translator;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * @author qiangt
 * @date 2023/9/29
 * @apiNote List属性日志描述转换
 */
public class ListTranslator implements Translator<List, String> {

    @Override
    public String translate(List var) {
        return JSON.toJSONString(var);
    }
}
