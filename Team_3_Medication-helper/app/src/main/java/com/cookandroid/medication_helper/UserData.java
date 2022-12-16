/***************************
 * 사용자 정보를 저장하는 클래스
 * 작성 팀 : Hello World!
 * 제작자 : 안현종
 **************************/

package com.cookandroid.medication_helper;

import android.app.Application;

public class UserData extends Application {
    private String userID;
    private String userPassWord;
    private String userNickName;
    private String userBirth;
    private String userGender;

    @Override
    public void onCreate() { // UserData 클래스 초기화
        super.onCreate();
        userID = "";
        userPassWord = "";
        userNickName = "";
        userBirth = "";
        userGender = "";
    }

    public void Init() { // UserData 초기화
        userID = "";
        userPassWord = "";
        userNickName = "";
        userBirth = "";
        userGender = "";
    }

    public String getUserID() { // ID 반환
        return userID;
    }

    public void setUserID(String userID) { // ID 갱신
        this.userID = userID;
    }

    public String getUserPassWord() { // 비밀번호 반환
        return userPassWord;
    }

    public void setUserPassWord(String userPassWord) { // 비밀번호 갱신
        this.userPassWord = userPassWord;
    }

    public String getUserNickName() { // 이름 반환
        return userNickName;
    }

    public void setUserNickName(String userNickName) { // 이름 갱신
        this.userNickName = userNickName;
    }

    public String getUserBirth() { // 생년월일 반환
        return userBirth;
    }

    public void setUserBirth(String userBirth) { // 생년월일 갱신
        this.userBirth = userBirth;
    }

    public String getUserGender() { // 성별 반환
        return userGender;
    }

    public void setUserGender(String userGender) { // 성별 갱신
        this.userGender = userGender;
    }
}
