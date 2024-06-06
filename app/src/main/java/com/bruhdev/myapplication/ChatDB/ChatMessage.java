package com.bruhdev.myapplication.ChatDB;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_history")
public class ChatMessage {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String address;
    private boolean sentTo;
    private String message;
    private long timestamp;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isSentTo() {
        return sentTo;
    }

    public void setSentTo(boolean sentTo) {
        this.sentTo = sentTo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

