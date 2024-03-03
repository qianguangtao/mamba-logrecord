package com.app.core.security;

import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/17 14:39
 * @description: Default实现当前用户操作
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class DefaultResourceOwnerContext implements ResourceOwnerContext {

    @Override
    public Principal getPrincipal() {
        return Principal.defaultPrincipal();
    }

    @Override
    public Principal getPrincipal(HttpServletRequest request) {
        return Principal.defaultPrincipal();
    }

    @Override
    public void setPrincipal(Principal principal) {
        Assert.notNull(principal);
    }
}
