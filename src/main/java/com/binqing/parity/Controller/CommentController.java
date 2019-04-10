package com.binqing.parity.Controller;

import com.binqing.parity.Model.*;
import org.apache.http.util.TextUtils;
import org.attoparser.util.TextUtil;
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
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    MongoTemplate mongoTemplate;

    @GetMapping("/get")
    public List<BaseCommentModel> get(@RequestParam (required = false) List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        List<BaseCommentModel> result = new ArrayList<>();
        for (String id : ids) {
            if (TextUtils.isEmpty(id)) {
                continue;
            }
            String [] strings = id.split(":");
            if (strings.length <= 1) {
                continue;
            }
            String judge = strings[0];
            id = strings[1];
            switch (judge) {
                case "jd":
                    List<JDCommentModel> jdCommentModels = findList(id, JDCommentModel.class);
                    result.addAll(jdCommentModels);
                    break;
                case "tb":
                    List<TBCommentModel> tbCommentModels = findList(id, TBCommentModel.class);
                    result.addAll(tbCommentModels);
                    break;
                case "tm":
                    List<TMCommentModel> tmCommentModels = findList(id, TMCommentModel.class);
                    result.addAll(tmCommentModels);
                    break;
                default:
                    break;
            }
        }
        Collections.sort(result);
        return result;
    }

    private <T> List<T> findList(String id, Class<T> clazz) {
        return findList(id, 0, 0, clazz);
    }

    private <T> List<T> findList(String id, int skip, int limit, Class<T> clazz) {
        return findList(id, skip, limit, null, null, clazz);
    }

    private <T> List<T> findList(String id, int skip, int limit, Sort.Direction order, String sortBy, Class<T> clazz) {
        return findList(id, skip, limit, order, sortBy, clazz, null);
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


