package com.example.settings;

public class Contact {
    String id;
    String name;
    String phoneNumber;
    String email;
    String note;
    String address;
    String InstantMessenger;
    String Organizations;


    public void setId(String id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInstantMessenger() {
        return InstantMessenger;
    }

    public void setInstantMessenger(String instantMessenger) {
        InstantMessenger = instantMessenger;
    }

    public String getOrganizations() {
        return Organizations;
    }

    public void setOrganizations(String organizations) {
        Organizations = organizations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
