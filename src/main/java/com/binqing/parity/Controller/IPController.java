package com.binqing.parity.Controller;

import com.binqing.parity.Consts.TimeConsts;
import com.binqing.parity.Enum.SortType;
import com.binqing.parity.Model.*;
import org.apache.http.util.TextUtils;
import org.attoparser.util.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/ip")
public class IPController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String REDIS_URL = "redis_url_once";

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

        //保存记录用作统计
        saveSearch(name, qsort);

        String code = new StringBuilder(name).append("urlurlurlaaaaa").append(qsort).toString();
        String time = stringRedisTemplate.opsForValue().get(code);
        long currentTime = System.currentTimeMillis();
        //存的都是整点
        long saveTime = currentTime - currentTime % TimeConsts.MILLS_OF_ONE_HOUR;
        //控制爬虫频率，仅当时间超过一定间隔之后
        //否则直接进mongoDB找
        GoodsListModel goodsListModel = new GoodsListModel();
        if (time == null || (time != null && saveTime - Long.parseLong(time) > TimeConsts.MILLS_OF_ONE_DAY)) {
            ConfigModel configModel = getConfig();
            stringRedisTemplate.opsForValue().set(code, String.valueOf(saveTime));
            stringRedisTemplate.opsForList().leftPush(REDIS_URL, new StringBuilder(name)
                    .append("_")
                    .append(qsort)
                    .append("_")
                    .append(configModel.getJd())
                    .append("_")
                    .append(configModel.getTb())
                    .toString());
            AtomicInteger integer = new AtomicInteger(10);
            while(integer.decrementAndGet() > 0) {
                List<GoodsModel> goodsModelList = goodsListModel.getGoodsModelList();
                if (goodsModelList == null || goodsModelList.isEmpty()) {
                    List<GoodsModel> modelList = findList(name, page, qsort, GoodsModel.class);
                    if (modelList != null && !modelList.isEmpty()) {
                        goodsListModel.setGoodsModelList(modelList);
                        List<GoodsModel> pageModel = findMaxPage(name, qsort, GoodsModel.class);
                        if (pageModel != null && !pageModel.isEmpty()) {
                            goodsListModel.setMaxPage(pageModel.get(0).getPage());
                        }
                        break;
                    }
                }
                Thread.sleep(TimeConsts.MILLS_OF_ONE_SECOND);
            }
        } else {
            goodsListModel.setGoodsModelList(findList(name, page, qsort, GoodsModel.class));
            List<GoodsModel> pageModel = findMaxPage(name, qsort, GoodsModel.class);
            if (pageModel != null && !pageModel.isEmpty()) {
                goodsListModel.setMaxPage(pageModel.get(0).getPage());
            }
        }

        if (page == 0) {
            goodsListModel.setParityGoodsList(findPairty(name, qsort, 0, 10, Sort.Direction.ASC, "distance",
                    Sort.Direction.ASC, "order",ParityModel.class));
        }
        return goodsListModel;
    }

    @RequestMapping("/test")
    public void test() {
        saveSearch("iphone", 0);
    }

    private void saveSearch(String keyword, int sort) {
        long currentTime = System.currentTimeMillis();
        //存的都是每天的0点
        long toSaveTime = currentTime - currentTime % TimeConsts.MILLS_OF_ONE_DAY;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(toSaveTime);
        String sql = "select count from searchcount where date = ? and keyword = ? and sort = ?";
        List<Integer> integers = jdbcTemplate.query(sql,new String[]{date, keyword, String.valueOf(sort)}, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt(1);
            }
        });
        if (integers == null || integers.isEmpty()) {
            sql = "insert into searchcount(date, keyword, sort, count) VALUES (?,?,?,1)";
            jdbcTemplate.update(sql,date,keyword,sort);
            return;
        } else {
            int count = integers.get(0)+1;
            sql = "update searchcount set count = ? where date = ? and keyword = ? and sort = ?";
            jdbcTemplate.update(sql, count, date, keyword, sort);
        }
    }

    @GetMapping("/getparitys")
    public List<ParityModel> getparitys(@RequestParam List<String> ids) throws InterruptedException {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        List<ParityModel> result = new ArrayList<>();
        for (String id : ids) {
            if (TextUtils.isEmpty(id)) {
                continue;
            }
            String [] strings = id.split(":");
            if (strings.length >= 2) {
                id = strings[1];
                result.addAll(findPairty(id, 0, 1, null, null, null, null, ParityModel.class));
            }
        }
        return result;
    }


    @GetMapping("getFavorite")
    public List<ParityModel> getFavorite(@RequestParam String user) {
        if (user == null || "".equals(user)) {
            return null;
        }
        try {
            saveLogin(Integer.parseInt(user));
        } catch (Exception e){
            return null;
        }
        List<ParityModel> result = new ArrayList<>();
        String sql = "select id, keyword, sort from favorite where uid = ?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, user);
        for (Map<String, Object> map : maps) {
            if (map == null) {
                continue;
            }
            String id = (String) map.get("id");
            String keyword = (String) map.get("keyword");
            String sort = (String) map.get("sort");
            String[] strings = id.split(":");
            if (strings.length >= 2) {
                String judge = strings[0];
                id = strings[1];
            }
            result.addAll(findPairty(id, 0, 1, null, null, null, null, ParityModel.class));
            String code = new StringBuilder(keyword).append("urlurlurlaaaaa").append(sort).toString();
            String time = stringRedisTemplate.opsForValue().get(code);
            long currentTime = System.currentTimeMillis();
            long saveTime = currentTime - currentTime % TimeConsts.MILLS_OF_ONE_HOUR;
            if (time == null || (time != null && saveTime - Long.parseLong(time) > TimeConsts.MILLS_OF_ONE_DAY)) {
                stringRedisTemplate.opsForValue().set(code, String.valueOf(saveTime));
                stringRedisTemplate.opsForList().leftPush(REDIS_URL, new StringBuilder(keyword).append("_").append(sort).toString());
            }
        }
        return result;
    }


    private void saveLogin(int uid) {
        String key = REDIS_URL + "_" + uid;
        String time = stringRedisTemplate.opsForValue().get(key);
        long currentTime = System.currentTimeMillis();
        //存的都是每天的0点
        long toSaveTime = currentTime - currentTime % TimeConsts.MILLS_OF_ONE_DAY;
        //不相同就说明是新的一天,存入redis和数据库
        if (!String.valueOf(toSaveTime).equals(time)) {
            stringRedisTemplate.opsForValue().set(key, String.valueOf(toSaveTime));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = format.format(toSaveTime);
            String sql = "select count from datecount where date = ?";
            List<Integer> integers = jdbcTemplate.query(sql,new String[]{date}, new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getInt(1);
                }
            });
            if (integers == null || integers.isEmpty()) {
                sql = "insert into datecount(date, count) VALUES (? ,1)";
                jdbcTemplate.update(sql,date);
                return;
            } else {
                int count = integers.get(0)+1;
                sql = "update datecount set count = ? where date = ?";
                jdbcTemplate.update(sql, count, date);
            }
        }
    }

    private  <T>List<T> findList(String keyword, int page, int sort, Class<T> clazz) {
        return findList(keyword, page, sort, 0, 0, clazz);
    }

    private  <T>List<T> findList(String keyword, int page, int sort, int skip, int limit, Class<T> clazz) {
        return findList(keyword, page, sort, skip, limit, null, null, clazz);
    }

    private  <T>List<T> findList(String keyword, int page, int sort, int skip, int limit, Sort.Direction order, String sortBy, Class<T> clazz) {
        return findList(keyword, page, sort, skip, limit, order, sortBy, clazz, null);
    }

    private  <T>List<T> findList(String keyword, int page, int sort, int skip, int limit, Sort.Direction order, String sortBy, Class<T> clazz, String collectionName) {
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
        if (collectionName != null && !"".equals(collectionName)) {
            return mongoTemplate.find(query, clazz, collectionName);
        }
        return mongoTemplate.find(query, clazz);
    }

    private  <T>List<T> findPairty(String gid, int skip, int limit, Sort.Direction order, String sortBy
            , Sort.Direction order2, String sortBy2, Class<T> clazz) {
        Query query = new Query();
        Criteria criteria = Criteria.where("gid").is(gid);
        query.addCriteria(criteria);
        query.skip(skip);
        query.limit(limit);
        if (order != null && sortBy != null) {
            query.with(new Sort(new Sort.Order(order, sortBy), new Sort.Order(order2,sortBy2)));
        }
        return mongoTemplate.find(query, clazz);
    }


    private  <T>List<T> findPairty(String keyword, int sort, int skip, int limit, Sort.Direction order, String sortBy
            , Sort.Direction order2, String sortBy2, Class<T> clazz) {
        Query query = new Query();
        Criteria criteria = Criteria.where("keyword").is(keyword);
        Criteria criteria2 = Criteria.where("sort").is(sort);
        query.addCriteria(criteria);
        query.addCriteria(criteria2);
        query.skip(skip);
        query.limit(limit);
        if (order != null && sortBy != null) {
            query.with(new Sort(new Sort.Order(order, sortBy), new Sort.Order(order2,sortBy2)));
        }
        return mongoTemplate.find(query, clazz);
    }


    private  <T>List<T> findMaxPage(String keyword, int sort, Class<T> clazz) {
        Query query = new Query();
        Criteria criteria = Criteria.where("keyword").is(keyword);
        Criteria criteria2 = Criteria.where("sort").is(sort);
        query.addCriteria(criteria);
        query.addCriteria(criteria2);
        query.limit(1);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "page")));
        return mongoTemplate.find(query, clazz);

    }

    private ConfigModel getConfig() {
        String querySql = "select * from config where id = 1;";
        return jdbcTemplate.queryForObject(querySql, new RowMapper<ConfigModel>() {
            @Override
            public ConfigModel mapRow(ResultSet resultSet, int i) throws SQLException {
                ConfigModel configModel = new ConfigModel();
                configModel.setJd(resultSet.getInt("jd"));
                configModel.setTb(resultSet.getInt("tb"));
                return configModel;
            }
        });
    }

//    private GoodsListModel parity(List<JDModel> jdModelList, List<TBModel> tbModelList, String keyword) {
//        List<TBModel> resultTB = new ArrayList<>();
//        List<JDModel> resultJD = new ArrayList<>();
//        for (TBModel model : tbModelList) {
//            if (model.getShop().contains("旗舰店") && EditDistance.levenshtein(model.getName(), keyword) == 1) {
//                resultTB.add(model);
//            }
//        }
//
//        for (JDModel model : jdModelList) {
//            if ((model.getShop().contains("自营") || model.getShop().contains("旗舰店")) && EditDistance.levenshtein(model.getName(), keyword) == 1) {
//                resultJD.add(model);
//            }
//        }
//
//        int sizeTB = resultTB.size();
//        int sizeJD = resultJD.size();
//        float max = -1;
//        float distance = 0;
//        int goodsTB = -1;
//        int goodsJD = -1;
//        for (int i = 0; i < sizeTB; i++) {
//            for (int j = 0; j < sizeJD; j++) {
//                distance = EditDistance.levenshtein(resultTB.get(i).getName(), resultJD.get(j).getName());
//                if (distance > max) {
//                    max = distance;
//                    goodsTB = i;
//                    goodsJD = j;
//                }
//                if (distance == max) {
//                    if (Double.parseDouble(resultTB.get(i).getPrice()) < Double.parseDouble(resultTB.get(goodsTB).getPrice())) {
//                        goodsTB = i;
//                    }
//                    if (Double.parseDouble(resultJD.get(j).getPrice()) < Double.parseDouble(resultJD.get(goodsJD).getPrice())) {
//                        goodsJD = j;
//                    }
//                }
//            }
//        }
//        if (goodsTB == goodsJD && goodsTB == -1) {
//            return null;
//        }
//        System.out.println(resultTB.get(goodsTB).getName() + " "+ resultJD.get(goodsJD).getName() + distance);
//        GoodsListModel model = new GoodsListModel();
//        model.setParityJdModel(resultJD.get(goodsJD));
//        model.setParityTbModel(resultTB.get(goodsTB));
//        return model;
//    }

}
