package com.example.apppill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class HomePage extends AppCompatActivity implements View.OnClickListener{

    private TextView homePageLogo, searchButton, scanButton;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.US);
                    speak("Welcome! Please scan or search");
                }
            }
        });
        homePageLogo = findViewById(R.id.homePageLogo);

        searchButton = findViewById(R.id.homePageSearch);
        searchButton.setOnClickListener(this);

        scanButton = findViewById(R.id.homePageScan);
        scanButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.homePageScan:
                startActivity(new Intent(HomePage.this, CameraHomePage.class));
                break;
            case R.id.homePageSearch:
                startActivity(new Intent(HomePage.this, SearchHomePage.class));
                break;
        }
    }
    public void speak(String s){
        tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, HomePage.this.hashCode()+"");
    }
    public void onPause(){
        if (tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }
}