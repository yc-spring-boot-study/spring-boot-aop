package com.chuan.aop.study.springbootaop.controller;

import com.chuan.aop.study.springbootaop.annotation.MyAnnotation;
import com.chuan.aop.study.springbootaop.annotation.SemaphoreCircuitBreaker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * author:曲终、人散
 * Date:2019/11/7 21:29
 */
@RestController
public class TestController {

    private final static Random random = new Random();

    @SemaphoreCircuitBreaker(value = 1)
    @GetMapping("/hello2")
    public String hello2(@RequestParam String message){
        return doSay2(message);
    }

    @MyAnnotation(timeOut = 100)
    @GetMapping("/hello")
    public String hello(@RequestParam String message){
        return doSay2(message);
    }

    private String doSay2(String message)  {
        // 如果随机时间 大于 100 ，那么触发容错
        int value = random.nextInt(200);
        System.out.println("say2() costs " + value + " ms.");
        // > 100
        try {
            Thread.sleep(value);
        } catch (InterruptedException e) {
        }
        String returnValue = "Say 2 : " + message;
        System.out.println(returnValue);
        return returnValue;
    }
}
