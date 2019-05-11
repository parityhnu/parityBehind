package com.binqing.parity.Service;

import com.binqing.parity.Model.*;
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

    public static List<ParityModel> getGoods(List<String> ids){
        if (ids == null || ids.isEmpty()) {
            return null;
        }

        try {
            RestTemplate restTemplate=new RestTemplate();
            StringBuilder url = new StringBuilder("http://localhost:9090/ip/getparitys?ids=");
            int size = ids.size();
            makeids(ids, url, size);
            ResponseEntity<ParityModel[]> responseEntity = restTemplate.getForEntity(url.toString(), ParityModel[].class);
            List<ParityModel> returnModel = Arrays.asList(responseEntity.getBody());
            return returnModel;
        } catch (Exception e) {
            System.out.print(e);
        }
        return null;
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
            makeids(ids, url, size);
            url.append("&index=").append(index);
            ResponseEntity<CommentReturnModel> responseEntity = restTemplate.getForEntity(url.toString(), CommentReturnModel.class);
            CommentReturnModel returnModel = responseEntity.getBody();
            return returnModel;
        } catch (Exception e) {
            System.out.print(e);
        }
        return null;
    }

    public static List<AttributeModel> getAttributes(List<String> ids){
        if (ids == null || ids.isEmpty()) {
            return null;
        }

        try {
            RestTemplate restTemplate=new RestTemplate();
            StringBuilder url = new StringBuilder("http://localhost:9090/attribute/get?ids=");
            int size = ids.size();
            makeids(ids, url, size);
            ResponseEntity<AttributeModel[]> responseEntity = restTemplate.getForEntity(url.toString(), AttributeModel[].class);
            List<AttributeModel> returnModel = Arrays.asList(responseEntity.getBody());
            return returnModel;
        } catch (Exception e) {
            System.out.print(e);
        }
        return null;
    }

    public static StringModel checkFavorite( String user, String id, String name, String sort){
        if (user == null || user.isEmpty() || id == null || id.isEmpty() || name == null || name.isEmpty() || sort == null || sort.isEmpty()) {
            return null;
        }

        try {
            RestTemplate restTemplate=new RestTemplate();
            StringBuilder url = new StringBuilder("http://localhost:9090/user/checkfavorite?user=");
            url.append(user).append("&id=").append(id).append("&name=").append(name).append("&sort=").append(sort);

            ResponseEntity<StringModel> responseEntity = restTemplate.getForEntity(url.toString(), StringModel.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            System.out.print(e);
        }
        return null;
    }

    public static List<ParityModel> getFavorites(String user){
        if (user == null || TextUtils.isEmpty(user)) {
            return null;
        }

        try {
            RestTemplate restTemplate=new RestTemplate();
            StringBuilder url = new StringBuilder("http://localhost:9090/ip/getFavorite?user=");
            url.append(user);
            ResponseEntity<ParityModel[]> responseEntity = restTemplate.getForEntity(url.toString(), ParityModel[].class);
            List<ParityModel> returnModel = Arrays.asList(responseEntity.getBody());
            return returnModel;
        } catch (Exception e) {
            System.out.print(e);
        }
        return null;
    }

    public static ConfigModel getConfig(){
        try {
            RestTemplate restTemplate=new RestTemplate();
            StringBuilder url = new StringBuilder("http://localhost:9090/controller/admin/getconfig");
            ResponseEntity<ConfigModel> responseEntity = restTemplate.getForEntity(url.toString(), ConfigModel.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            System.out.print(e);
        }
        return null;
    }

    public static UserModel getUser(String uid){
        if (uid == null || TextUtils.isEmpty(uid)) {
            return null;
        }
        try {
            RestTemplate restTemplate=new RestTemplate();
            StringBuilder url = new StringBuilder("http://localhost:9090/controller/admin/getuser?user=");
            url.append(uid);
            ResponseEntity<UserModel> responseEntity = restTemplate.getForEntity(url.toString(), UserModel.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            System.out.print(e);
        }
        return null;
    }

    public static List<UserModel> getUsers(String page){
        if (page == null || TextUtils.isEmpty(page)) {
            return null;
        }
        try {
            RestTemplate restTemplate=new RestTemplate();
            StringBuilder url = new StringBuilder("http://localhost:9090/controller/admin/getusers?page=");
            url.append(page);
            ResponseEntity<UserModel[]> responseEntity = restTemplate.getForEntity(url.toString(), UserModel[].class);
            List<UserModel> returnModel = Arrays.asList(responseEntity.getBody());
            return returnModel;
        } catch (Exception e) {
            System.out.print(e);
        }
        return null;
    }

    public static UserModel getAdmin(String uid){
        if (uid == null || TextUtils.isEmpty(uid)) {
            return null;
        }
        try {
            RestTemplate restTemplate=new RestTemplate();
            StringBuilder url = new StringBuilder("http://localhost:9090/controller/admin/getadmin?user=");
            url.append(uid);
            ResponseEntity<UserModel> responseEntity = restTemplate.getForEntity(url.toString(), UserModel.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            System.out.print(e);
        }
        return null;
    }

    public static List<UserModel> getAdmins(String page){
        if (page == null || TextUtils.isEmpty(page)) {
            return null;
        }
        try {
            RestTemplate restTemplate=new RestTemplate();
            StringBuilder url = new StringBuilder("http://localhost:9090/controller/admin/getadmins?page=");
            url.append(page);
            ResponseEntity<UserModel[]> responseEntity = restTemplate.getForEntity(url.toString(), UserModel[].class);
            List<UserModel> returnModel = Arrays.asList(responseEntity.getBody());
            return returnModel;
        } catch (Exception e) {
            System.out.print(e);
        }
        return null;
    }

    public static Integer getAdminNumber(){
        try {
            RestTemplate restTemplate=new RestTemplate();
            StringBuilder url = new StringBuilder("http://localhost:9090/controller/admin/getadminnumber");
            ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(url.toString(), Integer.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            System.out.print(e);
        }
        return null;
    }

    public static Integer getUserNumber(){
        try {
            RestTemplate restTemplate=new RestTemplate();
            StringBuilder url = new StringBuilder("http://localhost:9090/controller/admin/getusernumber");
            ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(url.toString(), Integer.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            System.out.print(e);
        }
        return null;
    }

    public static InformationModel getInformation(){
        try {
            RestTemplate restTemplate=new RestTemplate();
            StringBuilder url = new StringBuilder("http://localhost:9090/information/get");
            ResponseEntity<InformationModel> responseEntity = restTemplate.getForEntity(url.toString(), InformationModel.class);
            InformationModel informationModel = responseEntity.getBody();
            return informationModel;
        } catch (Exception e) {
            System.out.print(e);
        }
        return null;
    }

    private static void makeids(List<String> ids, StringBuilder url, int size) {
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
    }

}
