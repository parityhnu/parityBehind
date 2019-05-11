package com.binqing.parity.Model;

public class String_CountModel {
    private String string;

    private int count;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getSort() {
        switch (this.string) {
            case "0":
                return "默认排序";
            case "1":
                return "销量排序";
            case "2":
                return "价格升序";
            case "3":
                return "价格降序";
                default:
                    return "默认排序";
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
