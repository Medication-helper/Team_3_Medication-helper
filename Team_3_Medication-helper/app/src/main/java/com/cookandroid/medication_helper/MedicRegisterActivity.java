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


        //언어 파일 경로 설정
        datapath = getFilesDir() + "/tessaract/";

        //언어 파일 존재 여부 확인
        checkFile(new File(datapath + "tessdata/"), "eng");

        String lang = "eng";

        mTess = new TessBaseAPI();//TessBaseAPI 생성
        mTess.init(datapath, lang);//초기화

        //숫자만 인식해서 추출하도록 블랙리스트, 화이트리스트 설정
        mTess.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, ".,!?@#$%&*()<>_-+=/:;'\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789");

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

    //스마트폰에 사진 파일 복사
    private void copyFiles(String lang){
        try{
            String filepath=datapath+"/tessdata/"+lang+".traineddata";

            AssetManager assetManager=getAssets();

            InputStream inputStream=assetManager.open("tessdata/"+lang+".traineddata");
            OutputStream outputStream=new FileOutputStream(filepath);

            byte[] buffer=new byte[1024];
            int read;

            while ((read=inputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //스마트폰에 파일이 있는 지 확인
    private void checkFile(File dir, String lang){
        if(!dir.exists()&&dir.mkdirs()){
            copyFiles(lang);
        }
        if(dir.exists()){
            String datafilepath=datapath+"/tessdata/"+lang+".traineddata";
            File datafile=new File(datafilepath);
            if(!datafile.exists()){
                copyFiles(lang);
            }
        }
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

    //비트맵 객체 캐시 저장소에 jpg 형식으로 저장
    public Uri saveImage(Bitmap bitmap,Context context){
        File imagesFolder=new File(context.getCacheDir(),"images");
        Uri uri=null;

        try{
            imagesFolder.mkdirs();
            File file=new File(imagesFolder,"capturedimage.jpg");
            FileOutputStream stream=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            stream.flush();
            stream.close();

            uri=FileProvider.getUriForFile(context.getApplicationContext(),"com.cookandroid.medication_helper.fileprovider",file);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return uri;
    }
}
