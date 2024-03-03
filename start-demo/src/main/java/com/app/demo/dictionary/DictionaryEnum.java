package com.app.demo.dictionary;

import lombok.AllArgsConstructor;

/**
 * @author qiangt
 * @date 2023/9/13
 * @apiNote @CachePut(value = CacheEnum.Constant.Student, key = "#student.id")
 * 中的value无法直接使用CacheEnum.Student.name()
 */
@AllArgsConstructor
public enum DictionaryEnum {
    UserSource(Names.UserSource);

    private final String name;

    public interface Names {
        String UserSource = "UserSource";
    }
}
