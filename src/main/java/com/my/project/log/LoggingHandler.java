package com.my.project.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequestWrapper;

@Aspect
@Component
@Slf4j
public class LoggingHandler {


    @Pointcut("within(com.my.project.service..*)")
    public void service(){
        // for all service invocations
    }

    @Pointcut("within(com.my.project.dao..*)")
    public void dao(){
        // for all dao invocations
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void controllerInvocation(){
        // for all controller invocations
    }

@Before("controllerInvocation()") public void logBeforeController(JoinPoint jp){
    Object[] args = jp.getArgs();
    for (Object o: args
         ) {
        if(o instanceof HttpServletRequestWrapper){
            HttpServletRequestWrapper o1 = (HttpServletRequestWrapper) o;
            log.info(o1.getRequest().getContentType());
        }else{
            log.error(o.toString());
        }

    }
}

}
