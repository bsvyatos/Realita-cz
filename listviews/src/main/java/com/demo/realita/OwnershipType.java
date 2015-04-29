package com.demo.realita;

public enum OwnershipType {
    personal,
    mutual,
    cooperative,
    lease,
    municipal,
    rest;

    private String text = "";

    public void setString(String t) {
        this.text = t;
    }
    @Override
    public String toString() {
        return text;
    }
}

