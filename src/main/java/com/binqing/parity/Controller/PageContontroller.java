package com.binqing.parity.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PageContontroller {
    @RequestMapping("/hello")
    public String hello(){
        return "hello";
    }

    @RequestMapping("/search")
    public ModelAndView searchTest(@RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "page", required = false) String page,
                                   @RequestParam(value = "sort", required = false) String sort) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name",name);
        modelAndView.addObject("page",page);
        modelAndView.addObject("sort",sort);
        modelAndView.setViewName("search");
        return modelAndView;
    }


}
