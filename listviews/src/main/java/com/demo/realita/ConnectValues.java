package com.demo.realita;

public enum ConnectValues {
    AZUREMOBILEURL("https://realita-cz-demo.azure-mobile.net/"),
    AZUREMOBILEPWD("xEcspiKVLSUEJJaPRBEJnElbiQluGx23");

    private String text = "";

    ConnectValues(String s) {
        this.text = s;
    }

    public String toString() {
        return text;
    }
}
