package com.cookandroid.medication_helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class medicCheck extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forbiddenlist);
        setTitle("Medication Helper");

        Button combbacktoMain=findViewById(R.id.combtabclose);
        Button pregbacktoMain=findViewById(R.id.pregtabclose);
        Button agebacktoMain=findViewById(R.id.agetabclose);

        combbacktoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent combtoMain=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(combtoMain);
            }
        });

        pregbacktoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pregtoMain=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(pregtoMain);
            }
        });

        agebacktoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent agetoMain=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(agetoMain);
            }
        });

    }
}
