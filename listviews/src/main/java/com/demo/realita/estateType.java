package com.demo.realita;

public enum EstateType {
    byt,
    dum,
    pozemek,
    garaz,
    kancelar,
    nebytovy,
    rekreacni;

    private String text = "";

    public void setString(String t) {
        this.text = t;
    }
    @Override
    public String toString() {
        return text;
    }
}
