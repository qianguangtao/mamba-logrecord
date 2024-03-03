package com.app.core.security;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.app.core.configuration.CommonProperties;
import com.app.kit.SpringKit;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/17 13:23
 * @description: 当前登录用户的操作类
 */
public interface ResourceOwnerContext {
    /**
     * 获取当前用户（redis或jwt）
     *
     * @return
     */
    Principal getPrincipal();

    /**
     * 从request中获取当前用户（redis或jwt）
     *
     * @return
     */
    Principal getPrincipal(HttpServletRequest request);

    default String getUserId(HttpServletRequest request) {
        if (ObjectUtil.isNull(request)) {
            return null;
        }
        String token = request.getHeader(SpringKit.getBean(CommonProperties.class).getTokenHeader());
        if (StrUtil.isNotBlank(token)) {
            return Base64.decodeStr(token).split("@")[0];
        }
        return null;
    }

    /**
     * 生成token
     *
     * @param principal 从数据库查询的用户
     * @return
     */
    void setPrincipal(Principal principal);
}
