package com.binqing.parity.Model;

import java.util.List;

public class GoodsListModel {
    private int maxPage;

    private List<GoodsModel> parityGoodsList;

    private List<GoodsModel> goodsModelList;


    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public List<GoodsModel> getParityGoodsList() {
        return parityGoodsList;
    }

    public void setParityGoodsList(List<GoodsModel> parityGoodsList) {
        this.parityGoodsList = parityGoodsList;
    }

    public List<GoodsModel> getGoodsModelList() {
        return goodsModelList;
    }

    public void setGoodsModelList(List<GoodsModel> goodsModelList) {
        this.goodsModelList = goodsModelList;
    }
}
