package com.binqing.parity.Enum;

public enum LoginStatus {
    WRONG(-1),
    WRONG_TOMANY_TIMES(-2);

    private int mValue;
    LoginStatus(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }
}
