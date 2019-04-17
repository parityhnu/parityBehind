package com.binqing.parity.Controller;

import com.binqing.parity.Consts.TimeConsts;
import com.binqing.parity.Enum.LoginStatus;
import com.binqing.parity.Model.LoginModel;
import com.binqing.parity.Model.ParityModel;
import com.binqing.parity.Model.StringModel;
import com.binqing.parity.Model.UserModel;
import com.binqing.parity.PasswordHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //
    @GetMapping("/test")
    public StringModel testall(@RequestParam String user) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return requestPhone(user);
    }
//
//    @GetMapping("/testlogin")
//    public List<UserModel> testLogin() throws InvalidKeySpecException, NoSuchAlgorithmException {
//        List<UserModel> list = new ArrayList<>();
//        list.add(login("qing123","123123")); // 正确
//        list.add(login("qing123","123123123123123"));
//        list.add(login("qing123","123123123123123"));
//        list.add(login("qing123","123123123123123"));
//        list.add(login("qing123","123123123123123"));
//        list.add(login("qing123","123123123123123"));
//        list.add(login("qing123","123123123123123"));
//        list.add(login("qing123","123123123123123"));
//        return list;
//    }

//    @GetMapping("/testmodify")
//    public List<String> testmodify() throws InvalidKeySpecException, NoSuchAlgorithmException {
//        List<String>list = new ArrayList<>();
//        list.add(modify("22","123",null,"0"));
//        list.add(modify("22","1351147651","15074969752","1"));
//        list.add(modify("22","13511476510","15074969752","1"));
//        list.add(modify("22","123","123123","2"));
//        list.add(modify("22","123456","123123","2"));
//        return list;
//    }

    @PostMapping("/register")
    public UserModel register(@RequestParam String account, @RequestParam String password, @RequestParam String phone) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String sqlCheck = "select * from login where account =?";
        LoginModel loginModel = validatePasswordBysql(sqlCheck, new String[]{account});
        if (loginModel != null) {
            return null;
        }
        String sql2 = "insert into user(account, name, phone) values (?,?,?);";
        jdbcTemplate.update(sql2, account, "Parity_" + account, phone);
        UserModel userModel = queryUserByAccount(account);
        String salt = createSalt();
        password = PasswordHash.createHash(password + salt);
        String sql = "insert into login(account, password, salt, state, wrongtimes, uid) VALUES (?, ?, ?, ?, 0, ?);";
        jdbcTemplate.update(sql, account, password, salt, System.currentTimeMillis(), userModel.getUid());
        return userModel;
    }

    @PostMapping("/login")
    public UserModel login(HttpServletRequest request, @RequestParam String account, @RequestParam String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        HttpSession session = request.getSession(true);
        // 密码错误时check state，增加wrongtimes并更新state state显示状态 仅当为0时才会返回登陆成功 显示为时间戳时需要对wrongtimes进行计算，超过3次则不允许登陆
        // 接上，密码错误和不允许登陆需要返回不同的错误码（可以附在uid上 -1 -2）
        String sql = "select * from login where account =?";
        UserModel result = new UserModel();
        try {
            LoginModel loginModel = validatePasswordBysql(sql, new String[]{account});
            System.out.println(loginModel);
            if (loginModel == null) {
                result.setUid(LoginStatus.WRONG.getValue());
            } else {
                boolean isNeedUpdateState = true;
                long state = loginModel.getState();
                int wrongtimes = loginModel.getWrongtimes();
                if (wrongtimes >= 3) {
                    //错误超过3次，如未到5分钟则不做操作。如果到了5分钟，那么继续错误的话就设为1并更新state
                    if (System.currentTimeMillis() - state > TimeConsts.MILLS_OF_ONE_MINUTE * 5) {
                        if (PasswordHash.validatePassword(password + loginModel.getSalt(), loginModel.getPassword())) {
                            //清空次数
                            wrongtimes = 0;
                            result = queryUserByAccount(account);
                            setSession(session, result);
                        } else {
                            //设置次数为1，并且更新state
                            wrongtimes = 1;
                            result.setUid(LoginStatus.WRONG.getValue());
                        }
                    } else {
                        isNeedUpdateState = false;
                        result.setUid(LoginStatus.WRONG_TOMANY_TIMES.getValue());
                    }
                } else {
                    //错误次数没有超过3次,如果对了就清空，如果不对就+1
                    if (PasswordHash.validatePassword(password + loginModel.getSalt(), loginModel.getPassword())) {
                        //清空次数
                        wrongtimes = 0;
                        result = queryUserByAccount(account);
                        setSession(session, result);
                    } else {
                        wrongtimes += 1;
                        result.setUid(LoginStatus.WRONG.getValue());
                    }
                }
                if (isNeedUpdateState) {
                    state = System.currentTimeMillis();
                    sql = "update login set state = ?, wrongtimes = ? where account = ?";
                    jdbcTemplate.update(sql, state, wrongtimes, account);
                } else {
                    sql = "update login set  wrongtimes = ? where account = ?";
                    jdbcTemplate.update(sql, wrongtimes, account);
                }
            }
            return result;
        } catch (Exception e) {
            result.setUid(LoginStatus.WRONG.getValue());
            return result;
        }
    }

    private void setSession(HttpSession session, UserModel result) {
        session.setAttribute("user", result.getUid());
        session.setAttribute("name", result.getUname());
        StringBuilder builder = new StringBuilder(result.getPhone());
        builder.replace(3, 7, "****");
        session.setAttribute("ph", builder.toString());
    }

    @PostMapping("/requestName")
    public StringModel requestName(@RequestParam String user) {
        if (user == null || "".equals(user)) {
            return null;
        }
        String name = "";
        String sql = "select name from user where uid = ?";
        try {
            name = jdbcTemplate.queryForObject(sql, new String[]{user}, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet resultSet, int i) throws SQLException {
                    String phone = resultSet.getString("name");
                    return phone;
                }
            });
        } catch (Exception e) {
            return null;
        }
        return new StringModel(name);
    }

    @PostMapping("/requestPhone")
    public StringModel requestPhone(@RequestParam String user) {
        if (user == null || "".equals(user)) {
            return null;
        }
        String phone = "";
        String sql = "select phone from user where uid = ?";
        try {
            phone = jdbcTemplate.queryForObject(sql, new String[]{user}, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet resultSet, int i) throws SQLException {
                    String phone = resultSet.getString("phone");
                    return phone;
                }
            });
        } catch (Exception e) {
            return null;
        }
        return new StringModel(phone);
    }

    //todo 有空的话 把这些错误清况整理一下...
    @PostMapping("/modify")
    public StringModel modify(HttpServletRequest request, @RequestParam String user, @RequestParam String s1, @RequestParam(value = "s2", required = false) String s2, @RequestParam String modifyType) throws InvalidKeySpecException, NoSuchAlgorithmException {
        System.out.println(user + "_" + modifyType + "_" + s1);
        HttpSession session = request.getSession();
        String sql;
        switch (modifyType) {
            case "0":
                if (s1 == null || "".equals(s1)) {
                    return null;
                }
                sql = "update user set name = ? where uid = ?";
                if (jdbcTemplate.update(sql, s1, user) == 1) {
                    session.setAttribute("name", s1);
                    return new StringModel(s1);
                } else {
                    return null;
                }
            case "1":
                if (s1 == null || "".equals(s1) || s2 == null || "".equals(s2)) {
                    return null;
                }
                if (s1.equals(s2)) {
                    return new StringModel(s2);
                }
                if (s1.equals(requestPhone(user).getString())) {
                    sql = "update user set phone = ? where uid = ?";
                    if (jdbcTemplate.update(sql, s2, user) == 1) {
                        StringBuilder builder = new StringBuilder(s2);
                        builder.replace(3, 7, "****");
                        session.setAttribute("ph", builder.toString());
                        return new StringModel(s2);
                    } else {
                        return null;
                    }
                }
            case "2":
                if (s1 == null || "".equals(s1) || s2 == null || "".equals(s2)) {
                    return null;
                }
                if (s1.equals(s2)) {
                    return new StringModel(s2);
                }
                sql = "select * from login where uid =?";
                LoginModel loginModel = validatePasswordBysql(sql, new String[]{user});
                if (loginModel != null && PasswordHash.validatePassword(s1 + loginModel.getSalt(), loginModel.getPassword())) {
                    String sql2 = "update login set password = ? , salt = ? where uid = ?";
                    String salt = createSalt();
                    String password = PasswordHash.createHash(s2 + salt);
                    if (jdbcTemplate.update(sql2, password, salt, user) == 1) {
                        return new StringModel(s2);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            default:
                return null;
        }
    }

    @PostMapping("/forgetPassword")
    public StringModel forgetPassword(@RequestParam String account, @RequestParam String phone, @RequestParam String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        System.out.println(account + "_" + phone + "_" + password);
        if (account == null || "".equals(account)) {
            return new StringModel("-1");
        }
        if (phone == null || "".equals(phone)) {
            return new StringModel("-1");
        }
        if (password == null || "".equals(password)) {
            return new StringModel("-1");
        }
        String sql = "select phone from user where account =?";
        try {
            String phone_check = jdbcTemplate.queryForObject(sql, new String[]{account}, (resultSet, i) -> resultSet.getString("phone"));
            if (phone.equals(phone_check)) {
                String sql2 = "update login set password = ? , salt = ? where account = ?";
                String salt = createSalt();
                password = PasswordHash.createHash(password + salt);
                if (jdbcTemplate.update(sql2, password, salt, account) == 1) {
                    return new StringModel(account);
                } else {
                    return new StringModel("-1");
                }
            } else {
                return new StringModel("-1");
            }
        } catch (Exception e) {
            return new StringModel("-1");
        }
    }

    @GetMapping("/favorite")
    public StringModel favorite(@RequestParam String user,  @RequestParam String id1, @RequestParam String id2,
                                @RequestParam String keyword, @RequestParam String sort, @RequestParam boolean cancel) {
        if (user == null || "".equals(user) || id1 == null || "".equals(id1) || id2 == null || "".equals(id2)
                || keyword == null || "".equals(keyword) || sort == null || "".equals(sort)) {
            return null;
        }
        String sql;
        if (cancel) {
            sql = "delete from favorite where uid = ? and id1 = ? and id2 = ? and keyword = ? and sort = ?;";
        } else {
            sql = "insert into favorite(uid, id1, id2, keyword, sort) VALUES (?, ?, ?, ?, ?);";
        }
        if (jdbcTemplate.update(sql, user, id1, id2, keyword, sort) == 1) {
            return new StringModel(id1 + "_" + cancel);
        }
        return null;
    }

    private String createSalt() {
        byte[] values = new byte[128];
        SecureRandom random = new SecureRandom();
        random.nextBytes(values);
        String salt = String.valueOf(values);
        return salt;
    }


    private UserModel queryUserByAccount(String account) {
        String querySql = "select * from user where account = '" + account + "';";
        return jdbcTemplate.queryForObject(querySql, (resultSet, i) -> {
            UserModel userModel1 = new UserModel();
            userModel1.setUid(resultSet.getInt(1));
            userModel1.setAccount(resultSet.getString(2));
            userModel1.setUname(resultSet.getString(3));
            userModel1.setPhone(resultSet.getString(4));
            return userModel1;
        });
    }

    private LoginModel validatePasswordBysql(String sql, String[] columns) {
        LoginModel loginModel;
        try {
            loginModel = jdbcTemplate.queryForObject(sql, columns, (resultSet, i) -> {
                LoginModel loginModel1 = new LoginModel();
                loginModel1.setAccount(resultSet.getString("account"));
                loginModel1.setPassword(resultSet.getString("password"));
                loginModel1.setSalt(resultSet.getString("salt"));
                loginModel1.setState(resultSet.getLong("state"));
                loginModel1.setWrongtimes(resultSet.getInt("wrongtimes"));
                loginModel1.setUid(resultSet.getInt("uid"));
                return loginModel1;
            });
            return loginModel;
        } catch (Exception e) {
            return null;
        }
    }

}
