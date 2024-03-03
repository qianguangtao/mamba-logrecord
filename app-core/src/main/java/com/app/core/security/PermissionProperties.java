package com.app.core.security;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/1/31 10:44
 * @description: 权限配置类
 */
@Getter
@Setter
@Slf4j
@Component
@ConfigurationProperties(prefix = "app.permission")
public class PermissionProperties {
    /**
     * 是否启用权限控制
     */
    private boolean enabled;
    /**
     * 排除不需要控制的接口，可以使用/user/**通配
     */
    private List<String> whiteList;
}
