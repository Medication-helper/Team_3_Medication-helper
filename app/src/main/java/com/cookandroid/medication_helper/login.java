package com.cookandroid.medication_helper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {
    userDBHelper myHelper;
    SQLiteDatabase sqlDB;
    UserData userData;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(login.this, MainActivity.class);
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackToMain);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setTitle("Medication Helper");

        userData = (UserData)getApplicationContext();
        EditText edtID = findViewById(R.id.editID);
        EditText edtPW = findViewById(R.id.editPW);

        Button btnlogin = findViewById(R.id.btnlogin);
        Button btnsignin = findViewById(R.id.btnsignin);

        myHelper = new userDBHelper(this);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDB = myHelper.getReadableDatabase();
                Cursor cursor;
                cursor = sqlDB.rawQuery("SELECT * FROM userTBL;", null);
                Boolean checkID = false;
                Boolean checkPW = false;
                int position = 0;

                while (cursor.moveToNext()) {
                    if ((cursor.getString(0)).equals(edtID.getText().toString())) {
                        checkID = true;
                        if ((cursor.getString(1)).equals(edtPW.getText().toString())){
                            checkPW = true;
                            break;
                        }
                        break;
                    }
                    position++;
                }

                if (checkID == false) {
                    Toast.makeText(getApplicationContext(), "등록된 ID가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else if (checkPW == false) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    cursor.moveToPosition(position);
                    userData.setUserID(cursor.getString(0));
                    userData.setUserPassWord(cursor.getString(1));
                    userData.setUserNickName(cursor.getString(2));
                    userData.setUserBirth(cursor.getString(3));
                    userData.setUserGender(cursor.getString(4));
                    Intent mainIntent = new Intent(login.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
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
