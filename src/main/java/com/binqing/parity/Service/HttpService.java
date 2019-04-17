package com.binqing.parity.Service;

import com.binqing.parity.Model.BaseCommentModel;
import com.binqing.parity.Model.CommentReturnModel;
import com.binqing.parity.Model.GoodsListModel;
import org.apache.http.util.TextUtils;
import org.attoparser.util.TextUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpService {

    public static GoodsListModel getGoods(String name, String page, String sort){
        if (name == null || "".equals(name)) {
            return null;
        }
        if (page == null || "".equals(page)) {
            page = "0";
        }
        if (sort == null || "".equals(sort)) {
            sort = "0";
        }
        try {
            RestTemplate restTemplate=new RestTemplate();
            String url = "http://localhost:9090/ip/search?name={name}&page={page}&sort={sort}";
            ResponseEntity<GoodsListModel> responseEntity = restTemplate.getForEntity(url, GoodsListModel.class, name, page, sort);
            GoodsListModel goodsListModel = responseEntity.getBody();
            return goodsListModel;
        } catch (Exception e) {
            System.out.print(e);
        }
        return new GoodsListModel();
    }

    public static GoodsListModel test(){
        RestTemplate restTemplate=new RestTemplate();
        String url = "http://localhost:9090/ip/search?name={name}&page={page}&sort={sort}";
        ResponseEntity<GoodsListModel> responseEntity = restTemplate.getForEntity(url, GoodsListModel.class, "欧舒丹","0","0");
        GoodsListModel goodsListModel = responseEntity.getBody();
        return goodsListModel;
    }

    public static CommentReturnModel getComments(List<String> ids, String index){
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        if (index == null || TextUtils.isEmpty(index) || Integer.parseInt(index) < 1) {
            index = "1";
        }

        try {
            RestTemplate restTemplate=new RestTemplate();
            StringBuilder url = new StringBuilder("http://localhost:9090/comment/get?ids=");
            int size = ids.size();
            for (int i = 0 ; i < size; i++) {
                String id = ids.get(i);
                if (TextUtils.isEmpty(id)) {
                    continue;
                }
                if (i == size - 1) {
                    url.append(id);
                } else {
                    url.append(id).append(',');
                }
            }
            url.append("&index=").append(index);
            ResponseEntity<CommentReturnModel> responseEntity = restTemplate.getForEntity(url.toString(), CommentReturnModel.class);
            CommentReturnModel returnModel = responseEntity.getBody();
            return returnModel;
        } catch (Exception e) {
            System.out.print(e);
        }
        return null;
    }

}
