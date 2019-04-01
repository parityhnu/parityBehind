package com.binqing.parity.Controller;

import com.binqing.parity.Model.GoodsListModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
public class PageContontroller {
    @RequestMapping("/hello")
    public String hello(){
        return "hello";
    }


    @RequestMapping("/login")
    public ModelAndView login(@RequestParam(value = "href", required = false) String href) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("href",href);
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping("/signout")
    public String signout(HttpServletRequest request, @RequestParam(value = "href", required = false) String href,
                          @RequestParam(value = "name", required = false) String name,
                          @RequestParam(value = "page", required = false) String page,
                          @RequestParam(value = "sort", required = false) String sort) throws UnsupportedEncodingException {
        HttpSession session = request.getSession();
        session.setAttribute("user", null);
        session.setAttribute("name", null);
        session.setAttribute("ph", null);
        String result = "";
        if (href == null || "".equals(href)) {
            result = "redirect:/hello";
        }
        if ("/search".equals(href)) {
            StringBuilder builder = new StringBuilder("redirect:/search");
            builder.append("?name=");
            builder.append(URLEncoder.encode(name,"UTF-8"));
            if (page == null || "".equals(page)) {
                page = "0";
            }
            builder.append("&page=");
            builder.append(page);
            if (page == null || "".equals(page)) {
                sort = "0";
            }
            builder.append("&sort=");
            builder.append(sort);
            result = builder.toString();
        }
        return result;
    }

    @RequestMapping("/modify")
    public ModelAndView modify(@RequestParam(value = "href", required = false) String href) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("href",href);
        modelAndView.setViewName("modify");
        return modelAndView;
    }

    @RequestMapping("/forgetPassword")
    public ModelAndView forgetPassword(@RequestParam(value = "href", required = false) String href) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("href",href);
        modelAndView.setViewName("forgetPassword");
        return modelAndView;
    }

    @RequestMapping("/search")
    public ModelAndView searchTest(@RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "page", required = false) String page,
                                   @RequestParam(value = "sort", required = false) String sort,
                                   @RequestParam(value = "goodsListModel", required = false)GoodsListModel goodsListModel) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name",name);
        modelAndView.addObject("page",page);
        modelAndView.addObject("sort",sort);
        modelAndView.setViewName("search");
        return modelAndView;
    }


}
