package com.example.translator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.translator.model.UserModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class QuizActivity extends AppCompatActivity {
    private EditText etNickname, etEmail, etPassword;
    private Button btnUpdate;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        etNickname = findViewById(R.id.et_nikname);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnUpdate = findViewById(R.id.btn_update);

        loadUserData();
        btnUpdate.setOnClickListener(v -> updateUserData());


        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_quiz);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_quiz) {
                startActivity(new Intent(getApplicationContext(), QuizActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_voice) {
                startActivity(new Intent(getApplicationContext(), VoiceActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_camera) {
                startActivity(new Intent(getApplicationContext(), CameraActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }else if (item.getItemId() == R.id.nav_text) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }else if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
    });
    }

    private void loadUserData() {

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        String nickname = documentSnapshot.getString("nickname");
                        String email = documentSnapshot.getString("email");


                        etNickname.setText(nickname);
                        etEmail.setText(email);


                        Toast.makeText(QuizActivity.this, "User data loaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(QuizActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(QuizActivity.this, "Failed to load data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserData() {

        String nickname = etNickname.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();


        if (nickname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        UserModel userModel = new UserModel(nickname, email, Timestamp.now(), userId);


        db.collection("users")
                .document(userId)
                .set(userModel)
                .addOnSuccessListener(aVoid -> {

                    Toast.makeText(QuizActivity.this, "User data updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(QuizActivity.this, "Failed to update data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
