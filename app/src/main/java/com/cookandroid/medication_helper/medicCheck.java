package com.cookandroid.medication_helper;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

@SuppressWarnings("deprecation")
public class medicCheck extends TabActivity {
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(medicCheck.this, MainActivity.class);
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackToMain);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forbiddenlist);
        setTitle("Medication Helper");

        TabHost tabHost=getTabHost();

        TabHost.TabSpec tabSpecComb=tabHost.newTabSpec("COMB").setIndicator("병용 금기");
        tabSpecComb.setContent(R.id.TABCOMB);
        tabHost.addTab(tabSpecComb);

        TabHost.TabSpec tabSpecPreg=tabHost.newTabSpec("PREG").setIndicator("임부 금기");
        tabSpecPreg.setContent(R.id.TABPREG);
        tabHost.addTab(tabSpecPreg);

        TabHost.TabSpec tabSpecAge=tabHost.newTabSpec("AGE").setIndicator("연령 금기");
        tabSpecAge.setContent(R.id.TABAGE);
        tabHost.addTab(tabSpecAge);

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
