package com.example.onlinestore.web.controllers;

import org.springframework.web.servlet.ModelAndView;

public abstract class BaseController {

    private final static String REDIRECT_STRING = "redirect:";

    ModelAndView view(String viewName, ModelAndView modelAndView) {
        modelAndView.setViewName(viewName);
        return modelAndView;
    }

    ModelAndView view(String viewName) {
        return this.view(viewName, new ModelAndView());
    }

    ModelAndView redirect(String url) {
        return this.view(REDIRECT_STRING + url);
    }


}
