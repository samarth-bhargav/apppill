package com.example.apppill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginUser extends AppCompatActivity implements View.OnClickListener{
    private TextView banner, register;
    private EditText editFullName;
    private EditText editUsername;
    private EditText editPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_register_user);
        //^obv not for login idk j do the gui it's 11.37 at night oke
        mAuth = FirebaseAuth.getInstance();

        banner = this.findViewById(R.id.logo);
        banner.setOnClickListener(this);

        register = this.findViewById(R.id.registerButton);
        register.setOnClickListener(this);

        editUsername = findViewById(R.id.email);
        editPassword = findViewById(R.id.password);

        progressBar = findViewById(R.id.registerProgressBar);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.banner:
                startActivity(new Intent(LoginUser.this, MainActivity.class));
                break;
            case R.id.LoginButton:
                loginUser();
                break;
        }
    }
    //idk whatever shit needs to go here to make it like work and shit
    public void loginUser(){
        String username = editUsername.getText().toString().trim();
        String youshallnotpass = editPassword.getText().toString.trim();
        if(username.length() == 0){
            editUsername.setError("put in a username lmao");
            editUsername.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            editUsername.setError("put in a username that actually exists lmao");
            editUsername.requestFocus();
            return;
        }
        if(youshallnotpass.length() < 6){
            editPassword.setError("put in a valid pw lol");
            editPassword.requestFocus();
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(username, youshallnotpass).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> amongus){
                if(amongus.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(LoginUser.this, MainActivity.class));
                    finish();
                }
            }
        });
    }
}
