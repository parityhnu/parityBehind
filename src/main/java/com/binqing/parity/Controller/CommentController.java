package com.binqing.parity.Controller;

import com.binqing.parity.Model.*;
import com.binqing.parity.Service.HttpService;
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

    private static final int COMMENT_PAGE_SIZE = 15;

    @Autowired
    MongoTemplate mongoTemplate;

    @GetMapping("/test")
    public CommentReturnModel test() {
        List<String> ids = new ArrayList<>();
        ids.add("tb:587861043551");
        CommentReturnModel list = HttpService.getComments(ids, "1");
        return list;
    }

    @GetMapping("/test2")
    public CommentReturnModel test2() {
        List<String> ids = new ArrayList<>();
        ids.add("tb:587861043551");
        return get(ids, "1");
    }

    @GetMapping("/get")
    public CommentReturnModel get(@RequestParam (required = false) List<String> ids, @RequestParam(required = false) String index) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        if (index == null || TextUtils.isEmpty(index)) {
            index = "1";
        }
        int page = Integer.parseInt(index);
        //最小值为1
        if (page < 1) {
            page = 1;
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
        int size = result.size();
        if (size == 0) {
            return new CommentReturnModel();
        }
        Collections.sort(result);
        int maxPage = size / COMMENT_PAGE_SIZE;
        if (maxPage * COMMENT_PAGE_SIZE < size) {
            maxPage += 1;
        }
        List<BaseCommentModel> finalList = new ArrayList<>();
        if (maxPage != 0) {
            if (page >= maxPage) {
                page = maxPage;
                finalList = result.subList((page - 1) * COMMENT_PAGE_SIZE, size);
            } else {
                finalList = result.subList((page - 1) * COMMENT_PAGE_SIZE, page * COMMENT_PAGE_SIZE);
            }
        }

        //以下一步过滤的目的是，因为经过HttpService之后会过滤掉子类的一些属性
        List<JDCommentModel> jdCommentModels = new ArrayList<>();
        List<TBCommentModel> tbCommentModels = new ArrayList<>();
        List<TMCommentModel> tmCommentModels = new ArrayList<>();
        for (BaseCommentModel baseCommentModel : finalList) {
            if (baseCommentModel instanceof JDCommentModel) {
                jdCommentModels.add((JDCommentModel) baseCommentModel);
            } else if (baseCommentModel instanceof TMCommentModel) {
                tmCommentModels.add((TMCommentModel) baseCommentModel);
            } else if (baseCommentModel instanceof TBCommentModel) {
                tbCommentModels.add((TBCommentModel) baseCommentModel);
            }
        }
        CommentReturnModel returnModel = new CommentReturnModel();
        returnModel.setMaxPage(maxPage);
        returnModel.setJdCommentModels(jdCommentModels);
        returnModel.setTbCommentModels(tbCommentModels);
        returnModel.setTmCommentModels(tmCommentModels);
        return returnModel;
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


