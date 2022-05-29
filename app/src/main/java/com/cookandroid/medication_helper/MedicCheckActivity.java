package com.cookandroid.medication_helper;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

@SuppressWarnings("deprecation")
public class MedicCheckActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(MedicCheckActivity.this, MainActivity.class);
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackToMain);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediccheck);
        setTitle("Medication Helper");

        Button btnMedilist = findViewById(R.id.btnMedilist);
        Button btnFBcom = findViewById(R.id.btnFBcom);
        Button btnFBpreg = findViewById(R.id.btnFBpreg);
        Button btnFBage = findViewById(R.id.btnFBage);

        btnMedilist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicCheckActivity.this, MedicineListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnFBcom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicCheckActivity.this, ComForbiddenListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnFBpreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicCheckActivity.this, PregnantForbiddenListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnFBage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicCheckActivity.this, AgeForbiddenListActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}