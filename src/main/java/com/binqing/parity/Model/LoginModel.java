package com.binqing.parity.Model;

public class LoginModel {
    private String account;

    private String password;

    private String salt;

    private String state;

    private Integer wrongtimes;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getWrongtimes() {
        return wrongtimes;
    }

    public void setWrongtimes(Integer wrongtimes) {
        this.wrongtimes = wrongtimes;
    }
}
