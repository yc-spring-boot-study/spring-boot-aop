package com.chuan.aop.study.springbootaop.aspect;

import com.chuan.aop.study.springbootaop.annotation.MyAnnotation;
import com.chuan.aop.study.springbootaop.annotation.SemaphoreCircuitBreaker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 * author:曲终、人散
 * Date:2019/11/7 21:32
 */
@Component
@Aspect
public class MyAspect {

    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    private Semaphore semaphore;

    /**
     * 限流
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around(value = "@annotation(com.chuan.aop.study.springbootaop.annotation.SemaphoreCircuitBreaker)")
    public Object aroundSemaphore(ProceedingJoinPoint point) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        SemaphoreCircuitBreaker semaphoreCircuitBreaker = method.getAnnotation(SemaphoreCircuitBreaker.class);
        int value = semaphoreCircuitBreaker.value();

        if (null == semaphore) {
            semaphore = new Semaphore(value);
        }

        Object returnValue = null;
        Object[] args = point.getArgs();
        try {
            if (semaphore.tryAcquire()) {
                returnValue = point.proceed(args);
                Thread.sleep(1000);
            }else{
                System.out.println("==============");
                returnValue = errorContent("");
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }finally {
            semaphore.release();
        }
        return returnValue;
    }


    /**
     * 超时控制
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around(value = "@annotation(com.chuan.aop.study.springbootaop.annotation.MyAnnotation)")
    public Object aroundExec(ProceedingJoinPoint point) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        MyAnnotation myAnnotation = method.getAnnotation(MyAnnotation.class);
        long timeOut = myAnnotation.timeOut();

        Object[] args = point.getArgs();

        Future<Object> future = executorService.submit(() -> {
            Object returnValue = null;
            try {
                returnValue = point.proceed(args);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return returnValue;
        });

        Object returnValue = null;
        try {
            returnValue = future.get(timeOut, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            returnValue = errorContent("");
        }
        return returnValue;
    }

    public String errorContent(String message) {
        return "Fault";
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }

}
