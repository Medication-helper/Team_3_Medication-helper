package com.cookandroid.medication_helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class medicRegister extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(medicRegister.this, MainActivity.class);
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackToMain);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicationregister);
        setTitle("Medication Helper");

        Button registerButton = (Button) findViewById(R.id.medicregister);
        Button btnBack_medicRegister = (Button) findViewById(R.id.btnBack_medicRegister);

        btnBack_medicRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent BackToMain = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(BackToMain);
            }
        });
    }
}
