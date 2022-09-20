/******************
 * 회원가입 담당 코드
 * 작성 팀 : Hello World!
 * 제작자 : 안현종
 ******************/

package com.cookandroid.medication_helper;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserRegisterActivity extends AppCompatActivity{
    EditText E_ID, E_Pass, E_PassCheck, E_Name;
    RadioGroup RG;
    RadioButton RB_Man, RB_Woman;
    Button btnIDCheck, btnBirthChoose, btnComplete;
    TextView tvBirth;
    int dateCheckCounter = 0;
    int genderCheckCounter = 0;

    private boolean validate = false;

    @Override // 하단의 뒤로가기(◀) 버튼을 눌렀을 시 동작
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(UserRegisterActivity.this, com.cookandroid.medication_helper.MainActivity.class); // 메인화면으로 돌아가는 기능
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 회원가입 페이지가 백그라운드에서 돌아가지 않도록 완전종료
        startActivity(BackToMain); // 실행
        finish(); // Progress 완전 종료
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_userregister);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.titlebar_custom); // 커스텀 사용할 파일 위치

        E_ID = (EditText) findViewById(R.id.E_ID);
        E_Pass = (EditText) findViewById(R.id.E_Pass);
        E_PassCheck = (EditText) findViewById(R.id.E_PassCheck);
        E_Name = (EditText) findViewById(R.id.E_name);
        RG = (RadioGroup) findViewById(R.id.RG);
        RB_Man = (RadioButton) findViewById(R.id.RB_man);
        RB_Woman = (RadioButton) findViewById(R.id.RB_woman);
        btnIDCheck = (Button) findViewById(R.id.idCheck);
        btnBirthChoose = (Button) findViewById(R.id.BtnBirthChoose);
        btnComplete = (Button) findViewById(R.id.BtnComplete);
        tvBirth = (TextView) findViewById(R.id.TvBirth);

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
                    tvBirth.setText(dayOfMonth +"/" + (month + 1) + "/" + year); // 생년월일 선택 버튼에 생년월일을 표시함
            }
        }, mYear, mMonth, mDay);

        btnIDCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ID = E_ID.getText().toString();
                if (validate)
                    return;
                if (ID.equals("")) {
                    Toast.makeText(getApplicationContext(), "ID가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                Toast.makeText(getApplicationContext(), "사용할 수 있는 ID입니다.", Toast.LENGTH_SHORT).show();
                                E_ID.setEnabled(false);
                                validate = true;
                                btnIDCheck.setText("확인");
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "사용할 수 없는 ID입니다.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                ValidateRequest validateRequest = new ValidateRequest(ID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(UserRegisterActivity.this);
                queue.add(validateRequest);
            }
        });

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
                String ID = E_ID.getText().toString();
                String password = E_Pass.getText().toString();
                String passwordCheck = E_PassCheck.getText().toString();
                String name = E_Name.getText().toString();
                String birth = tvBirth.getText().toString();

                switch (RG.getCheckedRadioButtonId()) {
                    case R.id.RB_man:
                        genderCheckCounter = 1;
                        break;
                    case R.id.RB_woman:
                        genderCheckCounter = 2;
                        break;
                    default:
                        genderCheckCounter = 0;
                        break;
                }

                if (ID.length() == 0) { // ID란이 공백인 경우
                    Toast.makeText(getApplicationContext(), "ID가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                } else if (password.length() == 0) { // 비밀번호란이 공백인 경우
                    Toast.makeText(getApplicationContext(), "비밀번호가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                } else if (passwordCheck.length() == 0) { // 비밀번호 확인란이 공백인 경우
                    Toast.makeText(getApplicationContext(), "비밀번호를 다시 한 번 입력해 주십시오.", Toast.LENGTH_SHORT).show();
                } else if (password.equals(passwordCheck) == false) { // 비밀번호랑 확인이 다를 경우
                    Toast.makeText(getApplicationContext(), "비밀번호 확인란에 입력된 내용이 비밀번호와 다릅니다.", Toast.LENGTH_SHORT).show();
                } else if (name.length() == 0) { // 이름란이 공백인 경우
                    Toast.makeText(getApplicationContext(), "사용자 이름이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                System.out.println(ID);
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    Intent finish = new Intent(UserRegisterActivity.this, MainActivity.class);
                                    startActivity(finish);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    switch (genderCheckCounter) {
                        case 0:
                            UserRegisterRequest RegisterRequest = new UserRegisterRequest(ID, password, name, birth, "", responseListener);
                            RequestQueue Queue = Volley.newRequestQueue(UserRegisterActivity.this);
                            Queue.add(RegisterRequest);
                            break;
                        case 1:
                            UserRegisterRequest manRegisterRequest = new UserRegisterRequest(ID, password, name, birth, "man", responseListener);
                            RequestQueue manQueue = Volley.newRequestQueue(UserRegisterActivity.this);
                            manQueue.add(manRegisterRequest);
                            break;
                        case 2:
                            UserRegisterRequest womanRegisterRequest = new UserRegisterRequest(ID, password, name, birth, "woman", responseListener);
                            RequestQueue womanQueue = Volley.newRequestQueue(UserRegisterActivity.this);
                            womanQueue.add(womanRegisterRequest);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }
}
