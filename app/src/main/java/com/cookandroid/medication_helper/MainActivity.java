
package com.cookandroid.medication_helper;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends FragmentActivity {
    com.cookandroid.medication_helper.UserData userData;
    String loginID, loginPW;

    //뒤로가기 누르면 앱종료시키는 함수
    @Override
    public void onBackPressed() {
        //다이어로그를 화면에 나타냄
        AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        exitDialogBuilder
                .setTitle("프로그램 종료")
                .setMessage("종료하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네",
                        //네를 누르면 앱 종료
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int pid = android.os.Process.myPid();
                                android.os.Process.killProcess(pid);
                                finish();
                            }
                        })
                //아니오 누르면 다이어로그를 종료
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
        AlertDialog exitDialog = exitDialogBuilder.create();
        exitDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        userData = (com.cookandroid.medication_helper.UserData) getApplicationContext();
        EditText edtID = findViewById(R.id.editID);
        EditText edtPW = findViewById(R.id.editPW);

        CheckBox checkBox = findViewById(R.id.autoLogin);
        Button btnlogin = findViewById(R.id.btnlogin);
        TextView btnsignin = findViewById(R.id.btnsignin);

        SharedPreferences auto = getSharedPreferences("autologin", Activity.MODE_PRIVATE);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = edtID.getText().toString();
                String userPassword = edtPW.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                Toast.makeText(getApplicationContext(), "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();

                                String loginID = jsonResponse.getString("userID");
                                String loginPassword = jsonResponse.getString("userPassword");
                                String loginName = jsonResponse.getString("userName");
                                String loginBirth = jsonResponse.getString("userBirth");
                                String loginGender = jsonResponse.getString("userGender");

                                userData.setUserID(loginID);
                                userData.setUserPassWord(loginPassword);
                                userData.setUserNickName(loginName);
                                userData.setUserBirth(loginBirth);
                                userData.setUserGender(loginGender);

                                Intent intent = new Intent(MainActivity.this, com.cookandroid.medication_helper.MainPageActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(loginRequest);
            }
        });

        // 자동 로그인 구현 부분분
       if (checkBox.isChecked()) {
          // autoLoginEdit.commit();
        }

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userReIntent = new Intent(MainActivity.this, com.cookandroid.medication_helper.UserRegisterActivity.class);
                startActivity(userReIntent);
                finish();
            }
        });
    }
}