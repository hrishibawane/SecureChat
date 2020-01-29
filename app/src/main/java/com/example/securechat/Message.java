package com.example.securechat;

public class Message {

    private String sender;
    private String message;
    private String time;
    private boolean status;

    public Message() {
    }

    public Message(String sender, String message, String time, boolean status) {
        this.sender = sender;
        this.message = message;
        this.time = time;
        this.status = status;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) { this.message = message; }

    public String getTime() {
        return time;
    }

    public void setTime(String time) { this.time = time; }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

}
