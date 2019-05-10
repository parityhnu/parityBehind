package com.binqing.parity.Controller;

import com.binqing.parity.Model.ConfigModel;
import com.binqing.parity.Model.LoginModel;
import com.binqing.parity.Model.StringModel;
import com.binqing.parity.Model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 管理员注册登录在UserController
 */
@RestController
@RequestMapping("/controller/admin")
public class AdminController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/getusernumber")
    public Integer getUsers(){
        String sql = "select count(uid) as count from user";
        return jdbcTemplate.queryForObject(sql, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt(1);
            }
        });
    }

    @RequestMapping("/getusers")
    public List<UserModel> getUsers(@RequestParam String page) throws SQLException {
        return queryUsers(Integer.parseInt(page));
    }

    @RequestMapping("/getuser")
    public UserModel getUser(@RequestParam String user){
        String sql = "select * from user where uid = " + user;
        List<UserModel> userModels = getUserModels(sql);
        if (userModels == null || userModels.isEmpty()) {
            return null;
        }
        sql = "select state from login where uid = " + user;
        userModels.get(0).setState( getState(sql).getState());
        return userModels.get(0);
    }

    @RequestMapping("/getadminnumber")
    public Integer getAdmins(){
        String sql = "select count(uid) as count from admin";
        return jdbcTemplate.queryForObject(sql, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt(1);
            }
        });
    }

    @RequestMapping("/getadmins")
    public List<UserModel> getAdmins(@RequestParam String page) throws SQLException {
        return queryAdmins(Integer.parseInt(page));
    }

    @RequestMapping("/getadmin")
    public UserModel getAdmin(@RequestParam String user){
        String sql = "select * from admin where uid = " + user;
        List<UserModel> userModels = getAdminModels(sql);
        if (userModels == null || userModels.isEmpty()) {
            return null;
        }
        return userModels.get(0);
    }

    @RequestMapping("/banuser")
    public StringModel banUser(@RequestParam String account) {
        StringModel stringModel = new StringModel();
        java.lang.String sql = "update login set state = -1 where account = ?";
        if (jdbcTemplate.update(sql, account) == 1) {
            stringModel.setString(account);
        } else {
            stringModel.setString("-1");
        }
        return stringModel;
    }

    @RequestMapping("/releaseuser")
    public StringModel releaseUser(@RequestParam String account) {
        StringModel stringModel = new StringModel();
        String sql = "update login set state = ? where account = ?";
        if (jdbcTemplate.update(sql, System.currentTimeMillis(), account) == 1) {
            stringModel.setString(account);
        } else {
            stringModel.setString("-1");
        }
        return stringModel;
    }

    @RequestMapping("/banadmin")
    public StringModel banAdmin(@RequestParam java.lang.String account) {
        StringModel stringModel = new StringModel();
        java.lang.String sql = "update admin set state = -1 where account = ?";
        if (jdbcTemplate.update(sql, account) == 1) {
            stringModel.setString(account);
        } else {
            stringModel.setString("-1");
        }
        return stringModel;
    }

    @RequestMapping("/releaseadmin")
    public StringModel releaseAdmin(@RequestParam String account) {
        StringModel stringModel = new StringModel();
        java.lang.String sql = "update admin set state = ? where account = ?";
        if (jdbcTemplate.update(sql, System.currentTimeMillis(), account) == 1) {
            stringModel.setString(account);
        } else {
            stringModel.setString("-1");
        }
        return stringModel;
    }

    @RequestMapping("/getconfig")
    public ConfigModel getConfig() {
        return getConfigFromDB();
    }

    @RequestMapping("/configjd")
    public StringModel configJD(@RequestParam String signal) {
        StringModel stringModel = new StringModel();
        if ("0".equals(signal) || "1".equals(signal)) {
            String sql = "update config set jd = ? where id = 1";
            if (jdbcTemplate.update(sql, signal) == 1) {
                stringModel.setString(signal);
            } else {
                stringModel.setString("-1");
            }
        } else {
            stringModel.setString("-1");
        }
        return stringModel;
    }

    @RequestMapping("/configtb")
    public StringModel configTB(@RequestParam String signal) {
        StringModel stringModel = new StringModel();
        if ("0".equals(signal) || "1".equals(signal)) {
            String sql = "update config set tb = ? where id = 1";
            if (jdbcTemplate.update(sql, signal) == 1) {
                stringModel.setString(signal);
            } else {
                stringModel.setString("-1");
            }
        } else {
            stringModel.setString("-1");
        }
        return stringModel;
    }

    private List<UserModel> queryUsers(int page) throws SQLException {
        String querySql = "select * from user limit "+(page-1)*20+",20;";
        return getUserModels(querySql);
    }

    private List<UserModel> queryAdmins(int page) throws SQLException {
        String querySql = "select * from admin where type = 1 limit "+(page-1)*20+",20;";
        return getAdminModels(querySql);
    }

    private List<UserModel> getAdminModels(String querySql){
        return jdbcTemplate.query(querySql, new RowMapper<UserModel>() {
            @Override
            public UserModel mapRow(ResultSet resultSet, int i) throws SQLException {
                UserModel userModel = new UserModel();
                userModel.setUid(resultSet.getInt("uid"));
                userModel.setAccount(resultSet.getString("account"));
                userModel.setState(resultSet.getLong("state"));
                return userModel;
            }
        });
    }

    private List<UserModel> getUserModels(String querySql){
        return jdbcTemplate.query(querySql, new RowMapper<UserModel>() {
            @Override
            public UserModel mapRow(ResultSet resultSet, int i) throws SQLException {
                UserModel userModel = new UserModel();
                userModel.setUid(resultSet.getInt("uid"));
                userModel.setAccount(resultSet.getString("account"));
                userModel.setUname(resultSet.getString("name"));
                userModel.setPhone(resultSet.getString("phone"));
                return userModel;
            }
        });
    }

    private LoginModel getState(String sql) {
        return jdbcTemplate.queryForObject(sql, new RowMapper<LoginModel>() {
            @Override
            public LoginModel mapRow(ResultSet resultSet, int i) throws SQLException {
                LoginModel loginModel = new LoginModel();
                loginModel.setState(resultSet.getLong("state"));
                return loginModel;
            }
        });
    }

    private ConfigModel getConfigFromDB() {
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

}
