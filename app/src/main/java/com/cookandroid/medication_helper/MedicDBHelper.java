package com.cookandroid.medication_helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MedicDBHelper extends SQLiteOpenHelper {
    public MedicDBHelper(Context context) {
        super(context, "medicTBL", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String qry = "CREATE TABLE medicTBL (serialNo INTEGER PRIMARY KEY, uID char(20), mName char(50), uForbidden char(50))";
        db.execSQL(qry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String qry = "DROP TABLE IF EXISTS medicTBL";
        db.execSQL(qry);
        onCreate(db);
    }
}
