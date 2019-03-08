package com.binqing.parity.Model;

public class GoodsModel {
    private String name;

    private String price;

    private String href;

    private String image;

    private String keyword;

    private int page;

    private String shop;

    private double score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getSaleOrComment() {
        if (this instanceof TBModel) {
            String sale = ((TBModel) this).getSale();
            return Integer.parseInt(sale.substring(0, sale.length()-3));
        } else if (this instanceof JDModel) {
            return Integer.parseInt(((JDModel) this).getComment());
        }
        return 0;
    }
}
