package com.example.apppill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Map;

public class DisplayUserInfo extends AppCompatActivity implements View.OnClickListener {

    private Button yesButton, noButton;
    private TextView displayMedicine, displayDosage, displayLastTimeTaken, displayLogo;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String medicineText;
    public String medicine;
    public String dosage;
    private MedicineDatabase medicineDatabase;
    private TextToSpeech tts;
    DocumentReference medicineLog;
    CollectionReference medicines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_info);

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

        Toast.makeText(DisplayUserInfo.this, currentUser.getUid(), Toast.LENGTH_SHORT).show();
        Toast.makeText(DisplayUserInfo.this, currentUser.getEmail(), Toast.LENGTH_SHORT).show();

        medicineText = getIntent().getExtras().getString("Text");
        medicine = medicineDatabase.getMedicine(medicineText);
        dosage = medicineDatabase.getDosage(medicineText);
        displayMedicine.setText("Medicine: " + medicine);
        displayDosage.setText("Dosage: " + dosage);
        medicineLog = db.collection("users")
                .document(currentUser.getUid())
                .collection("Medicines")
                .document(medicine);
        medicines = db.collection("users")
                .document(currentUser.getUid())
                .collection("Medicines");
        displayLastTimeTaken();

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
                startActivity(new Intent(DisplayUserInfo.this, HomePage.class));
                break;
            case R.id.UserInfoNo:
                Toast.makeText(DisplayUserInfo.this, "You did not take " + medicine, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DisplayUserInfo.this, HomePage.class));
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
                        if (documentSnapshot.exists()) {
                            //user has taken medication before!
                            displayLastTimeTaken.setText("Last Time Taken: " + documentSnapshot.get("lastTimeTaken").toString());
                        } else {
                            displayLastTimeTaken.setText("First Time Taking Pill");
                            Map<String, Object> lastTimeTaken = new HashMap<>();
                            lastTimeTaken.put("lastTimeTaken", "First Time Taking Pill");
                            medicineLog.set(lastTimeTaken);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DisplayUserInfo.this, "Could not find Document", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void medicineLogUpdate() {
        medicineLog.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> lastTimeTaken = new HashMap<>();
                lastTimeTaken.put("lastTimeTaken", getCurrentTime());
                medicineLog.update(lastTimeTaken).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(DisplayUserInfo.this, "Sucesfully Updated :)", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DisplayUserInfo.this, "Could Not Update Database", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}