package com.binqing.parity.Model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(value = "comments_tb")
public class TBCommentModel extends BaseCommentModel{

    private String auctionSku;

    private List<String> attendpics;

    public String getAuctionSku() {
        return auctionSku;
    }

    public void setAuctionSku(String auctionSku) {
        this.auctionSku = auctionSku;
    }

    public List<String> getAttendpics() {
        return attendpics;
    }

    public void setAttendpics(List<String> attendpics) {
        this.attendpics = attendpics;
    }
}
