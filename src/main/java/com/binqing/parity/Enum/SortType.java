package com.binqing.parity.Enum;

public enum SortType {
    INIT(0),
    SALE_COMMENT_DESC(1),
    PRICE_ASC(2),
    PRICE_DESC(3);

    private int mValue;
    SortType(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }
}
