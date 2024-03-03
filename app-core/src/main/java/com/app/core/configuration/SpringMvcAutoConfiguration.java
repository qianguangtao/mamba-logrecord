package com.app.core.configuration;


import com.app.core.mark.AppConfiguration;
import com.app.core.mvc.serialization.argument.EncryptLongPathVariableMethodArgumentResolver;
import com.app.core.mvc.serialization.argument.EncryptLongRequestParamMethodArgumentResolver;
import com.app.core.mvc.serialization.argument.GetRequestBodyMethodArgumentResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author qiangt
 * @since 2023/6/30 19:42
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SpringMvcAutoConfiguration implements WebMvcConfigurer, AppConfiguration {

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new EncryptLongPathVariableMethodArgumentResolver());
        resolvers.add(new EncryptLongRequestParamMethodArgumentResolver());
        resolvers.add(new GetRequestBodyMethodArgumentResolver());
    }

}
