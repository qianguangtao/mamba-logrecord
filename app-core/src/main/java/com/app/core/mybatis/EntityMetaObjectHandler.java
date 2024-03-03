package com.app.core.mybatis;

import com.app.core.security.ResourceOwnerContext;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/16 15:29
 * @description: mybatis plus 自动填充
 */
@Component
@RequiredArgsConstructor
public class EntityMetaObjectHandler implements MetaObjectHandler {
    /**
     * 填充创建人属性名
     */
    private static final String FIELD_CREATEBY = "createBy";
    /**
     * 填充创建时间
     */
    private static final String FIELD_CREATE_TIME = "createTime";
    /**
     * 填充修改人属性名
     */
    private static final String FIELD_UPDATEBY = "updateBy";
    /**
     * 填充修改时间
     */
    private static final String FIELD_UPDATE_TIME = "updateTime";
    /**
     * 填充逻辑删除属性名
     */
    private static final String FIELD_DELETED = "deleted";
    private final ResourceOwnerContext resourceOwnerContext;


    @Override
    public void insertFill(final MetaObject metaObject) {
        fillCreateBy(metaObject);
        fillCreateTime(metaObject);
        fillUpdateBy(metaObject);
        fillUpdateTime(metaObject);
    }

    @Override
    public void updateFill(final MetaObject metaObject) {
        fillUpdateBy(metaObject);
        fillUpdateTime(metaObject);
    }

    private void fillCreateBy(MetaObject metaObject) {
        if (metaObject.hasGetter(FIELD_CREATEBY)) {
            this.setFieldValByName(FIELD_CREATEBY, this.resourceOwnerContext.getPrincipal().getPrimaryKey(), metaObject);
        }
    }

    private void fillCreateTime(MetaObject metaObject) {
        if (metaObject.hasGetter(FIELD_CREATE_TIME)) {
            this.setFieldValByName(FIELD_CREATE_TIME, new Date(), metaObject);
        }
    }

    private void fillUpdateBy(MetaObject metaObject) {
        if (metaObject.hasGetter(FIELD_UPDATEBY)) {
            this.setFieldValByName(FIELD_UPDATEBY, this.resourceOwnerContext.getPrincipal().getPrimaryKey(), metaObject);
        }
    }

    private void fillUpdateTime(MetaObject metaObject) {
        if (metaObject.hasGetter(FIELD_UPDATE_TIME)) {
            this.setFieldValByName(FIELD_UPDATE_TIME, new Date(), metaObject);
        }
    }

}
