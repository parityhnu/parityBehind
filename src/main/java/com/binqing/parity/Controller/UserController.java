package com.binqing.parity.Controller;

import com.binqing.parity.Consts.TimeConsts;
import com.binqing.parity.Enum.LoginStatus;
import com.binqing.parity.Model.*;
import com.binqing.parity.PasswordHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private static final String REDIS_URL = "redis_url_login_count";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //
    @GetMapping("/test")
    public void testall() throws InvalidKeySpecException, NoSuchAlgorithmException {
        saveLogin(31);
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
        LoginModel loginModel = queryLoginModel(sqlCheck, new String[]{account});
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
            LoginModel loginModel = queryLoginModel(sql, new String[]{account});
            System.out.println(loginModel);
            if (loginModel == null) {
                result.setUid(LoginStatus.WRONG.getValue());
            } else {
                boolean isNeedUpdateState = true;
                long state = loginModel.getState();
                if (state < 0) {
                    result.setUid(LoginStatus.BAN.getValue());
                    return result;
                }
                int wrongtimes = loginModel.getWrongtimes();
                if (wrongtimes >= 3) {
                    //错误超过3次，如未到5分钟则不做操作。如果到了5分钟，那么继续错误的话就设为1并更新state
                    if (System.currentTimeMillis() - state > TimeConsts.MILLS_OF_ONE_MINUTE * 5) {
                        if (PasswordHash.validatePassword(password + loginModel.getSalt(), loginModel.getPassword())) {
                            //清空次数
                            wrongtimes = 0;
                            result = queryUserByAccount(account);
                            saveLogin(result.getUid());
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
                        saveLogin(result.getUid());
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

    @RequestMapping("/admin/register")
    public AdminLoginModel adminRegister(HttpServletRequest request, @RequestParam String account, @RequestParam String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        HttpSession session = request.getSession(true);
        AdminLoginModel result = new AdminLoginModel();
        if (session.getAttribute("admin") == null || "".equals(session.getAttribute("admin"))) {
            result.setAccount("-1");
            return result;
        }
        String sqlCheck = "select * from admin where account =?";
        result = queryAdminLoginModel(sqlCheck, new String[]{account});
        if (result != null) {
            result.setAccount("-1");
            return result;
        }
        String salt = createSalt();
        password = PasswordHash.createHash(password + salt);
        String sql = "insert into admin(account, password, salt, state, wrongtimes, type) VALUES (?, ?, ?, ?, 0, 1);";
        jdbcTemplate.update(sql, account, password, salt, System.currentTimeMillis());
        result = queryAdminLoginModel(sqlCheck, new String[]{account});
        return result;
    }

    @PostMapping("/admin/login")
    public AdminLoginModel adminLogin(HttpServletRequest request, @RequestParam String account, @RequestParam String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        HttpSession session = request.getSession(true);
        // 密码错误时check state，增加wrongtimes并更新state state显示状态 仅当为0时才会返回登陆成功 显示为时间戳时需要对wrongtimes进行计算，超过3次则不允许登陆
        // 接上，密码错误和不允许登陆需要返回不同的错误码（可以附在uid上 -1 -2）
        String sql = "select * from admin where account =?";
        AdminLoginModel result = new AdminLoginModel();
        try {
            AdminLoginModel adminLoginModel = queryAdminLoginModel(sql, new String[]{account});
            System.out.println(adminLoginModel);
            if (adminLoginModel == null) {
                result.setUid(LoginStatus.WRONG.getValue());
            } else {
                boolean isNeedUpdateState = true;
                long state = adminLoginModel.getState();
                if (state < 0) {
                    result.setUid(LoginStatus.BAN.getValue());
                    return result;
                }
                int wrongtimes = adminLoginModel.getWrongtimes();
                if (wrongtimes >= 3) {
                    //错误超过3次，如未到5分钟则不做操作。如果到了5分钟，那么继续错误的话就设为1并更新state
                    if (System.currentTimeMillis() - state > TimeConsts.MILLS_OF_ONE_MINUTE * 5) {
                        if (PasswordHash.validatePassword(password + adminLoginModel.getSalt(), adminLoginModel.getPassword())) {
                            //清空次数
                            wrongtimes = 0;
                            result.setUid(1);
                            session.setAttribute("admin", adminLoginModel.getType());
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
                    if (PasswordHash.validatePassword(password + adminLoginModel.getSalt(), adminLoginModel.getPassword())) {
                        //清空次数
                        wrongtimes = 0;
                        result.setUid(1);
                        session.setAttribute("admin", adminLoginModel.getType());
                    } else {
                        wrongtimes += 1;
                        result.setUid(LoginStatus.WRONG.getValue());
                    }
                }
                if (isNeedUpdateState) {
                    state = System.currentTimeMillis();
                    sql = "update admin set state = ?, wrongtimes = ? where account = ?";
                    jdbcTemplate.update(sql, state, wrongtimes, account);
                } else {
                    sql = "update admin set  wrongtimes = ? where account = ?";
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
        StringModel stringModel = new StringModel();
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
        stringModel.setString(name);
        return stringModel;
    }

    @PostMapping("/requestPhone")
    public StringModel requestPhone(@RequestParam String user) {
        StringModel stringModel = new StringModel();
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
        stringModel.setString(phone);
        return stringModel;
    }

    //todo 有空的话 把这些错误清况整理一下...
    @PostMapping("/modify")
    public StringModel modify(HttpServletRequest request, @RequestParam String user, @RequestParam String s1, @RequestParam(value = "s2", required = false) String s2, @RequestParam String modifyType) throws InvalidKeySpecException, NoSuchAlgorithmException {
        System.out.println(user + "_" + modifyType + "_" + s1);
        StringModel stringModel = new StringModel();
        HttpSession session = request.getSession();
        String sql;
        switch (modifyType) {
            case "0"://修改昵称
                if (s1 == null || "".equals(s1)) {
                    return null;
                }
                sql = "update user set name = ? where uid = ?";
                if (jdbcTemplate.update(sql, s1, user) == 1) {
                    session.setAttribute("name", s1);
                    stringModel.setString(s1);
                    return stringModel;
                } else {
                    return null;
                }
            case "1":
                if (s1 == null || "".equals(s1) || s2 == null || "".equals(s2)) {
                    return null;
                }
                if (s1.equals(s2)) {
                    stringModel.setString(s2);
                    return stringModel;
                }
                if (s1.equals(requestPhone(user).getString())) {
                    sql = "update user set phone = ? where uid = ?";
                    if (jdbcTemplate.update(sql, s2, user) == 1) {
                        StringBuilder builder = new StringBuilder(s2);
                        builder.replace(3, 7, "****");
                        session.setAttribute("ph", builder.toString());
                        stringModel.setString(s2);
                        return stringModel;
                    } else {
                        return null;
                    }
                }
            case "2"://修改密码
                if (s1 == null || "".equals(s1) || s2 == null || "".equals(s2)) {
                    return null;
                }
                if (s1.equals(s2)) {
                    stringModel.setString(s2);
                    return stringModel;
                }
                sql = "select * from login where uid =?";
                LoginModel loginModel = queryLoginModel(sql, new String[]{user});
                if (loginModel != null && PasswordHash.validatePassword(s1 + loginModel.getSalt(), loginModel.getPassword())) {
                    String sql2 = "update login set password = ? , salt = ? where uid = ?";
                    String salt = createSalt();
                    String password = PasswordHash.createHash(s2 + salt);
                    if (jdbcTemplate.update(sql2, password, salt, user) == 1) {
                        stringModel.setString(s2);
                        return stringModel;
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
        StringModel stringModel = new StringModel();
        if (account == null || "".equals(account)) {
            stringModel.setString("-1");
            return stringModel;
        }
        if (phone == null || "".equals(phone)) {
            stringModel.setString("-1");
            return stringModel;
        }
        if (password == null || "".equals(password)) {
            stringModel.setString("-1");
            return stringModel;
        }
        String sql = "select phone from user where account =?";
        try {
            String phone_check = jdbcTemplate.queryForObject(sql, new String[]{account}, (resultSet, i) -> resultSet.getString("phone"));
            if (phone.equals(phone_check)) {
                String sql2 = "update login set password = ? , salt = ? where account = ?";
                String salt = createSalt();
                password = PasswordHash.createHash(password + salt);
                if (jdbcTemplate.update(sql2, password, salt, account) == 1) {
                    stringModel.setString(account);
                    return stringModel;
                } else {
                    stringModel.setString("-1");
                    return stringModel;
                }
            } else {
                stringModel.setString("-1");
                return stringModel;
            }
        } catch (Exception e) {
            stringModel.setString("-1");
            return stringModel;
        }
    }

    @PostMapping("/favorite")
    public StringModel favorite(@RequestParam String user,  @RequestParam String id,
                                @RequestParam String name, @RequestParam String sort, @RequestParam boolean cancel) {
        StringModel stringModel = new StringModel();
        if (user == null || "".equals(user) || id == null || "".equals(id)
                || name == null || "".equals(name) || sort == null || "".equals(sort)) {
            stringModel.setString("null");
            return stringModel;
        }
        try {
            id = id.split(":")[1];
        } catch (Exception e) {
            stringModel.setString("null");
            return stringModel;
        }
        String sql;
        if (cancel) {
            sql = "delete from favorite where uid = ? and id = ? and keyword = ? and sort = ?;";
        } else {
            sql = "insert into favorite(uid, id, keyword, sort) VALUES (?, ?, ?, ?);";
        }
        if (jdbcTemplate.update(sql, user, id, name, sort) == 1) {
            stringModel.setString(id + "_" + cancel);
            return stringModel;
        }
        stringModel.setString("null");
        return stringModel;
    }

    @GetMapping("/checkfavorite")
    public StringModel checkfavorite(@RequestParam String user,  @RequestParam String id,
                                @RequestParam String name, @RequestParam String sort) {
        StringModel stringModel = new StringModel();
        if (user == null || "".equals(user) || id == null || "".equals(id)
                || name == null || "".equals(name) || sort == null || "".equals(sort)) {
            stringModel.setString("null");
            return stringModel;
        }
        try {
            saveLogin(Integer.parseInt(user));
            id = id.split(":")[1];
        } catch (Exception e) {
            stringModel.setString("null");
            return stringModel;
        }
        String sql = "SELECT id from favorite where uid = ? and id = ? and keyword = ? and sort = ?;";
        String result = "";
        try {
            result = jdbcTemplate.queryForObject(sql, new String[]{user,id ,name,sort}, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet resultSet, int i) throws SQLException {
                    String id = resultSet.getString("id");
                    return id;
                }
            });
        } catch (Exception e) {
            stringModel.setString("null");
            return stringModel;
        }
        stringModel.setString(result);
        return stringModel;
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

    private LoginModel queryLoginModel(String sql, String[] columns) {
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

    private AdminLoginModel queryAdminLoginModel(String sql, String[] columns) {
        AdminLoginModel adminLoginModel;
        try {
            adminLoginModel = jdbcTemplate.queryForObject(sql, columns, (resultSet, i) -> {
                AdminLoginModel admin = new AdminLoginModel();
                admin.setAccount(resultSet.getString("account"));
                admin.setPassword(resultSet.getString("password"));
                admin.setSalt(resultSet.getString("salt"));
                admin.setState(resultSet.getLong("state"));
                admin.setWrongtimes(resultSet.getInt("wrongtimes"));
                admin.setUid(resultSet.getInt("uid"));
                admin.setType(resultSet.getInt("type"));
                return admin;
            });
            return adminLoginModel;
        } catch (Exception e) {
            return null;
        }
    }

}
