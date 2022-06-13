package com.cookandroid.medication_helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WebActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent Back = new Intent(WebActivity.this, MainPageActivity.class);
        Back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(Back);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        setTitle("Medication Helper");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        WebView webView = findViewById(R.id.web);

        bottomNavigationView.setSelectedItemId(R.id.pageNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pageNav:
                        return true;

                    case R.id.homeNav:
                        startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.userNav:
                        startActivity(new Intent(getApplicationContext(), MyPageActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
        webView.loadUrl("https://www.hira.or.kr/main1.do");
        webView.setWebViewClient(new WebViewClient());  // 새 창 띄우기 않기
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setLoadWithOverviewMode(true);  // WebView 화면크기에 맞추도록 설정 - setUseWideViewPort 와 같이 써야함
        webView.getSettings().setUseWideViewPort(true);  // wide viewport 설정 - setLoadWithOverviewMode 와 같이 써야함
        webView.getSettings().setSupportZoom(false);  // 줌 설정 여부
        webView.getSettings().setBuiltInZoomControls(false);  // 줌 확대/축소 버튼 여부
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); // javascript가 window.open()을 사용할 수 있도록 설정
        webView.getSettings().setSupportMultipleWindows(true); // 멀티 윈도우 사용 여부
        webView.getSettings().setDomStorageEnabled(true);
    }
}
