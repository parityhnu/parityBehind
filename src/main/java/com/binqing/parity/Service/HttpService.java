package com.binqing.parity.Service;

import com.binqing.parity.Model.GoodsListModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class HttpService {

    public static GoodsListModel getGoods(String name, String page, String sort) {
        if (name == null || "".equals(name)) {
            return null;
        }
        if (page == null || "".equals(page)) {
            page = "0";
        }
        if (sort == null || "".equals(sort)) {
            sort = "0";
        }
        RestTemplate restTemplate=new RestTemplate();
        String url="http://localhost:9090/ip/search?name="+name+"&page="+page+"&sort="+sort;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<GoodsListModel> entity = new HttpEntity<GoodsListModel>(headers);
        System.out.println(restTemplate.exchange(url, HttpMethod.GET, entity, GoodsListModel.class));
        GoodsListModel goodsListModel = restTemplate.exchange(url, HttpMethod.GET, entity, GoodsListModel.class).getBody();
        return goodsListModel;
    }
}
