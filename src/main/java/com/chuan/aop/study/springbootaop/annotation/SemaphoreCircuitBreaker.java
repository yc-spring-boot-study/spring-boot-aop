package com.chuan.aop.study.springbootaop.annotation;

import java.lang.annotation.*;

/**
 * author:曲终、人散
 * Date:2019/11/7 22:11
 */
@Target({ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SemaphoreCircuitBreaker {
    /**
     * 限流个数
     * @return
     */
    int value();
}
