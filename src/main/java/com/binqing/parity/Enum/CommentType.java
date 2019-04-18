package com.binqing.parity.Enum;

public enum CommentType {

    JD(0),
    TAOBAO(1),
    TMALL(2);

    private int mValue;
    CommentType(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }
}
