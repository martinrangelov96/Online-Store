package com.example.onlinestore.web.interceptors;

import com.example.onlinestore.web.annotations.PageTitle;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TitleInterceptor extends HandlerInterceptorAdapter {

    private final static String TITLE_CONST = "Online Store";
    private final static String TITLE_ATTRIBUTE_NAME = "title";

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        String title = TITLE_CONST;

        if (handler instanceof HandlerMethod) {
            PageTitle methodAnnotation = ((HandlerMethod) handler).getMethodAnnotation(PageTitle.class);

            if (methodAnnotation != null) {
                modelAndView.addObject(TITLE_ATTRIBUTE_NAME, title + " - " +methodAnnotation.value());
            }
        }
    }
}
