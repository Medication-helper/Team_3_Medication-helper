/*******************************************************
 * SQLiteOpenHelper 클래스를 상속받는 유저정보 DB 클래스 정의
 * 제작자 : 안현종
 ******************************************************/

package com.cookandroid.medication_helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDBHelper extends SQLiteOpenHelper {
    public UserDBHelper(Context context) {
        super(context, "userTBL", null, 1); // 유저정보 DB의 테이블 이름 지정
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String qry = "CREATE TABLE userTBL ( uID char(20) PRIMARY KEY, uPassword char(20), uName CHAR(10), uBirth CHAR(20), uGender CHAR(5))";
        db.execSQL(qry);
        // userTBL DB 생성
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String qry = "DROP TABLE IF EXISTS userTBL";
        db.execSQL(qry);
        onCreate(db);
        // userTBL DB 초기화(삭제 후 재생성)
    }
}
