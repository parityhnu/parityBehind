package com.binqing.parity.Controller;

import com.binqing.parity.Consts.TimeConsts;
import com.binqing.parity.Enum.LoginStatus;
import com.binqing.parity.Model.LoginModel;
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

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
//
//    @GetMapping("/testall")
//    public List<UserModel> testall() throws InvalidKeySpecException, NoSuchAlgorithmException {
//        List<UserModel> list = new ArrayList<>();
//        list.add(register("qing123","123456","13511476510"));
//        list.add(login("qing123","123456"));
//        return list;
//    }
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
        String sqlCheck = "select * from login where account =?" ;
        LoginModel loginModel = validatePasswordBysql(sqlCheck, new String[]{account});
        if (loginModel != null) {
            return null;
        }
        String sql2 = "insert into user(account, name, phone) values (?,?,?);";
        jdbcTemplate.update(sql2, account, "Parity_" + account, phone);
        UserModel userModel = queryUserByAccount(account);
        String salt =createSalt();
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
        String sql = "select * from login where account =?" ;
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
                        if (PasswordHash.validatePassword( password + loginModel.getSalt(), loginModel.getPassword())) {
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
                    if (PasswordHash.validatePassword( password + loginModel.getSalt(), loginModel.getPassword())) {
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

    @PostMapping("/requestPhone")
    public String requestPhone(@RequestParam String user) {
        if (user == null || "".equals(user)) {
            return "";
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
            return "";
        }
        return phone;
    }

    //todo 有空的话 把这些错误清况整理一下...
    @PostMapping("/modify")
    public String modify(HttpServletRequest request, @RequestParam String user, @RequestParam String s1, @RequestParam(value = "s2",required = false) String s2, @RequestParam String modifyType) throws InvalidKeySpecException, NoSuchAlgorithmException {
        System.out.println(user + "_" + modifyType + "_" + s1);
        HttpSession session = request.getSession();
        String sql;
        switch (modifyType) {
            case "0":
                if (s1 == null || "".equals(s1)) {
                    return "";
                }
                sql = "update user set name = ? where uid = ?";
                if (jdbcTemplate.update(sql, s1, user) == 1) {
                    session.setAttribute("name", s1);
                    return s1;
                } else {
                    return "";
                }
            case "1":
                if (s1 == null || "".equals(s1) || s2 == null || "".equals(s2)) {
                    return "";
                }
                if (s1.equals(s2)) {
                    return s2;
                }
                if (s1.equals(requestPhone(user))) {
                    sql = "update user set phone = ? where uid = ?";
                    if (jdbcTemplate.update(sql, s2, user) == 1) {
                        StringBuilder builder = new StringBuilder(s2);
                        builder.replace(3, 7, "****");
                        session.setAttribute("ph", builder.toString());
                        return s2;
                    } else {
                        return "";
                    }
                }
            case "2":
                if (s1 == null || "".equals(s1) || s2 == null || "".equals(s2)) {
                    return "";
                }
                if (s1.equals(s2)) {
                    return s2;
                }
                sql = "select * from login where uid =?" ;
                LoginModel loginModel = validatePasswordBysql(sql, new String[]{user});
                if (loginModel != null && PasswordHash.validatePassword( s1 + loginModel.getSalt(), loginModel.getPassword())) {
                    String sql2 = "update login set password = ? , salt = ? where uid = ?";
                    String salt =createSalt();
                    String password = PasswordHash.createHash(s2 + salt);
                    if (jdbcTemplate.update(sql2, password, salt, user) == 1) {
                        return s2;
                    } else {
                        return "";
                    }
                } else {
                    return "";
                }
            default:
                return "";
        }
    }

    @PostMapping("/forgetPassword")
    public String forgetPassword(@RequestParam String account, @RequestParam String phone, @RequestParam String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        System.out.println(account + "_" + phone + "_" + password);
        if (account == null || "".equals(account)) {
            return "-1";
        }
        if (phone == null || "".equals(phone)) {
            return "-1";
        }
        if (password == null || "".equals(password)) {
            return "-1";
        }
        String sql = "select phone from user where account =?" ;
        try {
            String phone_check = jdbcTemplate.queryForObject(sql, new String[]{account}, (resultSet, i) -> resultSet.getString("phone"));
            if (phone.equals(phone_check)) {
                String sql2 = "update login set password = ? , salt = ? where account = ?";
                String salt =createSalt();
                password = PasswordHash.createHash(password + salt);
                if (jdbcTemplate.update(sql2, password, salt, account) == 1) {
                    return account;
                } else {
                    return "-1";
                }
            } else {
                return "-1";
            }
        }catch (Exception e) {
            return "-1";
        }
    }


    private String createSalt() {
        byte [] values = new byte[128];
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

    private LoginModel validatePasswordBysql(String sql, String [] columns) {
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
        }catch (Exception e) {
            return null;
        }
    }

}
