package com.app.core.mvc.serialization.argument;


import com.app.core.encrypt.processor.argument.PrimaryEncryptProcessor;
import com.app.core.mvc.result.Code;
import com.app.kit.SpringKit;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.method.support.UriComponentsContributor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.util.UriComponentsBuilder;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Resolves method arguments annotated with an @{@link PathVariable}.
 * <p>An @{@link PathVariable} is a named value that gets resolved from a URI template variable.
 * It is always required and does not have a default value to fall back on. See the base class
 * {@link AbstractNamedValueMethodArgumentResolver}
 * for more information on how named values are processed.
 * <p>If the method parameter type is {@link Map}, the name specified in the annotation is used
 * to resolve the URI variable String value. The value is then converted to a {@link Map} via
 * type conversion, assuming a suitable {@link Converter} or {@link PropertyEditor} has been
 * registered.
 * <p>A {@link WebDataBinder} is invoked to apply type conversion to resolved path variable
 * values that don't yet match the method parameter type.
 * @author Rossen Stoyanchev
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.1
 */
public class EncryptLongPathVariableMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver
        implements UriComponentsContributor {

    private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);

    private PrimaryEncryptProcessor primaryEncryptProcessor;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        if (!parameter.hasParameterAnnotation(DecryptPathVariable.class)) {
            return false;
        }
        if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
            final DecryptPathVariable pathVariable = parameter.getParameterAnnotation(DecryptPathVariable.class);
            if (Objects.nonNull(pathVariable) && pathVariable.clazz().isAssignableFrom(Long.class)) {
                return StringUtils.hasText(pathVariable.value());
            } else {
                return false;
            }
        }
        final DecryptPathVariable pathVariable = parameter.getParameterAnnotation(DecryptPathVariable.class);
        return Objects.nonNull(pathVariable) && pathVariable.clazz().isAssignableFrom(Long.class);
    }

    @Override
    public void contributeMethodArgument(final MethodParameter parameter, final Object value,
                                         final UriComponentsBuilder builder, final Map<String, Object> uriVariables, final ConversionService conversionService) {

        if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
            return;
        }

        final DecryptPathVariable ann = parameter.getParameterAnnotation(DecryptPathVariable.class);
        final String name = (ann != null && StringUtils.hasLength(ann.value()) ? ann.value() : parameter.getParameterName());
        final String formatted = this.formatUriValue(conversionService, new TypeDescriptor(parameter.nestedIfOptional()), value);
        uriVariables.put(name, formatted);
    }

    @Nullable
    protected String formatUriValue(@Nullable final ConversionService cs, @Nullable final TypeDescriptor sourceType, final Object value) {
        if (value instanceof String) {
            return (String) value;
        } else if (cs != null) {
            return (String) cs.convert(value, sourceType, STRING_TYPE_DESCRIPTOR);
        } else {
            return value.toString();
        }
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(final MethodParameter parameter) {
        final DecryptPathVariable ann = parameter.getParameterAnnotation(DecryptPathVariable.class);
        Assert.state(ann != null, "No PathVariable annotation");
        return new PathVariableNamedValueInfo(ann);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    protected Object resolveName(final String name, final MethodParameter parameter, final NativeWebRequest request) throws Exception {
        final Map<String, String> uriTemplateVars = (Map<String, String>) request.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE,
                RequestAttributes.SCOPE_REQUEST
        );
        if (Objects.isNull(uriTemplateVars)) {
            return null;
        }
        return Stream.of(uriTemplateVars.get(name).split(","))
                .filter(StringUtils::hasText)
                .map(s -> this.getPrimaryEncryptHandler().decode(s))
                .peek(s -> {
                    if (Objects.isNull(s)) {
                        throw Code.A00027.toCodeException();
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    protected void handleMissingValue(final String name, final MethodParameter parameter) throws ServletRequestBindingException {
        throw new MissingPathVariableException(name, parameter);
    }

    @Override
    protected void handleMissingValueAfterConversion(
            final String name, final MethodParameter parameter, final NativeWebRequest request) throws Exception {

        throw new MissingPathVariableException(name, parameter);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void handleResolvedValue(@Nullable final Object arg, final String name, final MethodParameter parameter,
                                       @Nullable final ModelAndViewContainer mavContainer, final NativeWebRequest request) {

        final String key = View.PATH_VARIABLES;
        final int scope = RequestAttributes.SCOPE_REQUEST;
        Map<String, Object> pathVars = (Map<String, Object>) request.getAttribute(key, scope);
        if (pathVars == null) {
            pathVars = new HashMap<>();
            request.setAttribute(key, pathVars, scope);
        }
        pathVars.put(name, arg);
    }

    private PrimaryEncryptProcessor getPrimaryEncryptHandler() {
        if (Objects.isNull(this.primaryEncryptProcessor)) {
            this.primaryEncryptProcessor = SpringKit.getBean(PrimaryEncryptProcessor.class);
        }
        return this.primaryEncryptProcessor;
    }

    private static class PathVariableNamedValueInfo extends NamedValueInfo {

        public PathVariableNamedValueInfo(final DecryptPathVariable annotation) {
            super(annotation.name(), annotation.required(), ValueConstants.DEFAULT_NONE);
        }
    }

}
