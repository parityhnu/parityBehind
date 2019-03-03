package com.binqing.parity.Controller;

import com.binqing.parity.Model.GoodsListModel;
import com.binqing.parity.Model.GoodsListModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class PageContontroller {
    @RequestMapping("/hello")
    public String hello(){
        return "hello";
    }

    @RequestMapping("/search")
    public ModelAndView search(@ModelAttribute("goodsList")List<GoodsListModel> goodsListModelList) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goodsList", goodsListModelList);
        modelAndView.setViewName("search");
        return modelAndView;
    }


}
