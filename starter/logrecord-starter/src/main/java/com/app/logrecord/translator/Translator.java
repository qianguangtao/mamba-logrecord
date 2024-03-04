package com.app.logrecord.translator;

/**
 * @author qiangt
 * @date 2023/9/29
 * @apiNote 对象比较翻译器接口
 */
public interface Translator<IN, OUT> {

    /**
     * 将输入IN转成输出OUT
     * @param var
     * @return
     */
    OUT translate(IN var);

    /**
     * 用来作为注解的默认值
     */
    abstract class None implements Translator<Object, Object> {
        public None() {
        }
    }
}
