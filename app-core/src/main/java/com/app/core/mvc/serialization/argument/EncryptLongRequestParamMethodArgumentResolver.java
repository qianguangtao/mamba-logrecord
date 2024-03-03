package com.app.core.mvc.serialization.argument;

import com.app.core.encrypt.processor.argument.PrimaryEncryptProcessor;
import com.app.core.mvc.result.Code;
import com.app.kit.SpringKit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver;
import org.springframework.web.method.support.UriComponentsContributor;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.beans.PropertyEditor;
import java.io.Serializable;
import java.util.*;

/**
 * Resolves method arguments annotated with @{@link org.springframework.web.bind.annotation.RequestParam}, arguments of
 * type {@link MultipartFile} in conjunction with Spring's {@link MultipartResolver}
 * abstraction, and arguments of type {@code javax.servlet.http.Part} in conjunction
 * with Servlet 3.0 multipart requests. This resolver can also be created in default
 * resolution mode in which simple types (int, long, etc.) not annotated with
 * {@link org.springframework.web.bind.annotation.RequestParam @RequestParam} are also treated as request parameters with
 * the parameter name derived from the argument name.
 * <p>If the method parameter type is {@link Map}, the name specified in the
 * annotation is used to resolve the request parameter String value. The value is
 * then converted to a {@link Map} via type conversion assuming a suitable
 * {@link Converter} or {@link PropertyEditor} has been registered.
 * Or if a request parameter name is not specified the
 * {@link RequestParamMapMethodArgumentResolver} is used instead to provide
 * access to all request parameters in the form of a map.
 * <p>A {@link WebDataBinder} is invoked to apply type conversion to resolved request
 * header values that don't yet match the method parameter type.
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @see RequestParamMapMethodArgumentResolver
 * @since 3.1
 */
public class EncryptLongRequestParamMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver
        implements UriComponentsContributor {

    private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);

    private final boolean useDefaultResolution;
    private PrimaryEncryptProcessor primaryEncryptProcessor;

    public EncryptLongRequestParamMethodArgumentResolver() {
        this.useDefaultResolution = false;
    }

    /**
     * Create a new {@link EncryptLongRequestParamMethodArgumentResolver} instance.
     * @param useDefaultResolution in default resolution mode a method argument
     *                             that is a simple type, as defined in {@link BeanUtils#isSimpleProperty},
     *                             is treated as a request parameter even if it isn't annotated, the
     *                             request parameter name is derived from the method parameter name.
     */
    public EncryptLongRequestParamMethodArgumentResolver(final boolean useDefaultResolution) {
        this.useDefaultResolution = useDefaultResolution;
    }

    /**
     * Create a new {@link EncryptLongRequestParamMethodArgumentResolver} instance.
     * @param beanFactory          a bean factory used for resolving  ${...} placeholder
     *                             and #{...} SpEL expressions in default values, or {@code null} if default
     *                             values are not expected to contain expressions
     * @param useDefaultResolution in default resolution mode a method argument
     *                             that is a simple type, as defined in {@link BeanUtils#isSimpleProperty},
     *                             is treated as a request parameter even if it isn't annotated, the
     *                             request parameter name is derived from the method parameter name.
     */
    public EncryptLongRequestParamMethodArgumentResolver(@Nullable final ConfigurableBeanFactory beanFactory,
                                                         final boolean useDefaultResolution) {

        super(beanFactory);
        this.useDefaultResolution = useDefaultResolution;
    }

    /**
     * Supports the following:
     * <ul>
     * <li>@RequestParam-annotated method arguments.
     * This excludes {@link Map} params where the annotation does not specify a name.
     * See {@link RequestParamMapMethodArgumentResolver} instead for such params.
     * <li>Arguments of type {@link MultipartFile} unless annotated with @{@link org.springframework.web.bind.annotation.RequestPart}.
     * <li>Arguments of type {@code Part} unless annotated with @{@link org.springframework.web.bind.annotation.RequestPart}.
     * <li>In default resolution mode, simple type arguments even if not with @{@link org.springframework.web.bind.annotation.RequestParam}.
     * </ul>
     */
    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(DecryptRequestParam.class)) {
            final DecryptRequestParam requestParam = parameter.getParameterAnnotation(DecryptRequestParam.class);
            if (Objects.isNull(requestParam)) {
                return false;
            }
            if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
                return org.springframework.util.StringUtils.hasText(requestParam.name()) && requestParam.clazz().isAssignableFrom(Long.class);
            }
            return requestParam.clazz().isAssignableFrom(Long.class);
        }
        //		else {
        //			if (parameter.hasParameterAnnotation(RequestPart.class)) {
        //				return false;
        //			}
        //			parameter = parameter.nestedIfOptional();
        //			if (MultipartResolutionDelegate.isMultipartArgument(parameter)) {
        //				return true;
        //			}
        //			else if (this.useDefaultResolution) {
        //				return BeanUtils.isSimpleProperty(parameter.getNestedParameterType());
        //			}
        //			else {
        //				return false;
        //			}
        //		}
        return false;
    }

    @Override
    public void contributeMethodArgument(MethodParameter parameter, @Nullable Object value,
                                         final UriComponentsBuilder builder, final Map<String, Object> uriVariables, final ConversionService conversionService) {

        final Class<?> paramType = parameter.getNestedParameterType();
        if (Map.class.isAssignableFrom(paramType) || MultipartFile.class == paramType || Part.class == paramType) {
            return;
        }

        final DecryptRequestParam requestParam = parameter.getParameterAnnotation(DecryptRequestParam.class);
        final String name = (requestParam != null && org.springframework.util.StringUtils.hasLength(requestParam.name()) ?
                requestParam.name() : parameter.getParameterName());
        Assert.state(name != null, "Unresolvable parameter name");

        parameter = parameter.nestedIfOptional();
        if (value instanceof Optional) {
            value = ((Optional<?>) value).orElse(null);
        }

        if (value == null) {
            if (requestParam != null &&
                    (!requestParam.required() || !requestParam.defaultValue().equals(ValueConstants.DEFAULT_NONE))) {
                return;
            }
            builder.queryParam(name);
        } else if (value instanceof Collection) {
            for (Object element : (Collection<?>) value) {
                element = this.formatUriValue(conversionService, TypeDescriptor.nested(parameter, 1), element);
                builder.queryParam(name, element);
            }
        } else {
            builder.queryParam(name, this.formatUriValue(conversionService, new TypeDescriptor(parameter), value));
        }
    }

    @Nullable
    protected String formatUriValue(
            @Nullable final ConversionService cs, @Nullable final TypeDescriptor sourceType, @Nullable final Object value) {

        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String) value;
        } else if (cs != null) {
            return (String) cs.convert(value, sourceType, STRING_TYPE_DESCRIPTOR);
        } else {
            return value.toString();
        }
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(final MethodParameter parameter) {
        final DecryptRequestParam ann = parameter.getParameterAnnotation(DecryptRequestParam.class);
        return (ann != null ? new RequestParamNamedValueInfo(ann) : new RequestParamNamedValueInfo());
    }

    @Override
    @Nullable
    protected Object resolveName(final String name, final MethodParameter parameter, final NativeWebRequest request) throws Exception {
        final HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);

        if (servletRequest != null) {
            final Object mpArg = MultipartResolutionDelegate.resolveMultipartArgument(name, parameter, servletRequest);
            if (mpArg != MultipartResolutionDelegate.UNRESOLVABLE) {
                return mpArg;
            }
        }

        Object arg = null;
        final MultipartRequest multipartRequest = request.getNativeRequest(MultipartRequest.class);
        if (multipartRequest != null) {
            final List<MultipartFile> files = multipartRequest.getFiles(name);
            if (!files.isEmpty()) {
                arg = (files.size() == 1 ? files.get(0) : files);
            }
        }
        if (arg == null) {
            final String[] paramValues = request.getParameterValues(name);
            if (paramValues != null) {
                if (paramValues.length == 1) {
                    if (StringUtils.isBlank(paramValues[0])) {
                        return null;
                    }
                    final Serializable primaryKey = this.getPrimaryEncryptHandler().decode(paramValues[0]);
                    if (Objects.isNull(primaryKey)) {
                        throw Code.A00027.toCodeException();
                    }
                    return primaryKey;
                }

                return Arrays.stream(paramValues)
                        .filter(StringUtils::isNotEmpty)
                        .map(s -> this.getPrimaryEncryptHandler().decode(paramValues[0]))
                        .toArray(Serializable[]::new);
            }
        }
        return arg;
    }

    // protected void handleMissingValueAfterConversion(
    //         String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
    //
    //     handleMissingValueInternal(name, parameter, request, true);
    // }

    @Override
    protected void handleMissingValue(final String name, final MethodParameter parameter, final NativeWebRequest request)
            throws Exception {

        this.handleMissingValueInternal(name, parameter, request, false);
    }

    protected void handleMissingValueInternal(
            final String name, final MethodParameter parameter, final NativeWebRequest request, final boolean missingAfterConversion)
            throws Exception {

        final HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        if (MultipartResolutionDelegate.isMultipartArgument(parameter)) {
            if (servletRequest == null || !MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
                throw new MultipartException("Current request is not a multipart request");
            } else {
                throw new MissingServletRequestPartException(name);
            }
        } else {
            throw new MissingServletRequestParameterException(name, parameter.getNestedParameterType().getSimpleName());
        }
    }

    private PrimaryEncryptProcessor getPrimaryEncryptHandler() {
        if (Objects.isNull(this.primaryEncryptProcessor)) {
            this.primaryEncryptProcessor = SpringKit.getBean(PrimaryEncryptProcessor.class);
        }
        return this.primaryEncryptProcessor;
    }

    private static class RequestParamNamedValueInfo extends NamedValueInfo {

        public RequestParamNamedValueInfo() {
            super("", false, ValueConstants.DEFAULT_NONE);
        }

        public RequestParamNamedValueInfo(final DecryptRequestParam annotation) {
            super(annotation.name(), annotation.required(), annotation.defaultValue());
        }
    }

}
