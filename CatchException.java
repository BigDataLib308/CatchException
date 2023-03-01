package com.qq.aop.annotation;

import java.lang.annotation.*;

/**
 * @CatchException注解属性
 * @author bin.xie
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CatchException {

    String client() default "";

    String remark();

    boolean sysLog() default true;

    boolean errorRecord() default true;

    String type() default "";

    String method() default "";

    String rank() default "";
}
