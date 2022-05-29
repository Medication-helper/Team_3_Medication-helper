package com.cookandroid.medication_helper;

import android.app.Application;

public class UserData extends Application {
    private String userID;
    private String userPassWord;
    private String userNickName;
    private String userBirth;
    private String userGender;

    @Override
    public void onCreate() {
        super.onCreate();
        userID = "";
        userPassWord = "";
        userNickName = "";
        userBirth = "";
        userGender = "";
    }

    public void Init() {
        userID = "";
        userPassWord = "";
        userNickName = "";
        userBirth = "";
        userGender = "";
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPassWord() {
        return userPassWord;
    }

    public void setUserPassWord(String userPassWord) {
        this.userPassWord = userPassWord;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserBirth() {
        return userBirth;
    }

    public void setUserBirth(String userBirth) {
        this.userBirth = userBirth;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }
}
