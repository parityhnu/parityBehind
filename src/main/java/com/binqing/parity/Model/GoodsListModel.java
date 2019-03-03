package com.binqing.parity.Model;

import java.util.List;

public class GoodsListModel {
    private List<JDModel> jdModelList;

    private List<TBModel> tbModelList;

    public List<JDModel> getJdModelList() {
        return jdModelList;
    }

    public void setJdModelList(List<JDModel> jdModelList) {
        this.jdModelList = jdModelList;
    }

    public List<TBModel> getTbModelList() {
        return tbModelList;
    }

    public void setTbModelList(List<TBModel> tbModelList) {
        this.tbModelList = tbModelList;
    }
}
