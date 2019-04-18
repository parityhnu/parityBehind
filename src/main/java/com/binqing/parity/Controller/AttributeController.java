package com.binqing.parity.Controller;

import com.binqing.parity.Model.*;
import com.binqing.parity.Service.HttpService;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/attribute")
public class AttributeController {


    @Autowired
    MongoTemplate mongoTemplate;



    @GetMapping("/get")
    public List<AttributeModel> get(@RequestParam (required = false) List<String> ids) {
        List<AttributeModel> result = new ArrayList<>();
        for (String id : ids){
            if (TextUtils.isEmpty(id)) {
                continue;
            }
            String [] strings = id.split(":");
            if (strings.length <= 1) {
                continue;
            }
            String judge = strings[0];
            id = strings[1];
            if ("jd".equals(judge)) {
                result.addAll(findList(id, 0, 0, null, null, AttributeModel.class, "attribute_jd"));
            }
            else if ("tb".equals(judge)){
                result.addAll(findList(id, 0, 0, null, null, AttributeModel.class, "attribute_tb"));
            }
            else if("tm".equals(judge)){
                result.addAll(findList(id, 0, 0, null, null, AttributeModel.class, "attribute_tm"));
            }
        }
        return result;
    }

    private <T> List<T> findList(String id, int skip, int limit, Sort.Direction order, String sortBy, Class<T> clazz, String collectionName) {
        Query query = new Query();
        Criteria criteria = Criteria.where("gid").is(id);
        query.addCriteria(criteria);
        query.skip(skip);
        query.limit(limit);
        if (order != null && sortBy != null) {
            query.with(new Sort(new Sort.Order(order, sortBy)));
        }
        if (collectionName != null && !"".equals(collectionName)) {
            return mongoTemplate.find(query, clazz, collectionName);
        }
        return mongoTemplate.find(query, clazz);
    }

}


