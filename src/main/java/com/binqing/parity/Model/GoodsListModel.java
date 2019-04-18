package com.binqing.parity.Model;

import java.util.List;

public class GoodsListModel {
    private int maxPage;

    private List<ParityModel> parityGoodsList;

    private List<GoodsModel> goodsModelList;


    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public List<ParityModel> getParityGoodsList() {
        return parityGoodsList;
    }

    public void setParityGoodsList(List<ParityModel> parityGoodsList) {
        this.parityGoodsList = parityGoodsList;
    }

    public List<GoodsModel> getGoodsModelList() {
        return goodsModelList;
    }

    public void setGoodsModelList(List<GoodsModel> goodsModelList) {
        this.goodsModelList = goodsModelList;
    }
}
