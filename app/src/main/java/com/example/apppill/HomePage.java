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
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        Toast.makeText(HomePage.this, "hi", Toast.LENGTH_LONG).show();
        if (takePicture.resolveActivity(getPackageManager()) == null){
            launcher.launch(takePicture);
        }
        else{
            Toast.makeText(HomePage.this, "There is an issue :((", Toast.LENGTH_SHORT).show();
        }
    }
}