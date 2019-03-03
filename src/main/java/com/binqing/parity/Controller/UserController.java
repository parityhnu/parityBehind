package com.binqing.parity.Controller;

import com.binqing.parity.Model.LoginModel;
import com.binqing.parity.Model.UserModel;
import com.binqing.parity.PasswordHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

@RestController
public class UserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/register")
    public UserModel register(@RequestParam String account, @RequestParam String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte [] values = new byte[128];
        SecureRandom random = new SecureRandom();
        random.nextBytes(values);
        String salt = String.valueOf(values);
        password = PasswordHash.createHash(password + salt);
        String sql = "insert into login(account, password, salt, state, wrongtimes) VALUES (?, ?, ?, '0', 0);";
        jdbcTemplate.update(sql, account, password, salt);
        String sql2 = "insert into user(account, name) values (?,?);";
        jdbcTemplate.update(sql2, account, "Parity" + account);
        String querySql = "select * from user where account = '" + account + "';";
        return queryUserByAccount(account);
    }

    @PostMapping("/login")
    public UserModel login(@RequestParam String account, @RequestParam String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String sql = "select * from login where account =?" ;
        LoginModel loginModel = jdbcTemplate.queryForObject(sql, new String[]{account}, (resultSet, i) -> {
            LoginModel loginModel1 = new LoginModel();
            loginModel1.setAccount(resultSet.getString("account"));
            loginModel1.setPassword(resultSet.getString("password"));
            loginModel1.setSalt(resultSet.getString("salt"));
            loginModel1.setState(resultSet.getString("state"));
            loginModel1.setWrongtimes(resultSet.getInt("wrongtimes"));
            return loginModel1;
        });

        if (PasswordHash.validatePassword( password + loginModel.getSalt(), loginModel.getPassword())) {
            return queryUserByAccount(account);
        } else {
            return null;
        }
    }

    public UserModel queryUserByAccount(String account) {
        String querySql = "select * from user where account = '" + account + "';";
        return jdbcTemplate.queryForObject(querySql, (resultSet, i) -> {
            UserModel userModel1 = new UserModel();
            userModel1.setUid(resultSet.getInt(1));
            userModel1.setAccount(resultSet.getString(2));
            userModel1.setUname(resultSet.getString(3));
            return userModel1;
        });
    }


}
