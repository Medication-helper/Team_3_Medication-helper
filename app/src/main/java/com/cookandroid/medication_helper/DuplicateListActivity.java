package com.cookandroid.medication_helper;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

public class DuplicateListActivity extends AppCompatActivity {

    String data;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent Back = new Intent(DuplicateListActivity.this, MedicCheckActivity.class);
        Back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(Back);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicatelist);
        setTitle("Medication Helper");

        Button btnBack = findViewById(R.id.btnback3);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DuplicateListActivity.this, MedicCheckActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ListView DuplicateList=(ListView)findViewById(R.id.duplicateList);
        TextView DuplicateTextView=(TextView)findViewById(R.id.duplicateIng);

        //약 목록을 저장하는 배열
        String[] medicineList={"스포라녹스액(이트라코나졸)","안텐스정(에날라프릴말레산염)","아클론정(아세클로페낙)","알락티스정","가스디알정50밀리그램(디메크로틴산마그네슘)","에이펙스정(아세클로페낙)","올린코정"};

        //약 목록이 저장되어 있는 배열의 길이
        int size=medicineList.length;

        //받은 약 목록 전체의 이름과 효능중복성분을 저장하는 배열(성분이 없으면 [?][1]은 ""이다)
        String[][] medicNameINGList=new String[size][2];

        //OpenApI xml 파싱 스레드
        new Thread(new Runnable() {

            int forbiddenlistSize=0;

            int index=0;

            @Override
            public void run() {

                for(int i=0;i<size;i++){
                    //처방약 목록에서 약 이름을 차례대로 받아 OpenAPI로 처리
                    data=getXmlData(medicineList[i]);

                    medicNameINGList[i][0]=medicineList[i];
                    medicNameINGList[i][1]=data;
                }

                //효능 중복이 있는 약들만 보관할 배열들의 크기를 구한다.
                for(int i=0;i<size;i++){
                    String str=medicNameINGList[i][1];
                    if(TextUtils.isEmpty(str)==false){
                        forbiddenlistSize++;
                    }
                }

                //효능중복 약물에 해당하는 약물들의 이름만 저장하는 배열
                String[] duplicateMedicNameList=new String[forbiddenlistSize];

                for(int i=0;i<size;i++){
                    String str=medicNameINGList[i][1];
                    if(TextUtils.isEmpty(str)==false){
                        duplicateMedicNameList[index]=medicNameINGList[i][0];
                        index++;
                    }
                }

                //효능중복 약물에 해당하는 약물들의 효능만 저장하는 배열
                String[] DuplicateIngredientList=new String[forbiddenlistSize];

                index=0;

                for(int i=0;i<size;i++){
                    String str=medicNameINGList[i][1];
                    if(TextUtils.isEmpty(str)==false){
                        DuplicateIngredientList[index]=medicNameINGList[i][1];
                        index++;
                    }
                }

                //효능중복 사항이 있는 약품 이름 목록을 가지는 arraylist
                ArrayList<String> DuplicateMedicationList=new ArrayList<>(Arrays.asList(duplicateMedicNameList));

                ArrayAdapter DuplicateNameAdapter=new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_single_choice,DuplicateMedicationList);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //화면에 효능 중복 대상 약품 이름 목록 표시
                        DuplicateList.setAdapter(DuplicateNameAdapter);

                        //약품 리스트뷰에서 항목을 선택했을 때 효능을 Textview에 표시
                        DuplicateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String medicineName=(String) adapterView.getAdapter().getItem(i);

                                for(int x=0;x<forbiddenlistSize;x++){
                                    if(medicineName.equals(duplicateMedicNameList[x])==true){
                                        String Ingredient=DuplicateIngredientList[x];
                                        DuplicateTextView.setText(Ingredient);
                                        break;
                                    }
                                }
                            }
                        });

                        DuplicateList.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                DuplicateList.requestDisallowInterceptTouchEvent(true);
                                return false;
                            }
                        });
                    }
                });
            }
        }).start();

    }

    String getXmlData(String medicname) {
        StringBuffer buffer=new StringBuffer();
        String str=medicname;
        String MedicineName= URLEncoder.encode(str);

        String queryUrl="http://apis.data.go.kr/1471000/DURPrdlstInfoService01/getEfcyDplctInfoList?serviceKey=RZnyfUGsOhY2tWWUv262AHpeMQYn4Idqd5cgG0rGNHPd648m5j0Pu3eiS3ewN4XhhHT%2FvuliAmF9KLJdzh1TFA%3D%3D&itemName="+MedicineName+"&pageNo=1&numOfRows=1&type=xml";
        try {
            URL url=new URL(queryUrl);
            InputStream is=url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp=factory.newPullParser();
            xpp.setInput(new InputStreamReader(is,"UTF-8"));

            String tag;

            xpp.next();
            int eventType=xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){

                    case XmlPullParser.START_TAG:
                        tag=xpp.getName();

                        if(tag.equals("item"));
                        else if(tag.equals("EFFECT_NAME")){
                            xpp.next();
                            buffer.append(xpp.getText());
                        }
                        break;
                }
                eventType=xpp.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
    }

}
