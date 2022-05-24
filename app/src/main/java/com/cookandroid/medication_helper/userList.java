package com.cookandroid.medication_helper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class userList extends AppCompatActivity {

    ListView userView;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(userList.this, MainActivity.class);
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackToMain);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlist);
        setTitle("Medication Helper");

        Button BtnChoose = (Button) findViewById(R.id.btnChoose);
        Button BtnDelete = (Button) findViewById(R.id.btnDelete);
        Button BtnBack_userList = (Button) findViewById(R.id.btnBack_userList);

        userView = (ListView)findViewById(R.id.userView);

        BtnBack_userList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BackToMain = new Intent(userList.this, MainActivity.class);
                startActivity(BackToMain);
                finish();
            }
        });



        displayList();
    }

    void displayList(){

        myDBHelper helper = new myDBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM userTBL",null);

        ListViewAdapter adapter = new ListViewAdapter();

        while(cursor.moveToNext()){
            adapter.addItemToList(cursor.getString(0),cursor.getString(1),cursor.getString(2));
        }

        userView.setAdapter(adapter);

    }


}
