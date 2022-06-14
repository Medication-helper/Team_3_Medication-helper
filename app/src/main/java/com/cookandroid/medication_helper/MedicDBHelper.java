/*********************************************************
 * SQLiteOpenHelper 클래스를 상속받는 의약품정보 DB 클래스 정의
 * 제작자 : 안현종
 ********************************************************/

package com.cookandroid.medication_helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MedicDBHelper extends SQLiteOpenHelper {
    // 의약품정보 DB의 테이블 이름 지정
    public MedicDBHelper(Context context) {
        super(context, "medicTBL", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String qry = "CREATE TABLE medicTBL (serialNo INTEGER PRIMARY KEY, uID char(20), mName char(50))";
        db.execSQL(qry);
        // userTBL DB 생성
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String qry = "DROP TABLE IF EXISTS medicTBL";
        db.execSQL(qry);
        onCreate(db);
        // userTBL DB 초기화(삭제 후 재생성)
    }
}
