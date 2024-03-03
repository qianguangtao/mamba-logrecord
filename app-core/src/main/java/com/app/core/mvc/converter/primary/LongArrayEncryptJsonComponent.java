package com.app.core.mvc.converter.primary;


import com.app.core.encrypt.processor.argument.PrimaryEncryptProcessor;
import com.app.kit.SpringKit;
import com.fasterxml.jackson.databind.util.StdConverter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class LongArrayEncryptJsonComponent {

    private static PrimaryEncryptProcessor primaryEncryptProcessor;

    private static PrimaryEncryptProcessor getPrimaryEncryptHandler() {
        if (Objects.isNull(primaryEncryptProcessor)) {
            primaryEncryptProcessor = SpringKit.getBean(PrimaryEncryptProcessor.class);
        }
        return primaryEncryptProcessor;
    }

    public static class Serializer extends StdConverter<Long[], String[]> {
        @Override
        public String[] convert(final Long[] value) {
            if (Objects.isNull(value)) {
                return null;
            }
            if (value.length == 0) {
                return new String[0];
            }
            if (log.isDebugEnabled()) {
                log.debug("加密:{}", Stream.of(value).map(Objects::toString).collect(Collectors.joining(",")));
            }
            return Stream.of(value).map(s -> (String) getPrimaryEncryptHandler().encode(s)).toArray(String[]::new);
        }
    }

    public static class Deserializer extends StdConverter<String[], Long[]> {
        @Override
        public Long[] convert(final String[] value) {
            if (Objects.isNull(value)) {
                return null;
            }
            if (value.length == 0) {
                return new Long[0];
            }
            if (log.isDebugEnabled()) {
                log.debug("解密:{}", String.join(",", value));
            }
            return Stream.of(value).map(s -> (Long) getPrimaryEncryptHandler().decode(s)).toArray(Long[]::new);
        }
    }

}
