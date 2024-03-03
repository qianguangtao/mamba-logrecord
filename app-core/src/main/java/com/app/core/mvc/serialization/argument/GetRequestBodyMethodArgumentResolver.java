package com.app.core.mvc.serialization.argument;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;

import java.lang.reflect.Parameter;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/17 16:39
 * @description: Get请求使用对象接收，主要针对Restful分页查询参数过多，@RequestParam操作不方便
 */
@Slf4j
public class GetRequestBodyMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {
    @Override
    protected NamedValueInfo createNamedValueInfo(final MethodParameter parameter) {
        final GetRequestBody ann = parameter.getParameterAnnotation(GetRequestBody.class);
        return ann != null ? new NamedValueInfo(ann.name(), ann.required(), null) : new NamedValueInfo("", false, null);
    }

    @Override
    protected Object resolveName(final String name, final MethodParameter parameter, final NativeWebRequest request) throws Exception {
        final Parameter param = parameter.getParameter();
        final Class<?> type = param.getType();
        JSONObject jsonObject = new JSONObject();
        request.getParameterMap().forEach((k, v) -> {
            try {
                Object fieldValue = v.length > 1 ? v : v[0];
                jsonObject.put(k, fieldValue);
            } catch (final Exception e) {
                log.error("GetBodyResolve NoSuchFieldException" + e);
            }
        });
        ObjectMapper om = new ObjectMapper();
        return om.readValue(jsonObject.toJSONString(), type);
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(GetRequestBody.class);
    }
}
