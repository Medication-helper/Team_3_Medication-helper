package com.cookandroid.medication_helper;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.regex.Pattern;

public class UserRegisterActivity extends AppCompatActivity{
    UserDBHelper myHelper;
    SQLiteDatabase sqlDB;
    EditText E_ID, E_Pass, E_Name;
    RadioGroup RG;
    RadioButton RB_Man, RB_Woman;
    Button btnDupCheck, btnBirthChoose, btnComplete, btnInit;
    int dupCheckCounter = 0;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(UserRegisterActivity.this, LoginActivity.class);
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackToMain);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userregister);
        setTitle("Medication Helper");

        E_ID = (EditText) findViewById(R.id.E_ID);
        E_Pass = (EditText) findViewById(R.id.E_Pass);
        E_Name = (EditText) findViewById(R.id.E_name);
        RG = (RadioGroup) findViewById(R.id.RG);
        RB_Man = (RadioButton) findViewById(R.id.RB_man);
        RB_Woman = (RadioButton) findViewById(R.id.RB_woman);
        btnDupCheck = (Button) findViewById(R.id.BtnDupCheck);
        btnBirthChoose = (Button) findViewById(R.id.BtnBirthChoose);
        btnComplete = (Button) findViewById(R.id.BtnComplete);

        myHelper = new UserDBHelper(this);

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

        btnDupCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDB = myHelper.getReadableDatabase();
                Cursor cursor = sqlDB.rawQuery("SELECT * FROM userTBL", null);
                if (E_ID.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "ID가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    while (cursor.moveToNext()) {
                        if (cursor.getString(0).equals(E_ID.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "이미 등록된 ID입니다.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        Toast.makeText(getApplicationContext(), "사용 가능한 ID입니다.", Toast.LENGTH_SHORT).show();
                        dupCheckCounter = 1;
                        break;
                    }
                }
            }
        });

        btnBirthChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnBirthChoose.isClickable()) {
                    datePickerDialog.show();
                }
            }
        });

        myHelper = new UserDBHelper(this);

//        btnInit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sqlDB = myHelper.getWritableDatabase();
//                myHelper.onUpgrade(sqlDB, 1, 2);
//                sqlDB.close();
//            }
//        });

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent completeIntent = new Intent(UserRegisterActivity.this, LoginActivity.class);
                sqlDB = myHelper.getWritableDatabase();
                if (E_ID.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "ID가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                else if (dupCheckCounter == 0) {
                    Toast.makeText(getApplicationContext(), "ID 중복확인을 해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (E_Pass.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                else if (E_Name.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "사용자 이름이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                else if (btnBirthChoose.getText().toString().equals("선택")) {
                    Toast.makeText(getApplicationContext(), "생년월일이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    switch (RG.getCheckedRadioButtonId()) {
                        case R.id.RB_man:
                            sqlDB.execSQL("INSERT INTO userTBL VALUES ( '"
                                    + E_ID.getText().toString() + "', '"
                                    + E_Pass.getText().toString() + "', '"
                                    + E_Name.getText().toString() + "', '"
                                    + btnBirthChoose.getText().toString() + "', 'MAN');");
                            Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            sqlDB.close();
                            finish();
                            startActivity(completeIntent);
                            break;
                        case R.id.RB_woman:
                            sqlDB.execSQL("INSERT INTO userTBL VALUES ( '"
                                    + E_ID.getText().toString() + "', '"
                                    + E_Pass.getText().toString() + "', '"
                                    + E_Name.getText().toString() + "' , '"
                                    + btnBirthChoose.getText().toString() + "', 'WOMAN');");
                            Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            sqlDB.close();
                            finish();
                            startActivity(completeIntent);
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "성별이 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                //이거 일단은 넘어가게 제가 intent로 설정은 해놨지만 나중에 손봐야 될 거 같아요 그냥 버튼 누르면 넘어갑니다 지금은
            }
        });
    }
}
