package com.example.onlinestore.web.controllers;

import org.springframework.web.servlet.ModelAndView;

public abstract class BaseController {

    private final static String REDIRECT_STRING = "redirect:";

    protected ModelAndView view(String viewName, ModelAndView modelAndView) {
        modelAndView.setViewName(viewName);

        return modelAndView;
    }

    protected ModelAndView view(String viewName) {
        return this.view(viewName, new ModelAndView());
    }

    protected ModelAndView redirect(String url) {
        return this.view(REDIRECT_STRING + url);
    }


}
