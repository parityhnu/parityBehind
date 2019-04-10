package com.binqing.parity.Model;

import java.util.List;

public class BaseCommentModel implements Comparable<BaseCommentModel> {
    private String gid;

    private int index;

    private long ctime;

    private String rateContent;

    private List<String> pics;

    private String displayUserNick;

    private String content;

    public String getGid() {
        if (this instanceof JDCommentModel) {
            return "jd:" + gid;
        } else if (this instanceof TBCommentModel) {
            return "tb:" + gid;
        } else if (this instanceof TMCommentModel) {
            return "tm:" + gid;
        }
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public String getRateContent() {
        return rateContent;
    }

    public void setRateContent(String rateContent) {
        this.rateContent = rateContent;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public String getDisplayUserNick() {
        return displayUserNick;
    }

    public void setDisplayUserNick(String displayUserNick) {
        this.displayUserNick = displayUserNick;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int compareTo(BaseCommentModel o) {
        return (int) (this.ctime - o.ctime);
    }
}