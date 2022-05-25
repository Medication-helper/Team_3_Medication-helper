package com.cookandroid.medication_helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class userDBHelper extends SQLiteOpenHelper {
    public userDBHelper(Context context) {
        super(context, "userTBL", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String qry = "CREATE TABLE userTBL ( uID char(20) PRIMARY KEY, uPassword char(20), uName CHAR(10), uBirth CHAR(20), uGender CHAR(5))";
        db.execSQL(qry);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        String qry = "DROP TABLE IF EXISTS userTBL";
        db.execSQL(qry);
        onCreate(db);
    }
}