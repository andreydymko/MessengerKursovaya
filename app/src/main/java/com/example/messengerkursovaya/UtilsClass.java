package com.example.messengerkursovaya;

import android.text.format.DateUtils;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class UtilsClass {
    private UtilsClass() {}

    /*
    * returns String user email, using FirebaseAuth
    * returns null if user email is not stated
    */
    public static String getCurrUserEmail() {
        String userEmail;
        try {
            userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        } catch (Exception e) {
            userEmail = null;
        }
        return userEmail;
    }

    /*
    * returns String current date
    * in format HH:mm if date is today
    * in format MMM dd otherwise
    */
    public static String getStrDate(Date date) {
        //TODO get date
        SimpleDateFormat formatter;
        if (DateUtils.isToday(date.getTime())) {
            formatter = new SimpleDateFormat("HH:mm");
            return formatter.format(date);
        }
        formatter = new SimpleDateFormat("MMM dd");
        return formatter.format(date);
    }
}
