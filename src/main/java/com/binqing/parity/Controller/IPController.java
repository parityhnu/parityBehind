package com.binqing.parity.Controller;

import com.binqing.parity.Consts.TimeConsts;
import com.binqing.parity.EditDistance;
import com.binqing.parity.Enum.SortType;
import com.binqing.parity.Model.GoodsListModel;
import com.binqing.parity.Model.JDModel;
import com.binqing.parity.Model.SearchModel;
import com.binqing.parity.Model.TBModel;
import com.binqing.parity.Service.HttpService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.internal.MongoClientImpl;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/ip")
public class IPController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MongoTemplate mongoTemplate;

    private static final String REDIS_URL = "redis_url";
    /**
     * 暂定存商品名称以及page页，
     * 如果要分几个服务器来爬，则选择存不同电商的url
     * 存储的时间为每隔半点、整点
     * @param name
     * @param page
     * @return
     */
    @GetMapping("/search")
    public GoodsListModel search(@RequestParam String name, @RequestParam int page, @RequestParam(name = "sort", required = false)String sort) throws InterruptedException {
        System.out.println(name);
        int qsort;
        if (sort == null || "".equals(sort)) {
            qsort = SortType.INIT.getValue();
        } else {
            qsort = Integer.parseInt(sort);
            switch (qsort) {
                case 0:
                case 1:
                case 2:
                case 3:
                    break;
                default:
                    qsort = SortType.INIT.getValue();
            }
        }

        String code = new StringBuilder(name).append("urlurlurlaaaaa").append(page).append("_").append(qsort).toString();
        String time = stringRedisTemplate.opsForValue().get(code);
        long currentTime = System.currentTimeMillis();
        //存的都是整点
        long saveTime = currentTime - currentTime % TimeConsts.MILLS_OF_ONE_HOUR;
        //控制爬虫频率，仅当时间超过一定间隔之后
        //否则直接进mongoDB找
        GoodsListModel goodsListModel = new GoodsListModel();
        if (time == null || (time != null && saveTime - Long.parseLong(time) > TimeConsts.MILLS_OF_ONE_DAY)) {
//            ToUsePy.catchGoods(String.valueOf(page), name, sort);
            stringRedisTemplate.opsForValue().set(code, String.valueOf(saveTime));
            stringRedisTemplate.opsForList().leftPush(REDIS_URL, new StringBuilder(name).append("_").append(page).append("_").append(qsort).toString());
            AtomicInteger integer = new AtomicInteger(8);
            while(integer.decrementAndGet() > 0) {
                List<JDModel> jdModelList = goodsListModel.getJdModelList();
                List<TBModel> tbModelList = goodsListModel.getTbModelList();
                if (jdModelList == null || jdModelList.isEmpty()) {
                    List<JDModel> modelList = findList(name, page,qsort, JDModel.class);
                    if (modelList != null && !modelList.isEmpty()) {
                        goodsListModel.setJdModelList(modelList);
                    }
                }
                if (tbModelList == null || tbModelList.isEmpty()) {
                    List<TBModel> modelList = findList(name, page,qsort, TBModel.class);
                    if (modelList != null && !modelList.isEmpty()) {
                        goodsListModel.setTbModelList(modelList);
                    }
                }

                jdModelList = goodsListModel.getJdModelList();
                tbModelList = goodsListModel.getTbModelList();
                if ((jdModelList != null && !jdModelList.isEmpty()) && (tbModelList != null && !tbModelList.isEmpty())) {
                    break;
                }
                Thread.sleep(TimeConsts.MILLS_OF_ONE_SECOND);
            }
        } else {
            goodsListModel.setTbModelList(findList(name, page,qsort, TBModel.class));
            goodsListModel.setJdModelList(findList(name, page,qsort, JDModel.class));
        }

        if (page == 0) {
            GoodsListModel listModel = parity(goodsListModel.getJdModelList(), goodsListModel.getTbModelList(), name);
            if (listModel != null) {
                goodsListModel.setParityTbModel(listModel.getParityTbModel());
                goodsListModel.setParityJdModel(listModel.getParityJdModel());
            }
        }
        return goodsListModel;
    }

    @GetMapping("/test")
    public GoodsListModel test() throws URISyntaxException {
        return HttpService.test();
    }


        @GetMapping("/test2")
    public List<TBModel> test2() {
        return findList("眼镜", 1, 0,  TBModel.class);
    }


    private  <T>List<T> findList(String keyword, int page, int sort, Class<T> clazz) {
        return findList(keyword, page, sort, 0, 0, clazz);
    }

    private  <T>List<T> findList(String keyword, int page, int sort, int skip, int limit, Class<T> clazz) {
        return findList(keyword, page, sort, skip, limit, null, null, clazz);
    }

    private  <T>List<T> findList(String keyword, int page, int sort, int skip, int limit, Sort.Direction order, String sortBy, Class<T> clazz) {
        Query query = new Query();
        Criteria criteria = Criteria.where("keyword").is(keyword);
        Criteria criteria1 = Criteria.where("page").is(page);
        Criteria criteria2 = Criteria.where("sort").is(sort);
        query.addCriteria(criteria);
        query.addCriteria(criteria1);
        query.addCriteria(criteria2);
        query.skip(skip);
        query.limit(limit);
        if (order != null && sortBy != null) {
            query.with(new Sort(new Sort.Order(order, sortBy)));
        }
        return mongoTemplate.find(query, clazz);
    }

    private GoodsListModel parity(List<JDModel> jdModelList, List<TBModel> tbModelList, String keyword) {
        if (jdModelList == null || tbModelList == null || jdModelList.isEmpty() || tbModelList.isEmpty()) {
            return null;
        }
        List<TBModel> resultTB = new ArrayList<>();
        List<JDModel> resultJD = new ArrayList<>();
        for (TBModel model : tbModelList) {
            if (model.getShop().contains("旗舰店") && EditDistance.levenshtein(model.getName(), keyword) == 1) {
                resultTB.add(model);
            }
        }

        for (JDModel model : jdModelList) {
            if ((model.getShop().contains("自营") || model.getShop().contains("旗舰店")) && EditDistance.levenshtein(model.getName(), keyword) == 1) {
                resultJD.add(model);
            }
        }

        int sizeTB = resultTB.size();
        int sizeJD = resultJD.size();
        float max = -1;
        float distance = 0;
        int goodsTB = -1;
        int goodsJD = -1;
        for (int i = 0; i < sizeTB; i++) {
            for (int j = 0; j < sizeJD; j++) {
                distance = EditDistance.levenshtein(resultTB.get(i).getName(), resultJD.get(j).getName());
                if (distance > max) {
                    max = distance;
                    goodsTB = i;
                    goodsJD = j;
                }
                if (distance == max) {
                    if (Double.parseDouble(resultTB.get(i).getPrice()) < Double.parseDouble(resultTB.get(goodsTB).getPrice())) {
                        goodsTB = i;
                    }
                    if (Double.parseDouble(resultJD.get(j).getPrice()) < Double.parseDouble(resultJD.get(goodsJD).getPrice())) {
                        goodsJD = j;
                    }
                }
            }
        }
        if (goodsTB == goodsJD && goodsTB == -1) {
            return null;
        }
        System.out.println(resultTB.get(goodsTB).getName() + " "+ resultJD.get(goodsJD).getName() + " " + distance);
        GoodsListModel model = new GoodsListModel();
        model.setParityJdModel(resultJD.get(goodsJD));
        model.setParityTbModel(resultTB.get(goodsTB));
        return model;
    }

}
