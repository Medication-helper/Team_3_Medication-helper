package com.cookandroid.medication_helper;

import android.app.DatePickerDialog;
import android.text.Spanned;
import android.widget.DatePicker;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.InputFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.regex.Pattern;

public class userRegister extends AppCompatActivity {
    userDBHelper myHelper;
    EditText E_ID, E_Pass, E_Name;
    RadioGroup RG;
    RadioButton RB_Man, RB_Woman;
    Button btnBirthChoose,  btnComplete, btnInit;
    SQLiteDatabase sqlDB;

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

        E_ID = (EditText) findViewById(R.id.E_ID);
        E_Pass = (EditText) findViewById(R.id.E_Pass);
        E_Name = (EditText) findViewById(R.id.E_name);
        RG = (RadioGroup) findViewById(R.id.RG);
        RB_Man = (RadioButton) findViewById(R.id.RB_man);
        RB_Woman = (RadioButton) findViewById(R.id.RB_woman);
        btnBirthChoose = (Button) findViewById(R.id.BtnBirthChoose);
        btnComplete = (Button) findViewById(R.id.BtnComplete);
        
        InputFilter filterAlphaNumber = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
                if(!ps.matcher(source).matches()) {
                    Toast.makeText(getApplicationContext(), "문자와 숫자만 입력 가능합니다.", Toast.LENGTH_SHORT).show();
                    return "";
                }
                return null;
            }
        };

        E_ID.setFilters(new InputFilter[]{filterAlphaNumber});
        E_Pass.setFilters(new InputFilter[]{filterAlphaNumber});

        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                btnBirthChoose.setText(dayOfMonth+"/" + (month+1) + "/" + year);
            }
        }, mYear, mMonth, mDay);

        btnBirthChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnBirthChoose.isClickable()) {
                    datePickerDialog.show();
                }
            }
        });

        myHelper = new userDBHelper(this);

        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDB = myHelper.getWritableDatabase();
                myHelper.onUpgrade(sqlDB, 1, 2);
                sqlDB.close();
            }
        });

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDB = myHelper.getWritableDatabase();
                switch (RG.getCheckedRadioButtonId()) {
                    case R.id.RB_man:
                        sqlDB.execSQL("INSERT INTO userTBL VALUES ( '"
                                + E_ID.getText().toString() + "', '"
                                + E_Pass.getText().toString() + "', '"
                                + E_Name.getText().toString() + "', '"
                                + btnBirthChoose.getText().toString() + "', 'MAN');");
                        break;
                    case R.id.RB_woman:
                        sqlDB.execSQL("INSERT INTO userTBL VALUES ( '"
                                + E_ID.getText().toString() + "', '"
                                + E_Pass.getText().toString() + "', '"
                                + E_Name.getText().toString() + "' , '"
                                + btnBirthChoose.getText().toString() + "', 'WOMAN');");
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "성별이 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}
