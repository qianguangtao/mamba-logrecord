package com.app.core.mvc.servlet;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * HttpServletRequest包装类，可实现对请求的body数据重复读取
 * @author qiangt
 * @since 2020-08-06
 */
@Slf4j
public class CustomHttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] body;

    public CustomHttpServletRequest(final HttpServletRequest request) {
        super(request);
        try {
            this.body = IoUtil.readBytes(request.getInputStream());
            log.info("REQUEST === > {}: {}", request.getRequestURI(), new String(this.body, StandardCharsets.UTF_8));
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    public String getBodyString() {
        return new String(this.body, StandardCharsets.UTF_8);
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.body);
        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() <= 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(final ReadListener readListener) {
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

}
