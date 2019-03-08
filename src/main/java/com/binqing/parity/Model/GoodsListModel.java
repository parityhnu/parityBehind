package com.binqing.parity.Model;

import java.util.List;

public class GoodsListModel {
    private JDModel parityJdModel;

    private TBModel parityTbModel;

    private List<JDModel> jdModelList;

    private List<TBModel> tbModelList;

    public JDModel getParityJdModel() {
        return parityJdModel;
    }

    public void setParityJdModel(JDModel parityJdModel) {
        this.parityJdModel = parityJdModel;
    }

    public TBModel getParityTbModel() {
        return parityTbModel;
    }

    public void setParityTbModel(TBModel parityTbModel) {
        this.parityTbModel = parityTbModel;
    }

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
