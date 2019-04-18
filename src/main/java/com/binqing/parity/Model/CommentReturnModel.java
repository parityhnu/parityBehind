package com.binqing.parity.Model;

import java.util.List;

public class CommentReturnModel {

    private int maxPage;

    private List<JDCommentModel> jdCommentModels;

    private List<TBCommentModel> tbCommentModels;

    private List<TMCommentModel> tmCommentModels;

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public List<JDCommentModel> getJdCommentModels() {
        return jdCommentModels;
    }

    public void setJdCommentModels(List<JDCommentModel> jdCommentModels) {
        this.jdCommentModels = jdCommentModels;
    }

    public List<TBCommentModel> getTbCommentModels() {
        return tbCommentModels;
    }

    public void setTbCommentModels(List<TBCommentModel> tbCommentModels) {
        this.tbCommentModels = tbCommentModels;
    }

    public List<TMCommentModel> getTmCommentModels() {
        return tmCommentModels;
    }

    public void setTmCommentModels(List<TMCommentModel> tmCommentModels) {
        this.tmCommentModels = tmCommentModels;
    }
}
