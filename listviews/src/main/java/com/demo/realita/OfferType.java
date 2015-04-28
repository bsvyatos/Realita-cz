package com.demo.realita;

public enum OfferType {
    SALE,
    RENT,
    MATE;

    private String text = "";

    public void setString(String t) {
        this.text = t;
    }
    @Override
    public String toString() {
        return text;
    }
}

