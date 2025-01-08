package com.example.everychat.aop;

import com.example.everychat.util.ClientUtil;
import com.example.everychat.annotation.ClientIp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;

@Aspect
@Slf4j
@Component
public class ClientIpAop {

    @Around("execution(* *(..))")
    public Object injectClientIp(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.warn("No request context available.");
            return joinPoint.proceed(args);
        }

        HttpServletRequest request = attributes.getRequest();
        String clientIp = ClientUtil.getIp(request);

        log.info("clientIp : {}", clientIp);

        for (Object arg : args) {
            if (arg == null) continue;

            Field[] fields = arg.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(ClientIp.class)) {
                    field.setAccessible(true);
                    field.set(arg, clientIp);
                }
            }
        }

        return joinPoint.proceed(args);
    }
}
