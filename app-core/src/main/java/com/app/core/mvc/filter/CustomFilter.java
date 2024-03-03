package com.app.core.mvc.filter;

import com.app.core.mvc.servlet.CustomHttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author qiangt
 * @since 2020-08-06
 */
@Component
@WebFilter(urlPatterns = "/*", filterName = "customFilter")
@Order(0)
public class CustomFilter implements Filter {

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final String contentType = request.getContentType();
        // JSON请求时将Request替换成自定义Request
        if (StringUtils.isNotEmpty(contentType) && contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
            chain.doFilter(
                    new CustomHttpServletRequest((HttpServletRequest) request),
                    response
            );
        } else {
            chain.doFilter(request, response);
        }
    }

}
