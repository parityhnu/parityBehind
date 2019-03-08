package com.binqing.parity.Model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "goods_tb")
public class TBModel extends GoodsModel{
    private String sale;

    public String getSale() {
        return sale;
    }

    public void setSale(String sale) {
        this.sale = sale;
    }
}
