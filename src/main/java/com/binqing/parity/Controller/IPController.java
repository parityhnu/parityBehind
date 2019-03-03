package com.binqing.parity.Controller;

import com.binqing.parity.Consts.TimeConsts;
import com.binqing.parity.Model.GoodsListModel;
import com.binqing.parity.Model.JDModel;
import com.binqing.parity.Model.SearchModel;
import com.binqing.parity.Model.TBModel;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@RestController()
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
    public GoodsListModel search(@RequestParam String name, @RequestParam int page) throws InterruptedException {
        String code = new StringBuilder(name).append("urlurlurlaaaaa").append(page).toString();
        String time = stringRedisTemplate.opsForValue().get(code);
        long currentTime = System.currentTimeMillis();
        //存的都是整点
        long saveTime = currentTime - currentTime % TimeConsts.MILLS_OF_ONE_HOUR;
        //控制爬虫频率，仅当时间超过一定间隔之后
        //否则直接进mongoDB找
        GoodsListModel goodsListModel = new GoodsListModel();
        if (time == null || (time != null && saveTime - Long.parseLong(time) > TimeConsts.MILLS_OF_ONE_HOUR)) {
            stringRedisTemplate.opsForValue().set(code, String.valueOf(saveTime));
            stringRedisTemplate.opsForList().leftPush(REDIS_URL, new StringBuilder(name).append("_").append(page).toString());
            AtomicInteger integer = new AtomicInteger(3);
            while(integer.decrementAndGet() > 0) {
                List<JDModel> jdModelList = findList(name, page, JDModel.class);
                List<TBModel> tbModelList = findList(name, page, TBModel.class);
                if (jdModelList != null || tbModelList != null) {
                    if (jdModelList != null) {
                        goodsListModel.setJdModelList(jdModelList);
                    }
                    if (tbModelList != null) {
                        goodsListModel.setTbModelList(tbModelList);
                    }
                    if ((jdModelList != null && !jdModelList.isEmpty()) || (tbModelList != null && !tbModelList.isEmpty())) {
                        break;
                    }
                }
                Thread.sleep(TimeConsts.MILLS_OF_ONE_SECOND);
            }
        } else {
            goodsListModel.setTbModelList(findList(name, page, TBModel.class));
            goodsListModel.setJdModelList(findList(name, page, JDModel.class));
        }
        return goodsListModel;
    }

    @GetMapping("/searchandredict")
    public ModelAndView searchAndRedict(RedirectAttributes attributes, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "page", required = false) String page) throws InterruptedException {
        if (name == null || "".equals(name)) {
            return new ModelAndView("redirect:/hello");
        }
        if (page == null || "".equals(page)) {
            page = "0";
        }
        attributes.addFlashAttribute("goodsList", search(name, Integer.parseInt(page)));
        return new ModelAndView("redirect:/search");
    }

    @GetMapping("/get")
    public SearchModel get() {
        SearchModel searchModel = new SearchModel();
        try {
            String result = stringRedisTemplate.opsForList().rightPop(REDIS_URL);
            searchModel.setPage(Integer.parseInt(result.split("_",2)[1]));
            searchModel.setGoodName(result.split("_",2)[0]);
        } catch (RuntimeException e) {
            searchModel = null;
        } finally {
            return searchModel;
        }
    }

    @GetMapping("/test")
    public List<TBModel> test() {
        return findList("欧舒丹", 1, 0, 10, TBModel.class);
    }

    @GetMapping("/test2")
    public List<TBModel> test2() {
        return findList("眼镜", 1,  TBModel.class);
    }


    private  <T>List<T> findList(String keyword, int page, Class<T> clazz) {
        return findList(keyword, page, 0, 0, clazz);
    }

    private  <T>List<T> findList(String keyword, int page, int skip, int limit, Class<T> clazz) {
        return findList(keyword, page, skip, limit, null, null, clazz);
    }

    private  <T>List<T> findList(String keyword, int page, int skip, int limit, Sort.Direction order, String sortBy, Class<T> clazz) {
        Query query = new Query();
        Criteria criteria = Criteria.where("keyword").is(keyword);
        Criteria criteria1 = Criteria.where("page").is(page);
        query.addCriteria(criteria);
        query.addCriteria(criteria1);
        query.skip(skip);
        query.limit(limit);
        if (order != null && sortBy != null) {
            query.with(new Sort(new Sort.Order(order, sortBy)));
        }
        return mongoTemplate.find(query, clazz);
    }

}
