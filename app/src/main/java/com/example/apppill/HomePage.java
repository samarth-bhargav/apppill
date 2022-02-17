package com.example.apppill;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomePage extends AppCompatActivity implements View.OnClickListener{

    private Button dictate;
    private TextView banner, description;
    private ImageView display;
    private ActivityResultLauncher launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        description = findViewById(R.id.homePageText);

        display = findViewById(R.id.homePageImage);
        display.setOnClickListener(this);

        dictate = findViewById(R.id.homePageDictate);
        dictate.setOnClickListener(this);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    bitmap = rotateBitmap(bitmap, 90);
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
        }
    }
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void takePictureAndDisplay(){
        if (ContextCompat.checkSelfPermission(HomePage.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA}, 225);
        }
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        launcher.launch(takePicture);
    }
    private static Bitmap rotateBitmap(Bitmap img, int degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImage = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImage;
    }

}