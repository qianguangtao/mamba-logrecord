package com.app.core.security;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.app.core.mvc.result.Code;
import com.app.kit.WebUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/1/31 8:07
 * @description: 接口权限控制切面
 */
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAop {
    private static final String[] methods = {
            RequestMethod.GET.name(),
            RequestMethod.POST.name(),
            RequestMethod.PUT.name(),
            RequestMethod.DELETE.name()
    };
    private final PermissionProperties permissonProperties;

    @Before("execution(* com.app..*.controller..*.*(..))")
    public void beforeControllerMethodExecution(JoinPoint joinPoint) throws NoSuchMethodException {
        String httpMethod = WebUtil.getRequest().getMethod();
        if (!ArrayUtil.contains(methods, httpMethod)) {
            return;
        }
        if (!permissonProperties.isEnabled()) {
            return;
        }
        Class<?> targetClass = joinPoint.getTarget().getClass();
        RequestMapping classRequestMapping = targetClass.getAnnotation(RequestMapping.class);
        // 获取controller类上的请求url前缀
        String classUrl = "";
        if (ObjectUtil.isNotNull(classRequestMapping)) {
            classUrl = formatUrl(classRequestMapping.value());
        }

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = targetClass.getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        String methodUrl = getMethodUrl(method);
        String url = classUrl + methodUrl;
        if (StrUtil.isBlank(url)) {
            return;
        }
        if (checkExcludePermission(url)) {
            return;
        }
        // 校验当前登录人是否有当前接口的访问权限
        String permissionUrl = httpMethod + "@" + url;
        // TODO 获取当前登录人菜单下的api url
        List<String> apiList = Collections.emptyList();
        if (!apiList.contains(permissionUrl)) {
            throw Code.A00028.toCodeException();
        }
    }

    /**
     * 格式化url，以/开头，删除结尾的/
     *
     * @param urls
     * @return
     */
    private String formatUrl(String[] urls) {
        if (ObjectUtil.isEmpty(urls)) {
            return "";
        }
        String url = urls[0];
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    /**
     * 获取controller方法上配置的url
     *
     * @param method
     * @return
     */
    private String getMethodUrl(Method method) {
        String url = "";
        RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
        if (ObjectUtil.isNotNull(methodRequestMapping)) {
            return formatUrl(methodRequestMapping.value());
        }
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (ObjectUtil.isNotNull(postMapping)) {
            return formatUrl(postMapping.value());
        }
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (ObjectUtil.isNotNull(putMapping)) {
            return formatUrl(putMapping.value());
        }
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (ObjectUtil.isNotNull(getMapping)) {
            return formatUrl(getMapping.value());
        }
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (ObjectUtil.isNotNull(deleteMapping)) {
            return formatUrl(deleteMapping.value());
        }
        return url;
    }

    /**
     * 校验当前请求的接口是否排除，不控制权限
     *
     * @param url
     * @return
     */
    private boolean checkExcludePermission(String url) {
        boolean isExcluded = false;
        if (CollectionUtil.isNotEmpty(permissonProperties.getWhiteList())) {
            for (String excludeUrl : permissonProperties.getWhiteList()) {
                if (url.startsWith(excludeUrl.replaceAll("\\*", ""))) {
                    isExcluded = true;
                    break;
                }
            }
        }
        return isExcluded;
    }
}
