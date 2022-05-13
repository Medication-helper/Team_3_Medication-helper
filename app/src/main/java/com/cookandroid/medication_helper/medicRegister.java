package com.cookandroid.medication_helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class medicRegister extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicationregister);
        setTitle("Medication Helper");


        Button registerButton=findViewById(R.id.medicregister);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regMedic=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(regMedic);
            }
        });
    }
}
