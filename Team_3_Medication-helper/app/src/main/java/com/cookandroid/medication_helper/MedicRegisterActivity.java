package com.cookandroid.medication_helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MedicRegisterActivity extends AppCompatActivity {

    Bitmap image;
    Bitmap bitmap;
    Bitmap rotatedbitmap;

    private Uri photoUri;
    private String imageFilepath;

    private TessBaseAPI mTess;
    String datapath = "";

    PreviewView previewView;
    Button btnStartCamera;
    Button btnCaptureCamera;
    Button btnOcr;
    Button btnRegister;
    TextView textView;
    ImageView picture;
    View overlay;

    ProcessCameraProvider processCameraProvider;
    int lensFacing = CameraSelector.LENS_FACING_BACK;
    ImageCapture imageCapture;

    private TextRecognizer textRecognizer;//define TextRecognizer

    String OCRresult;
    static final int REQUEST_IMAGE_CAPTURE = 672;

    String[] EdiCodearray;//EDI 코드 목록을 저장하는 배열
    String[] medicList;//OpenAPI를 이용해 의약품 이름 목록을 저장하는 배열
    String data;

    String ocrResult;

    UserData userData;
    com.cookandroid.medication_helper.MedicDBHelper myHelper;
    SQLiteDatabase sqlDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_medicregister);

        SharedPreferences sharedPreferences=getSharedPreferences("PREF", Context.MODE_PRIVATE);

        final String ocrApiGwUrl = sharedPreferences.getString("https://czt9qlltax.apigw.ntruss.com/custom/v1/16147/e9a1814442c9633751f8b26ebeba60b6f23d612647bbee28a6022693b2c1416b/general", "");
        final String ocrSecretKey = sharedPreferences.getString("UG1rTVZLTWpseUpLWVlESmpZREt6RmZxTURBcmhBR3E=", "");

        userData = (UserData) getApplicationContext();
        myHelper = new com.cookandroid.medication_helper.MedicDBHelper(this);
        previewView = (PreviewView) findViewById(R.id.previewView);
        btnStartCamera = (Button) findViewById(R.id.btnCameraStart);
        btnCaptureCamera = (Button) findViewById(R.id.btnPicture);
        btnRegister = (Button) findViewById(R.id.regimedicbtn);
        textView = (TextView) findViewById(R.id.OCRTextResult);
        picture = (ImageView) findViewById(R.id.imageview);
        overlay = (View) findViewById(R.id.overlay);

        textRecognizer= TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());


        //카메라 촬영을 위한 동의 얻기
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        try {
            processCameraProvider = processCameraProvider.getInstance(this).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //카메라 프리뷰 작동
        btnStartCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MedicRegisterActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    previewView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    btnStartCamera.setVisibility(View.INVISIBLE);
                    btnStartCamera.setEnabled(false);
                    btnCaptureCamera.setVisibility(View.VISIBLE);
                    btnCaptureCamera.setEnabled(true);

                    bindPreview();
                    bindImageCapture();

                }
            }
        });

        //카메라에 접근해 사진 찍는 버튼
        btnCaptureCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageCapture.takePicture(ContextCompat.getMainExecutor(MedicRegisterActivity.this),
                        new ImageCapture.OnImageCapturedCallback() {
                            @Override
                            public void onCaptureSuccess(@NonNull ImageProxy image) {
                                @SuppressLint("UnsafeExperimentalUsageError")
                                Image mediaImage = image.getImage();

                                //카메라에서 가져온 이미지를 비트맵 이미지로 변환
                                bitmap = com.cookandroid.medication_helper.ImageUtil.mediaImageToBitmap(mediaImage);

                                Log.d("result", Integer.toString(bitmap.getWidth())); //4032
                                Log.d("result", Integer.toString(bitmap.getHeight())); //3024
                                Log.d("result", Integer.toString(image.getImageInfo().getRotationDegrees()));

                                //이미지 회전(최종상태)
                                rotatedbitmap = com.cookandroid.medication_helper.ImageUtil.rotateBitmap(bitmap, image.getImageInfo().getRotationDegrees());

                                Log.d("result", Integer.toString(rotatedbitmap.getWidth())); //3024
                                Log.d("result", Integer.toString(rotatedbitmap.getHeight())); //4032
                                Log.d("result", Integer.toString(image.getImageInfo().getRotationDegrees()));

                                //가로, 세로를 중앙을 중심으로 자르기
                                Bitmap cutImage=Bitmap.createBitmap(rotatedbitmap,504,672,2016,2688);

                                processCameraProvider.unbindAll();//카메라 프리뷰 중단
                                previewView.setVisibility(View.INVISIBLE);

                                super.onCaptureSuccess(image);

                                int height = rotatedbitmap.getHeight();
                                int width = rotatedbitmap.getWidth();

                                //AlertDialog에 사용할 비트맵 이미지의 사이즈를 가로세로 비율 맞춰 축소(현재 가로세로 1/6 스케일)
                                Bitmap popupBitmap = Bitmap.createScaledBitmap(cutImage, 900, height / (width / 900), true);

                                Log.d("result", Integer.toString(cutImage.getWidth())); //3096
                                Log.d("result", Integer.toString(cutImage.getHeight())); //4128
                                Log.d("result", Integer.toString(image.getImageInfo().getRotationDegrees()));

                                //카메라 바인딩 사용중단
                                processCameraProvider.unbindAll();

                                ImageView capturedimage = new ImageView(MedicRegisterActivity.this);
                                capturedimage.setImageBitmap(popupBitmap);

                                //사진 촬영 결과를 AlertDialog로 띄워 사용 여부를 선택한다
                                AlertDialog.Builder captureComplete = new AlertDialog.Builder(MedicRegisterActivity.this)
                                        .setTitle("촬영 결과")
                                        .setMessage("이 사진을 사용할까요?")
                                        .setView(capturedimage)
                                        //사용을 선택할 경우 OCR 실행
                                        .setPositiveButton("사용", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Bitmap gray=grayScale(popupBitmap);//사진 GrayScale
                                                Bitmap binary=GetBinaryBitmap(gray);//이진화(내가 보기엔 버려야 할 것 같음)

//                                                textView.setText("촬영 완료");
//                                                textView.setVisibility(View.VISIBLE);
                                                InputImage image=InputImage.fromBitmap(gray,0);//MLKit에서 사용하기 위해서 비트맵에서 InputImage로 변환

                                                //Recognize Text
                                                Task<Text> result = textRecognizer.process(image)
                                                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                                                            @Override
                                                            public void onSuccess(Text text) {
                                                                textView.setText(text.getText());
                                                                textView.setVisibility(View.VISIBLE);

                                                                ocrResult=text.getText().toString();

                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });

//                                                photoUri=saveImage(binary,MedicRegisterActivity.this);
//                                                picture.setImageURI(photoUri);
//                                                //picture.setImageBitmap(binary);
//                                                picture.setVisibility(View.VISIBLE);
                                                overlay.setVisibility(View.INVISIBLE);
                                            }
                                        })
                                        //재촬영을 선택할 경우 bitmap에 저장된 비트맵 파일을 지우고 다시 카메라 프리뷰를 바인딩함
                                        .setNegativeButton("재촬영", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                bitmap = null;
                                                bindPreview();
                                                bindImageCapture();
                                                textView.setVisibility(View.INVISIBLE);

                                                previewView.setVisibility(View.VISIBLE);
                                            }
                                        });

                                captureComplete.setCancelable(false);

                                captureComplete.create().show();
                            }
                        });
            }
        });


        /*복약 등록 버튼*/
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"등록 중, 잠시만 기다려주세요...",Toast.LENGTH_SHORT).show();

                //Log.v("result",ocrResult);
                String[] list=ocrResult.split("\n");

                for(String line:list){
                    System.out.println(line);
                }


                Toast.makeText(getApplicationContext(),"등록했습니다",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.MainPageActivity.class));
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
//        //Button btnPill = findViewById(R.id.pillbtn);
//        //Button btnJar = findViewById(R.id.jarbtn);
        bottomNavigationView.setSelectedItemId(R.id.cameraNav);

        //바텀네비게이션을 나타나게 해주는 함수
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    //home버튼을 누르면 액티비티 화면을 전환시켜준다
                    case R.id.homeNav:
                        startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.MainPageActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    //현재 화면에서 보여주는 액티비티
                    case R.id.cameraNav:
                        return true;
                    //article 버튼을 누르면 액티비티 화면을 전환시켜준다
                    case R.id.articleNav:
                        startActivity(new Intent(getApplicationContext(), MedicineListActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    //user 버튼을 누르면 액티비티 화면을 전환시켜준다
                    case R.id.userNav:
                        startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.MyPageActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
        overlay.bringToFront();
    }

    void bindPreview(){
        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();

        Preview preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        processCameraProvider.bindToLifecycle(this,cameraSelector,preview);
    }

    void bindImageCapture(){
        CameraSelector cameraSelector=new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();
        imageCapture=new ImageCapture.Builder()
                .build();

        processCameraProvider.bindToLifecycle(this,cameraSelector,imageCapture);
    }

    //회색조
    private Bitmap grayScale(final Bitmap orgBitmap){
        int width, height;
        width=orgBitmap.getWidth();
        height=orgBitmap.getHeight();

        Bitmap bmpGrayScale=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_4444);
        Canvas canvas=new Canvas(bmpGrayScale);
        Paint paint=new Paint();
        ColorMatrix colorMatrix=new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixColorFilter=new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixColorFilter);
        canvas.drawBitmap(orgBitmap,0,0,paint);
        return bmpGrayScale;
    }

    //이진화
    private Bitmap GetBinaryBitmap(Bitmap bitmap_src) {

        Bitmap bitmap_new=bitmap_src.copy(bitmap_src.getConfig(), true);

        for(int x=0; x<bitmap_new.getWidth(); x++) {

            for(int y=0; y<bitmap_new.getHeight(); y++) {

                int color=bitmap_new.getPixel(x, y);

                color=GetNewColor(color);

                bitmap_new.setPixel(x, y, color);

            }

        }

        return bitmap_new;

    }

    private int GetNewColor(int c) {

        double dwhite=GetColorDistance(c, Color.WHITE);

        double dblack=GetColorDistance(c,Color.BLACK)*0.4;

        if(dwhite<=dblack) {

            return Color.WHITE;

        }

        else {

            return Color.BLACK;

        }

    }

    private double GetColorDistance(int c1, int c2) {

        int db= Color.blue(c1)-Color.blue(c2);

        int dg=Color.green(c1)-Color.green(c2);

        int dr=Color.red(c1)-Color.red(c2);

        double d=Math.sqrt(  Math.pow(db, 2) + Math.pow(dg, 2) +Math.pow(dr, 2)  );

        return d;

    }

    //약 이름을 이용해 공공데이터 포털에서 약 정보 알아내기
    String getXmlData(String medicname) {
        StringBuffer buffer=new StringBuffer();
        String str=medicname;
        String MedicineName= URLEncoder.encode(str);


        String queryUrl="https://apis.data.go.kr/1471000/MdcinGrnIdntfcInfoService01/getMdcinGrnIdntfcInfoList01?serviceKey=RZnyfUGsOhY2tWWUv262AHpeMQYn4Idqd5cgG0rGNHPd648m5j0Pu3eiS3ewN4XhhHT%2FvuliAmF9KLJdzh1TFA%3D%3D&itemName="+MedicineName+"&pageNo=1&numOfRows=1&type=xml";
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

                        //약품 이미지 URL
                        else if(tag.equals("ITEM_NAME")){
                            xpp.next();
                            buffer.append(xpp.getText());
                        }
                        //약품 회사명
                        else if(tag.equals("ENTP_NAME")){
                            xpp.next();
                            buffer.append(xpp.getText());
                        }
                        //약품 이미지 URL
                        else if(tag.equals("ITEM_IMAGE")){
                            xpp.next();
                            buffer.append(xpp.getText());
                        }
                        //약품 종류
                        else if(tag.equals("ITEM_CLASS")){
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
