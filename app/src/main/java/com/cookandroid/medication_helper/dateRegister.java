package com.cookandroid.medication_helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class dateRegister extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicinedate);
        setTitle("Medication Helper");

        AppCompatButton btnNextStep = (AppCompatButton) findViewById(R.id.btnNextStep);
        AppCompatButton btnBack_medicInedate = (AppCompatButton) findViewById(R.id.btnBack_medicInedate);

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
