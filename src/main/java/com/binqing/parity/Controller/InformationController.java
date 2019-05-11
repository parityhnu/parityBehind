package com.binqing.parity.Controller;

import com.binqing.parity.Consts.TimeConsts;
import com.binqing.parity.Model.Date_CountModel;
import com.binqing.parity.Model.InformationModel;
import com.binqing.parity.Model.String_CountModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/information")
public class InformationController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/test")
    public InformationModel test(HttpServletRequest request) throws SQLException {
        return getInformation(request);
    }

    @RequestMapping("/get")
    public InformationModel getInformation(HttpServletRequest request) throws SQLException {
        long currentTime = System.currentTimeMillis();
        //存的都是每天的0点
        currentTime = currentTime - currentTime % TimeConsts.MILLS_OF_ONE_DAY;
        long lastWeekTime = currentTime - TimeConsts.MILLS_OF_ONE_DAY * 6;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current = format.format(currentTime);
        String lastWeek = format.format(lastWeekTime);

        InformationModel informationModel = new InformationModel();

        //将活跃用户数按日期map
        Map<Date, Integer> map = new HashMap<>();
        List<Date_CountModel> activeUsers = queryActiveUsers(current, lastWeek);
        for (Date_CountModel date_countModel : activeUsers) {
            if (date_countModel == null) {
                continue;
            }
            map.put(date_countModel.getDate(), date_countModel.getCount());
        }
        informationModel.setActiveUserWeek(map);

        //将搜索数按日期map
        List<Date_CountModel> searchCount = querySearchCount(current, lastWeek);
        map = new HashMap<>();
        for (Date_CountModel date_countModel : searchCount) {
            if (date_countModel == null) {
                continue;
            }
            map.put(date_countModel.getDate(), date_countModel.getCount());
        }
        informationModel.setSearchWeek(map);

        informationModel.setKeywordAll(queryKeywordCount());
        informationModel.setKeywordWeek(queryWeekKeywordCount(current, lastWeek));
        informationModel.setSortAll(querySortCount());
        informationModel.setSortWeek(queryWeekSortCount(current, lastWeek));
        informationModel.setFavoriteCount(queryFavoriteUserCount());
        return informationModel;

    }

    private List<Date_CountModel> queryActiveUsers(String current, String lastWeek) {
        String sql = "select date,count from datecount where date between ? and ?";
        return getDate_CountModels(current, lastWeek, sql);
    }

    private List<Date_CountModel> querySearchCount(String current, String lastWeek) {
        String sql = "select date,sum(count) as count from searchcount where date BETWEEN ? and ? GROUP BY date;";
        return getDate_CountModels(current, lastWeek, sql);
    }

    private List<String_CountModel> queryKeywordCount() {
        String sql = "select keyword,sum(count) as count from searchcount GROUP BY keyword ORDER BY count DESC LIMIT 10;";
        return getString_CountModels(sql, true);
    }

    private List<String_CountModel> querySortCount() {
        String sql = "select sort, sum(count) as count from searchcount GROUP BY sort ORDER BY count DESC LIMIT 10;";
        return getString_CountModels(sql, false);
    }

    private List<String_CountModel> queryWeekKeywordCount(String current, String lastWeek) {
        String sql = "select keyword,sum(count) as count from searchcount where date BETWEEN ? and ? GROUP BY keyword ORDER BY count DESC LIMIT 10;";
        return getString_CountModels(current, lastWeek, sql, true);
    }

    private List<String_CountModel> queryWeekSortCount(String current, String lastWeek) {
        String sql = "select sort, sum(count) as count from searchcount where date BETWEEN ? and ? GROUP BY sort ORDER BY count DESC LIMIT 10;";
        return getString_CountModels(current, lastWeek, sql, false);
    }

    private int queryFavoriteUserCount() {
        String sql = "select count(uid) as count from (select uid from favorite GROUP BY uid) as countuid ";
        return jdbcTemplate.queryForObject(sql, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt(1);
            }
        });
    }

    private List<Date_CountModel> getDate_CountModels(String current, String lastWeek, String sql) {
        return jdbcTemplate.query(sql, new String[]{lastWeek, current}, new RowMapper<Date_CountModel>(){
            @Override
            public Date_CountModel mapRow(ResultSet resultSet, int i) throws SQLException {
                Date_CountModel date_countModel = new Date_CountModel();
                date_countModel.setDate(resultSet.getDate("date"));
                date_countModel.setCount(resultSet.getInt("count"));
                return date_countModel;
            }
        });
    }

    private List<String_CountModel> getString_CountModels(String sql, boolean isKeyword) {
        return jdbcTemplate.query(sql, new RowMapper<String_CountModel>(){
            @Override
            public String_CountModel mapRow(ResultSet resultSet, int i) throws SQLException {
                String_CountModel string_countModel = new String_CountModel();
                if (isKeyword) {
                    string_countModel.setString(resultSet.getString("keyword"));
                } else {
                    string_countModel.setString(resultSet.getString("sort"));
                }
                string_countModel.setCount(resultSet.getInt("count"));
                return string_countModel;
            }
        });
    }

    private List<String_CountModel> getString_CountModels(String current, String lastWeek, String sql, boolean isKeyword) {
        return jdbcTemplate.query(sql, new String[]{lastWeek, current}, new RowMapper<String_CountModel>(){
            @Override
            public String_CountModel mapRow(ResultSet resultSet, int i) throws SQLException {
                String_CountModel string_countModel = new String_CountModel();
                if (isKeyword) {
                    string_countModel.setString(resultSet.getString("keyword"));
                } else {
                    string_countModel.setString(resultSet.getString("sort"));
                }
                string_countModel.setCount(resultSet.getInt("count"));
                return string_countModel;
            }
        });
    }


}


