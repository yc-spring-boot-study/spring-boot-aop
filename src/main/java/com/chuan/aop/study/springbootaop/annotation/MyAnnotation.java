package com.chuan.aop.study.springbootaop.annotation;

import java.lang.annotation.*;

/**
 * author:曲终、人散
 * Date:2019/11/7 21:28
 */

@Target({ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAnnotation {
    /**
     * 超时时间
     * @return
     */
    long timeOut();
}
