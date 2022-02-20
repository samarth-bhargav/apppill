package com.example.apppill;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class HomePage extends AppCompatActivity implements View.OnClickListener{

    private Button dictate;
    private TextView banner, description;
    private Text resultText;
    private Medicine medicine;
    private ImageView display;
    private ActivityResultLauncher launcher;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        medicine = new Medicine();
        try {
            medicine.init(HomePage.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        description = findViewById(R.id.homePageText);

        display = findViewById(R.id.homePageImage);
        display.setOnClickListener(this);

        dictate = findViewById(R.id.homePageDictate);
        dictate.setOnClickListener(this);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Toast.makeText(HomePage.this, "Hihi", Toast.LENGTH_SHORT).show();
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    bitmap = rotateBitmap(bitmap, 90);
                    TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                    InputImage image = InputImage.fromBitmap(bitmap, 0);
                    Task<Text> processedImage =
                            recognizer.process(image)
                                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                                        @Override
                                        public void onSuccess(Text text) {
                                            resultText = text;
                                            Toast.makeText(HomePage.this, "Processed Image :D", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                    Toast.makeText(HomePage.this, "So it processed the image", Toast.LENGTH_SHORT).show();
                    display.setImageBitmap(bitmap);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.homePageImage:
                takePictureAndDisplay();
                break;
            case R.id.homePageDictate:
                if (resultText != null){
                    description.setText(resultText.getText());
                    boolean noMedicine = true;
                    for (Text.TextBlock block : resultText.getTextBlocks()){
                        for (Text.Line line: block.getLines()){
                            for (Text.Element word : line.getElements()){
                                if (medicine.isMedicine(word.getText().toLowerCase(Locale.ROOT))){
                                    Toast.makeText(HomePage.this, "Medicine: " + word.getText(), Toast.LENGTH_SHORT).show();
                                    noMedicine = false;
                                }
                            }
                        }
                    }
                    if (noMedicine)
                        Toast.makeText(HomePage.this, "Please take a more proper image", Toast.LENGTH_SHORT).show();
                    else{
                        startActivity(new Intent(HomePage.this, DisplayUserInfo.class));
                    }
                }
                else{
                    Toast.makeText(HomePage.this, "Please take an image", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.homePageLogo:
                startActivity(new Intent(HomePage.this, MainActivity.class));
                break;
        }
    }
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void takePictureAndDisplay(){
        if (ContextCompat.checkSelfPermission(HomePage.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 225);
        }
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Toast.makeText(HomePage.this, "Hi", Toast.LENGTH_SHORT).show();
        launcher.launch(takePicture);
    }
    private static Bitmap rotateBitmap(Bitmap img, int degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImage = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImage;
    }
//    private static File getOutputMediaFile(){
//        String fileName = "temp";
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Medicine";
//        File temp = new File(path,fileName+".jpg");
//
//        return temp;
//    }
}