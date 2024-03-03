package com.app.core.mvc.serialization.argument;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.*;
import java.util.Map;

/**
 * Annotation which indicates that a method parameter should be bound to a web
 * request parameter.
 * <p>Supported for annotated handler methods in Spring MVC and Spring WebFlux
 * as follows:
 * <ul>
 * <li>In Spring MVC, "request parameters" map to query parameters, form data,
 * and parts in multipart requests. This is because the Servlet API combines
 * query parameters and form data into a single map called "parameters", and
 * that includes automatic parsing of the request body.
 * <li>In Spring WebFlux, "request parameters" map to query parameters only.
 * To work with all 3, query, form data, and multipart data, you can use data
 * binding to a command object annotated with {@link org.springframework.web.bind.annotation.ModelAttribute}.
 * </ul>
 * <p>If the method parameter type is {@link Map} and a request parameter name
 * is specified, then the request parameter value is converted to a {@link Map}
 * assuming an appropriate conversion strategy is available.
 * <p>If the method parameter is {@link Map Map&lt;String, String&gt;} or
 * {@link org.springframework.util.MultiValueMap MultiValueMap&lt;String, String&gt;}
 * and a parameter name is not specified, then the map parameter is populated
 * with all request parameter names and values.
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @see org.springframework.web.bind.annotation.RequestMapping
 * @see org.springframework.web.bind.annotation.RequestHeader
 * @see org.springframework.web.bind.annotation.CookieValue
 * @since 2.5
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptRequestParam {

    /**
     * Alias for {@link #name}.
     */
    @AliasFor("name")
    String value() default "";

    /**
     * The name of the request parameter to bind to.
     * @since 4.2
     */
    @AliasFor("value")
    String name() default "";

    /**
     * Whether the parameter is required.
     * <p>Defaults to {@code true}, leading to an exception being thrown
     * if the parameter is missing in the request. Switch this to
     * {@code false} if you prefer a {@code null} value if the parameter is
     * not present in the request.
     * <p>Alternatively, provide a {@link #defaultValue}, which implicitly
     * sets this flag to {@code false}.
     */
    boolean required() default true;

    /**
     * The default value to use as a fallback when the request parameter is
     * not provided or has an empty value.
     * <p>Supplying a default value implicitly sets {@link #required} to
     * {@code false}.
     */
    String defaultValue() default ValueConstants.DEFAULT_NONE;

    /**
     * 数据类型 String.class,Long.class
     * @return Class
     */
    Class<?> clazz() default Long.class;
}
