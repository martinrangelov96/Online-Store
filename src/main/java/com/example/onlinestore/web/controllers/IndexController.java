package com.example.onlinestore.web.controllers;

import com.example.onlinestore.web.annotations.PageTitle;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import static com.example.onlinestore.constants.Constants.*;

@Controller
public class IndexController extends BaseController {

    @GetMapping("/")
    @PreAuthorize(IS_ANONYMOUS)
    @PageTitle(INDEX_PAGE_PAGE_TITLE)
    public ModelAndView index() {
        return view("index");
    }

}
