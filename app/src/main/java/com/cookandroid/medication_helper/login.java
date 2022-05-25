package com.cookandroid.medication_helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicationstarttime);
        setTitle("Medication Helper");

        Button btnlogin = findViewById(R.id.btnlogin);
        Button btnsignin = findViewById(R.id.btnsignin);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(login.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userReIntent = new Intent(login.this, userRegister.class);
                startActivity(userReIntent);
                finish();
            }
        });



    }


}
