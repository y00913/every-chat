package com.example.everychat.enums;

public enum MessageTypeEnum {
    MESSAGE("message"),
    ENTER("enter"),
    LEAVE("leave"),
    COUNT("count");
    private final String label;

    MessageTypeEnum (String label){
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
