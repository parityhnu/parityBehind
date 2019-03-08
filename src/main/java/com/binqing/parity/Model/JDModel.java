package com.binqing.parity.Model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "goods_jd")
public class JDModel extends GoodsModel{

    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
