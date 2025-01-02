package com.example.everychat.argumentresolver;

import com.example.everychat.annotation.ClientIp;
import com.example.everychat.util.ClientUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Field;

@Component
public class ClientIpResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() != null && parameter.getParameterType() != String.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String clientIp = ClientUtil.getIp(request);

        Object instance = mavContainer.getModel().get(parameter.getParameterName());

        if (instance != null) {
            for (Field field : instance.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(ClientIp.class) && field.getType().equals(String.class)) {
                    field.setAccessible(true);
                    field.set(instance, clientIp);
                }
            }
        }

        return instance;
    }
}