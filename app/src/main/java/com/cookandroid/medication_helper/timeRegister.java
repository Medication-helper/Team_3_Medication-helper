package com.cookandroid.medication_helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class timeRegister extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(timeRegister.this, MainActivity.class);
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackToMain);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicationstarttime);
        setTitle("Medication Helper");

        Button btnBack_medicStartTime = (Button) findViewById(R.id.btnBack_medicStartTime);

        btnBack_medicStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent BackToMain = new Intent(timeRegister.this, MainActivity.class);
                startActivity(BackToMain);
                finish();
            }
        });
    }
}
