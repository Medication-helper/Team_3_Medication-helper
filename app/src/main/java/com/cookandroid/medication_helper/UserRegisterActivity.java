/******************
 * 회원가입 담당 코드
 * 제작자 : 안현종
 ******************/

package com.cookandroid.medication_helper;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserRegisterActivity extends AppCompatActivity{
    UserDBHelper myHelper;
    SQLiteDatabase sqlDB;
    EditText E_ID, E_Pass, E_Name;
    RadioGroup RG;
    RadioButton RB_Man, RB_Woman;
    Button btnBirthChoose, btnComplete;
    int dateCheckCounter = 0;
    int dupCheckCounter = 0;

    @Override // 하단의 뒤로가기(◀) 버튼을 눌렀을 시 동작
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(UserRegisterActivity.this, MainActivity.class); // 메인화면으로 돌아가는 기능
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 회원가입 페이지가 백그라운드에서 돌아가지 않도록 완전종료
        startActivity(BackToMain); // 실행
        finish(); // Progress 완전 종료
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
        btnBirthChoose = (Button) findViewById(R.id.BtnBirthChoose);
        btnComplete = (Button) findViewById(R.id.BtnComplete);

        myHelper = new UserDBHelper(this);

        /* 오늘 날짜 계산 */
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        int todayYear = Integer.parseInt(yearFormat.format(todayDate));
        int todayMonth = Integer.parseInt(monthFormat.format(todayDate));
        int todayDay = Integer.parseInt(dayFormat.format(todayDate));

        /* 생년월일 선택용 변수 */
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        /* 생년월일 선택 */
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) { // DatePicker에서 월은 0부터 시작되기 때문에 1을 더해줘야 함
                if (year > todayYear) {
                    dateCheckCounter = 1;
                }
                else if (year == todayYear) {
                    if ((month+1) > todayMonth) {
                        dateCheckCounter = 1;
                    }
                    else if ((month+1) == todayMonth) {
                        if (dayOfMonth > todayDay) {
                            dateCheckCounter = 1;
                        }
                    }
                }

                if (dateCheckCounter == 1) { // 생년월일이 현재 일자보다 미래일 경우
                    Toast.makeText(getApplicationContext(), "미래시입니다.", Toast.LENGTH_SHORT).show();
                    dateCheckCounter = 0; // 카운터 초기화
                }
                else // 정상적으로 생년월일을 입력받은 경우
                    btnBirthChoose.setText(dayOfMonth +"/" + (month + 1) + "/" + year); // 생년월일 선택 버튼에 생년월일을 표시함
            }
        }, mYear, mMonth, mDay);

        btnBirthChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnBirthChoose.isClickable()) {
                    datePickerDialog.show(); // 상단의 생년월일 선택 함수 작동
                }
            }
        });

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent completeIntent = new Intent(UserRegisterActivity.this, MainActivity.class); // 메인화면으로 돌아가기 위한 기능
                sqlDB = myHelper.getWritableDatabase(); // 사용자 정보 DB를 쓰기 가능으로 읽어옴
                Cursor cursor = sqlDB.rawQuery("SELECT * FROM userTBL", null); // DB를 읽어옴
                    while (cursor.moveToNext()) {
                        if (cursor.getString(0).equals(E_ID.getText().toString())) {
                            dupCheckCounter = 1; // 만약 회원가입할 ID가 DB상에 이미 존재할 경우
                        }
                    }
                if (E_ID.getText().toString().length() == 0) { // ID란이 공백인 경우
                    Toast.makeText(getApplicationContext(), "ID가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                else if (dupCheckCounter == 1) { // ID가 중복될 경우
                    Toast.makeText(getApplicationContext(), "이미 등록된 ID입니다.", Toast.LENGTH_SHORT).show();
                    dupCheckCounter = 0;
                }
                else if (E_Pass.getText().toString().length() == 0) { // 비밀번호란이 공백인 경우
                    Toast.makeText(getApplicationContext(), "비밀번호가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                else if (E_Name.getText().toString().length() == 0) { // 이름란이 공백인 경우
                    Toast.makeText(getApplicationContext(), "사용자 이름이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                else if (btnBirthChoose.getText().toString().equals("선택")) { // 생년월일이 선택되지 않았을 경우
                    Toast.makeText(getApplicationContext(), "생년월일이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                else { // 성별을 제외한 모든 정보가 입력되었을 경우
                    switch (RG.getCheckedRadioButtonId()) {
                        case R.id.RB_man: // 성별을 남자로 선택했을 경우
                            /* 입력받은 정보에 더해 성별을 MAN으로 지정해 DB에 저장 */
                            sqlDB.execSQL("INSERT INTO userTBL VALUES ( '"
                                    + E_ID.getText().toString() + "', '"
                                    + E_Pass.getText().toString() + "', '"
                                    + E_Name.getText().toString() + "', '"
                                    + btnBirthChoose.getText().toString() + "', 'MAN');");
                            Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            sqlDB.close(); // DBHelper를 닫음
                            finish(); // 회원가입 프로그래스 종료
                            startActivity(completeIntent); // 메인화면으로 돌아감
                            break; // switch문 탈출
                        case R.id.RB_woman: // 성별을 여자로 선택했을 경우
                            /* 입력받은 정보에 더해 성별을 WOMAN으로 지정해 DB에 저장 */
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
                        default: // 성별이 선택되지 않았을 경우
                            Toast.makeText(getApplicationContext(), "성별이 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
    }
}
