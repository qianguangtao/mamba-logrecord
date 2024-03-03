package com.app.core.mvc.serialization.argument;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author qiangt
 * @date 2023/9/1
 * @apiNote
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GetRequestBody {

    /**
     * Alias for {@link #name}.
     */
    @AliasFor("name")
    String value() default "";

    /**
     * The name of the path variable to bind to.
     * @since 4.3.3
     */
    @AliasFor("value")
    String name() default "";

    /**
     * Whether the path variable is required.
     * <p>Defaults to {@code true}, leading to an exception being thrown if the path
     * variable is missing in the incoming request. Switch this to {@code false} if
     * you prefer a {@code null} or Java 8 {@code java.util.Optional} in this case.
     * e.g. on a {@code ModelAttribute} method which serves for different requests.
     * @since 4.3.3
     */
    boolean required() default true;

    /**
     * 数据类型 String.class,Long.class
     * @return Class
     */
    Class<?> clazz() default Long.class;

}
