package com.cookandroid.ocrtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class AuthActivity extends BaseActivity {
    Button cssBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        SharedPreferences sharedPref1 = getSharedPreferences("PREF", Context.MODE_PRIVATE);
        String ocrApiGwUrl = sharedPref1.getString("ocr_api_gw_url", "");
        String ocrSecretKey = sharedPref1.getString("ocr_secret_key", "");

        EditText editTextOCRApiGwUrl = (EditText) findViewById(R.id.text_input_ocr_api_gw_url);
        editTextOCRApiGwUrl.setText(ocrApiGwUrl);

        EditText editTextOcrSecretKey = (EditText) findViewById(R.id.text_input_ocr_secret_key);
        editTextOcrSecretKey.setText(ocrSecretKey);

        TextView textView1 = (TextView) findViewById(R.id.textView_rlt);
        textView1.setText("");

        cssBtn = (Button) findViewById(R.id.btn_auth);
        cssBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText editTextOCRApiGwUrl = (EditText) findViewById(R.id.text_input_ocr_api_gw_url);
                    //String clientOcrApiUrl = editTextOCRApiGwUrl.getText().toString();
                    String clientOcrApiUrl = "https://2d5ip0xmqs.apigw.ntruss.com/custom/v1/19465/3fc8d9998b12503d46db6f593e2fe2bbf745bb10eb23d04306a76411c117f301/general";

                    EditText editTextOcrSecretKey = (EditText) findViewById(R.id.text_input_ocr_secret_key);
                    //String clientOcrSecreteKey = editTextOcrSecretKey.getText().toString();
                    String clientOcrSecreteKey = "c3BJQ0lyRUxMaHBXa2dVWXZxeG1ESVBiYWFsZXN4QmI=";

                    SharedPreferences sharedPref = getSharedPreferences("PREF", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putString("ocr_api_gw_url", clientOcrApiUrl);
                    editor.putString("ocr_secret_key", clientOcrSecreteKey);

                    editor.commit();

                    TextView textView1 = (TextView) findViewById(R.id.textView_rlt);
                    textView1.setText("저장되었습니다.");

                } catch (Exception e) {
                    TextView textView1 = (TextView) findViewById(R.id.textView_rlt);
                    textView1.setText("저장 실패");
                }
            }
        });
    }
}
