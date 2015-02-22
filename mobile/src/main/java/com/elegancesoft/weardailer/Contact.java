package com.elegancesoft.weardailer;

/**
 * Created by Imran on 2015-02-05.
 */
public class Contact {

    private String id;
    private String name;
    private ContactPhone [] contactPhones;
    public Contact(String id, String name) {
        this.id =id;
        this.name = name;
    }
    public Contact(String id, String name,ContactPhone[] numbers) {
        this(id,name);
        this.contactPhones = numbers;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ContactPhone[] getContactPhones() {
        return contactPhones;
    }
    public void setContactPhones(ContactPhone[] contactPhones) {
        this.contactPhones = contactPhones;
    }
}
