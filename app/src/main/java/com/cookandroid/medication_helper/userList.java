package com.cookandroid.medication_helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class userList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlist);
        setTitle("Medication Helper");

        Button BtnChoose = (Button) findViewById(R.id.btnChoose);
        Button BtnDelete = (Button) findViewById(R.id.btnDelete);
        Button BtnBack_userList = (Button) findViewById(R.id.btnBack_userList);

        BtnBack_userList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BackToMain = new Intent(userList.this, MainActivity.class);
                startActivity(BackToMain);
                finish();
            }
        });

    }
}
