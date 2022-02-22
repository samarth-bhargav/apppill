package com.example.apppill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{
    private TextView banner, register;
    private EditText editFullName;
    private EditText editEmail;
    private EditText editPassword;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextToSpeech tts;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.UK);
                    tts.speak("Please Register", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        banner = this.findViewById(R.id.logo);
        banner.setOnClickListener(this);

        register = this.findViewById(R.id.registerButton);
        register.setOnClickListener(this);

        editFullName = findViewById(R.id.fullname);
        editEmail = findViewById(R.id.email);
        editPassword = findViewById(R.id.password);

        progressBar = findViewById(R.id.registerProgressBar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.banner:
                startActivity(new Intent(RegisterUser.this, MainActivity.class));
                break;
            case R.id.registerButton:
                registerUser();
                break;
        }
    }
    public void registerUser(){
        String fullName = editFullName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (fullName.isEmpty()){
            editFullName.setError("Full Name is Required");
            tts.speak("Full Name is Required", TextToSpeech.QUEUE_FLUSH, null);
            editFullName.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Email is Required");
            tts.speak("Email is Required", TextToSpeech.QUEUE_FLUSH, null);
            editEmail.requestFocus();
            return;
        }
        if (password.length() < 6){
            editPassword.setError("Password must be at least 6 characters");
            tts.speak("Password needs to be 6 characters", TextToSpeech.QUEUE_FLUSH, null);
            editPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Map<String, Object> user = new HashMap<>();
                            user.put("Email", email);
                            user.put("fullName", fullName);
                            db.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            tts.speak("User has been registered", TextToSpeech.QUEUE_FLUSH, null);
                                            Toast.makeText(RegisterUser.this, "User has been registered!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            startActivity(new Intent(RegisterUser.this, MainActivity.class));
                                        }
                                    });
                        }
                        else{
                            tts.speak("Failed to Register", TextToSpeech.QUEUE_FLUSH, null);
                            Toast.makeText(RegisterUser.this, "Failed To Register", Toast.LENGTH_LONG).show();
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