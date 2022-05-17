package com.cookandroid.medication_helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class userRegister extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(userRegister.this, MainActivity.class);
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackToMain);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userregister);
        setTitle("Medication Helper");

        Button btnComplete = (Button) findViewById(R.id.BtnComplete);
        Button btnBack_userRegister = (Button) findViewById(R.id.BtnBack_userRegister);

        btnBack_userRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BackToMain = new Intent(userRegister.this, MainActivity.class);
                startActivity(BackToMain);
                finish();
            }
        });
    }
}
