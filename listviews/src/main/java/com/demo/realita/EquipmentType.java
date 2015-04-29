package com.demo.realita;

public enum EquipmentType {
    unequipped,
    partly,
    equipped;

    private String text = "";

    public void setString(String t) {
        this.text = t;
    }
    @Override
    public String toString() {
        return text;
    }
}
