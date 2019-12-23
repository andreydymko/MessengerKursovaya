package com.example.messengerkursovaya.MessagingActivity;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class MessageData {
    private String id;
    private Date date;
    private boolean isReadenByAnother;
    private String msgText;
    private DocumentReference sender;
    private byte isSent;

    MessageData() {}

    public MessageData(String id,
                Date date,
                boolean isReadenByAnother,
                String msgText,
                DocumentReference sender,
                byte isSent) {
        this.id = id;
        this.date = date;
        this.isReadenByAnother = isReadenByAnother;
        this.msgText = msgText;
        this.sender = sender;
        this.isSent = isSent;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setReadenByAnother(boolean readenByAnother) {
        isReadenByAnother = readenByAnother;
    }

    public boolean isReadenByAnother() {
        return isReadenByAnother;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setSender(DocumentReference sender) {
        this.sender = sender;
    }

    public DocumentReference getSender() {
        return sender;
    }

    public void setSent(byte sent) {
        isSent = sent;
    }

    public byte isSent() {
        return isSent;
    }
}
