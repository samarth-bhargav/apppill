package com.example.apppill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DisplayUserInfo extends AppCompatActivity implements View.OnClickListener {

    private Button yesButton, noButton;
    private TextView displayMedicine, displayDosage, displayLastTimeTaken, displayLogo;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String medicineText;
    public String medicine;
    public String dosage;
    public String lastTimeTaken;
    private MedicineDatabase medicineDatabase;
    private TextToSpeech tts;
    private boolean ttsReady = false;
    DocumentReference medicineLog;
    CollectionReference medicines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_info);

        setup();
        db = FirebaseFirestore.getInstance();
        medicineDatabase = new MedicineDatabase();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        try {
            medicineDatabase.init(DisplayUserInfo.this);
        } catch (IOException e) {
            e.printStackTrace();
        }



        yesButton = findViewById(R.id.UserInfoYes);
        yesButton.setOnClickListener(this);

        noButton = findViewById(R.id.UserInfoNo);
        noButton.setOnClickListener(this);

        displayMedicine = findViewById(R.id.UserInfoMedicine);
        displayDosage = findViewById(R.id.UserInfoDosage);
        displayLastTimeTaken = findViewById(R.id.UserInfoLastTimeTaken);

        medicineText = getIntent().getExtras().getString("Text");
        medicine = medicineDatabase.getMedicine(medicineText);
        dosage = medicineDatabase.getDosage(medicineText);
        displayMedicine.setText("Medicine: " + medicine);
        displayDosage.setText("Dosage: " + dosage);

        medicineLog = db.collection("users")
                .document(currentUser.getUid())
                .collection("Medicines")
                .document(medicine);
        displayLastTimeTaken();
        SystemClock.sleep(3000);
        if (lastTimeTaken == null){
            lastTimeTaken = "error";
        }
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {

                    if (lastTimeTaken.equals("First Time Taking Pill")) {
                        speak("Your medicine is " + medicine);
//                        while (tts.isSpeaking()) {SystemClock.sleep(500);}
                        speak("Your Dosage Is " + dosage);
//                        while (tts.isSpeaking()) {SystemClock.sleep(500);}
                        speak("This is the first time you are taking " + medicine);
                    }
                    else {
                        speak("Your medicine is " + medicine);
//                        while (tts.isSpeaking()) {SystemClock.sleep(500);}
                        speak("Your Dosage Is " + dosage);
//                        while (tts.isSpeaking()) {SystemClock.sleep(500);}
                        speak("The Last Time You Took " + medicine + " is " + lastTimeTaken);
                    }
                }
            }
        });
        displayLogo = findViewById(R.id.UserInfoLogo);
        displayLogo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.UserInfoLogo:
                startActivity(new Intent(DisplayUserInfo.this, MainActivity.class));
                break;
            case R.id.UserInfoYes:
                medicineLogUpdate();
                speak("You took " + medicine);
                while (tts.isSpeaking()){}
                startActivity(new Intent(DisplayUserInfo.this, CameraHomePage.class));
                break;
            case R.id.UserInfoNo:
                speak("You did not take " + medicine);
                while (tts.isSpeaking()){}
                Toast.makeText(DisplayUserInfo.this, "You did not take " + medicine, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DisplayUserInfo.this, CameraHomePage.class));
                break;
        }
    }

    public String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public void displayLastTimeTaken() {
        medicineLog.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            //user has taken medication before!
                            Object temp = documentSnapshot.get("lastTimeTaken");
                            if (temp == null){
                                lastTimeTaken = "what";
                            }
                            else{
                                lastTimeTaken = temp.toString();
                            }
                            displayLastTimeTaken.setText("Last Time Taken: " + lastTimeTaken);
//                            tts.speak("Last Time Taken: " + documentSnapshot.get("lastTimeTaken").toString(), TextToSpeech.QUEUE_FLUSH, null);
                        }
                        else{
//                            tts.speak("This is your first time taking this medicine", TextToSpeech.QUEUE_FLUSH, null);
                            lastTimeTaken = "First Time Taking Pill";
                            displayLastTimeTaken.setText("First Time Taking Pill");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        tts.speak("Could not find Document", TextToSpeech.QUEUE_FLUSH, null);
                        Toast.makeText(DisplayUserInfo.this, "Could not find Document", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    public void medicineLogUpdate() {
        medicineLog.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                Map<String, Object> lastTimeTaken = new HashMap<>();
                lastTimeTaken.put("lastTimeTaken", getCurrentTime());
                if (doc.exists()){
                    medicineLog.update(lastTimeTaken);
                }
                else{
                    //first time taking pill
                    medicineLog.set(lastTimeTaken);
                }
            }
        });
    }
    public void speak(String s){
        tts.speak(s, TextToSpeech.QUEUE_ADD, null, this.hashCode()+"");
    }
    @Override
    public void onStop() {
        if (tts != null) {
            tts.stop();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.shutdown();
        }
        super.onDestroy();
    }
    public void setup(){

    }
}