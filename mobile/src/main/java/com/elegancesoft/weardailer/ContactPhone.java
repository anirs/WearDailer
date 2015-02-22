package com.elegancesoft.weardailer;

/**
 * Created by Imran on 2015-02-05.
 */
public class ContactPhone {
    private String type;
    private String number;

    public ContactPhone(String number) {
        this.number = number;
    }

    public ContactPhone(String type, String number) {
        this.type = type;
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
