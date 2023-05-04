package com.jtexplorer.aop.annotation;


import java.lang.annotation.*;


/**
 * 基于session的日志注解
 * @author bin.xie
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CatchException {

    String client() default "";

    String remark();

    boolean sysLog () default true;

    boolean errorRecord () default true;

    String type() default "";

    String method() default "";

    String rank() default "";
}
