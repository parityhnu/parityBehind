package com.binqing.parity.Model;

public class AdminLoginModel {
    private String account;

    private String password;

    private String salt;

    private long state;

    private Integer wrongtimes;

    private Integer uid;

    private Integer type;

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

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    public Integer getWrongtimes() {
        return wrongtimes;
    }

    public void setWrongtimes(Integer wrongtimes) {
        this.wrongtimes = wrongtimes;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
