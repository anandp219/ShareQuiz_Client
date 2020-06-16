package com.sharequiz.sharequiz.models;

import java.io.Serializable;

public class Room implements Serializable {
    private String room;
    private String phoneNumber;

    public Room(String room, String phoneNumber) {
        this.room = room;
        this.phoneNumber = phoneNumber;
    }

    public Room() {
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
