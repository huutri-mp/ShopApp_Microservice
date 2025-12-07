package com.example.paymentservce.constan;

public enum Locale {
    VIETNAM("vn"),
    ENGLISH("en");

    private final String code;

    Locale(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
