package com.binqing.parity.Model;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 1.活跃用户数量（过去一周）
 * 2.搜索量（过去一周）
 * 3.排序方式的量（过去一周）
 * 3.1 排序方式的总量
 * 4.关键字的量（过去一周）
 * 4.1 关键字的总量
 * 5.收藏功能的使用数量
 */
public class InformationModel {

    private Map<Date, Integer> activeUserWeek;

    private Map<Date, Integer> searchWeek;

    private List<String_CountModel> sortWeek;

    private List<String_CountModel> sortAll;

    private List<String_CountModel> keywordWeek;

    private List<String_CountModel> keywordAll;

    private int favoriteCount;

    public Map<Date, Integer> getActiveUserWeek() {
        return activeUserWeek;
    }

    public void setActiveUserWeek(Map<Date, Integer> activeUserWeek) {
        this.activeUserWeek = activeUserWeek;
    }

    public Map<Date, Integer> getSearchWeek() {
        return searchWeek;
    }

    public void setSearchWeek(Map<Date, Integer> searchWeek) {
        this.searchWeek = searchWeek;
    }

    public List<String_CountModel> getSortWeek() {
        return sortWeek;
    }

    public void setSortWeek(List<String_CountModel> sortWeek) {
        this.sortWeek = sortWeek;
    }

    public List<String_CountModel> getSortAll() {
        return sortAll;
    }

    public void setSortAll(List<String_CountModel> sortAll) {
        this.sortAll = sortAll;
    }

    public List<String_CountModel> getKeywordWeek() {
        return keywordWeek;
    }

    public void setKeywordWeek(List<String_CountModel> keywordWeek) {
        this.keywordWeek = keywordWeek;
    }

    public List<String_CountModel> getKeywordAll() {
        return keywordAll;
    }

    public void setKeywordAll(List<String_CountModel> keywordAll) {
        this.keywordAll = keywordAll;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }
}
