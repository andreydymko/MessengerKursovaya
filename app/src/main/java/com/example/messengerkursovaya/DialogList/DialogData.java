package com.example.messengerkursovaya.DialogList;

import android.graphics.Bitmap;

import java.util.Date;

public class DialogData {

    private String id;
    private String title;
    private String lastMessage;
    private Date lastMessageDate;
    private Bitmap dialogImage;
    private boolean isReadenMyself;
    private boolean isReadenByAnother;
    private boolean isSent;

    public DialogData() {}

    public DialogData(String id,
                      String title,
                      String lastMessage,
                      Date lastMessageDate,
                      Bitmap dialogImage,
                      boolean isReadenMyself,
                      boolean isReadenByAnother,
                      boolean isSent) {
        this.id = id;
        this.title = title;
        this.lastMessage = lastMessage;
        this.lastMessageDate = lastMessageDate;
        this.dialogImage = dialogImage;
        this.isReadenMyself = isReadenMyself;
        this.isReadenByAnother = isReadenByAnother;
        this.isSent = isSent;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setDialogImage(Bitmap dialogImage) {
        this.dialogImage = dialogImage;
    }

    public Bitmap getDialogImage() {
        return dialogImage;
    }

    public void setReadenMyself(boolean isReadenMyself) {
        this.isReadenMyself = isReadenMyself;
    }

    public boolean isReadenMyself() {
        return this.isReadenMyself;
    }

    public void setReadenByAnother(boolean readenByAnother) {
        this.isReadenByAnother = readenByAnother;
    }

    public boolean isReadenByAnother() {
        return isReadenByAnother;
    }

    public void setSent(boolean isSent) {
        this.isSent = isSent;
    }

    public boolean isSent() {
        return this.isSent;
    }
}
