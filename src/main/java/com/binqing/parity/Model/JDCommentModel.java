package com.binqing.parity.Model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comments_jd")
public class JDCommentModel extends BaseCommentModel{
    private String productSize;

    private String productColor;

    private int score;

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public String getProductColor() {
        return productColor;
    }

    public void setProductColor(String productColor) {
        this.productColor = productColor;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
