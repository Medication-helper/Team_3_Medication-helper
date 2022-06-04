package com.cookandroid.medication_helper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MedicineListActivity extends AppCompatActivity {
    Button delBtn;
    ListView medicationListView;
    Button btnBack;

    UserData userData;
    MedicDBHelper myHelper;
    SQLiteDatabase sqlDB;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent Back = new Intent(MedicineListActivity.this, MedicCheckActivity.class);
        Back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(Back);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicinelist);
        setTitle("Medication Helper");

        userData = (UserData) getApplicationContext();
        myHelper = new MedicDBHelper(this);
        sqlDB = myHelper.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("SELECT * FROM medicTBL WHERE uID = '" + userData.getUserID() + "';", null);

        medicationListView=(ListView)findViewById(R.id.medicationlist);
        delBtn=(Button)findViewById(R.id.btnalldelete);
        //delselectBtn=(Button)findViewById(R.id.btnselectdel);
        btnBack=(Button)findViewById(R.id.back);

        String[] medicineArray = new String[cursor.getCount()];//DB에서 받아온 처방약 목록을 저장하는 String 배열
        int serialNo = 0;

        while (cursor.moveToNext()) {
            medicineArray[serialNo] = cursor.getString(2);
            serialNo++;
        }

        ArrayList<String> mediclist=new ArrayList<>(Arrays.asList(medicineArray));

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,mediclist);

        medicationListView.setAdapter(adapter);

        medicationListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                medicationListView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clear();
                adapter.notifyDataSetChanged();
                medicationListView.setAdapter(adapter);

                sqlDB = myHelper.getWritableDatabase();
                String sql = "DELETE FROM medicTBL WHERE uID = '" + userData.getUserID() + "';";
                sqlDB.execSQL(sql);
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicineListActivity.this, MedicCheckActivity.class);
                startActivity(intent);
                finish();
            }
        });

        cursor.close();
        sqlDB.close();
    }
}
