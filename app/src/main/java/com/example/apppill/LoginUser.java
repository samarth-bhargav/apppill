package com.example.apppill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class LoginUser extends AppCompatActivity implements View.OnClickListener{
    private TextView banner, register;
    private EditText editEmail;
    private EditText editPassword;
    private ProgressBar progressBar;
    private Button login;
    private TextToSpeech tts;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        //^obv not for login idk j do the gui it's 11.37 at night oke
        mAuth = FirebaseAuth.getInstance();

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.UK);
                    tts.speak("Please Login", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        banner = this.findViewById(R.id.loginLogo);
        banner.setOnClickListener(this);

        editEmail = findViewById(R.id.loginEmail);
        editPassword = findViewById(R.id.loginPassword);

        login = findViewById(R.id.loginButton);
        login.setOnClickListener(this);

        progressBar = findViewById(R.id.loginProgressBar);
        progressBar.setVisibility(View.GONE);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.banner:
                startActivity(new Intent(LoginUser.this, MainActivity.class));
                break;
            case R.id.loginButton:
                loginUser();
                break;
        }
    }
    public void loginUser(){
        progressBar.setVisibility(View.VISIBLE);
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        if(email.length() == 0){
            editEmail.setError("Please enter an email");
            editEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Please enter a valid email");
            editEmail.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> login){
                        if(login.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginUser.this, "Welcome", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginUser.this, HomePage.class));
                        }
                        else{
                            Toast.makeText(LoginUser.this, "Something Went Wrong :(", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
        }
    public void onPause(){
        if (tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }
}
