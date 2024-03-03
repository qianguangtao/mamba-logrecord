package com.app.core.util;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Objects;

/**
 * @author WangJie
 * @date 2023/7/19 15:40
 * @apiNote
 */
public class PojoHelper {
    public static <R> IPage<R> convertPage(IPage page, Class<R> clazz) {
        IPage<R> respIPage = new Page<R>();
        if (Objects.nonNull(page)) {
            respIPage.setPages(page.getPages());
            respIPage.setSize(page.getSize());
            respIPage.setTotal(page.getTotal());
            respIPage.setCurrent(page.getCurrent());
            if (Objects.nonNull(page.getRecords())) {
                List<R> resp = BeanUtil.copyToList(page.getRecords(), clazz);
                respIPage.setRecords(resp);
            }
        }
        return respIPage;
    }

    public static <R> List<R> convertList(List list, Class<R> clazz) {
        return BeanUtil.copyToList(list, clazz);
    }

    public static <R> R convert(Object o, Class<R> clazz) {
        return BeanUtil.toBean(o, clazz);
    }

}
