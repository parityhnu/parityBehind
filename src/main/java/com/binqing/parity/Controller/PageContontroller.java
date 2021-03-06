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
import java.util.List;

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
    public String signout(HttpServletRequest request, @RequestParam(value = "href", required = false) String href) throws UnsupportedEncodingException {
        HttpSession session = request.getSession();
        session.setAttribute("user", null);
        session.setAttribute("name", null);
        session.setAttribute("ph", null);
        String result = "";
        if (href == null || "".equals(href)) {
            result = "redirect:/hello";
        }else {
            href = href.replace('_','&');
            result = "redirect:" + href;
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

    @RequestMapping("/detail")
    public ModelAndView detail(@RequestParam(value = "ids", required = false) List<String> ids,
                               @RequestParam(value = "index", required = false) String index,
                               @RequestParam String name,
                               @RequestParam String sort) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("ids", ids);
        if (index == null || "".equals(index)) {
            index = "1";
        }
        modelAndView.addObject("index", index);
        modelAndView.addObject("name", name);
        modelAndView.addObject("sort", sort);
        modelAndView.setViewName("detail");
        return modelAndView;
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

    @RequestMapping("/favorite")
    public ModelAndView favorite(HttpServletRequest request,
                                 @RequestParam(value = "page", required = false) String page) {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("href", "/favorite");
            modelAndView.setViewName("login");
            return modelAndView;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("page",page);
        modelAndView.setViewName("favorite");
        return modelAndView;
    }

    @RequestMapping("/admin/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("adminlogin");
        return modelAndView;
    }

    @RequestMapping("/admin/hello")
    public ModelAndView adminHello() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin");
        return modelAndView;
    }

    @RequestMapping("/admin/crawler")
    public ModelAndView adminPythonState() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin");
        return modelAndView;
    }

    @RequestMapping("/admin/admin")
    public ModelAndView adminAdmin(@RequestParam(value = "index", required = false) String index) {
        ModelAndView modelAndView = new ModelAndView();
        if (index == null || "".equals(index)) {
            index = "1";
        }
        modelAndView.addObject("index", index);
        modelAndView.setViewName("adminadmin");
        return modelAndView;
    }

    @RequestMapping("/admin/user")
    public ModelAndView adminUser(@RequestParam(value = "index", required = false) String index) {
        ModelAndView modelAndView = new ModelAndView();
        if (index == null || "".equals(index)) {
            index = "1";
        }
        modelAndView.addObject("index", index);
        modelAndView.setViewName("adminuser");
        return modelAndView;
    }

    @RequestMapping("/admin/information")
    public ModelAndView adminInfo() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admininfo");
        return modelAndView;
    }

    @RequestMapping("/admin/userdetail")
    public ModelAndView userDetail(@RequestParam String user) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("adminuserdetail");
        return modelAndView;
    }

    @RequestMapping("/admin/admindetail")
    public ModelAndView adminDetail(@RequestParam String user) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("adminadmindetail");
        return modelAndView;
    }


}
