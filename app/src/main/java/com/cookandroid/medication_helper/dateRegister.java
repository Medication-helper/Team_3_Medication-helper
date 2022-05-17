package com.cookandroid.medication_helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class dateRegister extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(dateRegister.this, MainActivity.class);
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackToMain);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicinedate);
        setTitle("Medication Helper");

        AppCompatButton btnNextStep = (AppCompatButton) findViewById(R.id.btnNextStep);
        AppCompatButton btnBack_medicInedate = (AppCompatButton) findViewById(R.id.btnBack_medicInedate);
        
        //날짜 설정
        btnNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent NextStep=new Intent(dateRegister.this,timeRegister.class);
                startActivity(NextStep);
            }
        });

        //메인화면으로
        btnBack_medicInedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent BackToMain = new Intent(dateRegister.this, MainActivity.class);
                startActivity(BackToMain);
                finish();
            }
        });
    }
}
