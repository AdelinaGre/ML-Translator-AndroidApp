package com.example.translator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.translator.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, signupNickname;
    private String email, pass, nickname;
    private Button signupButton;
    private FirebaseFirestore firestore;
    private TextView loginRedirectText;
    UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupNickname = findViewById(R.id.signup_nickname);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        FirebaseUser user = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = signupEmail.getText().toString().trim();
                pass = signupPassword.getText().toString().trim();
                nickname = signupNickname.getText().toString().trim();

                if (email.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }
                if (pass.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                } if (nickname.isEmpty()){
                    signupNickname.setError("Nickname cannot be empty");
                }else{

                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                setNickname();
                                FirebaseUser currentUser = auth.getCurrentUser();
                                saveUserToFirestore(currentUser.getUid(), email, nickname);
                                Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                            } else {
                                Toast.makeText(SignUpActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }
        });
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
            }
        });
    }
    void setNickname() {
        if (nickname.isEmpty()) {
            signupNickname.setError("Nickname should be at least 1 character");
            return;
        }

        if (userModel == null) {
            userModel = new UserModel(nickname, email, Timestamp.now(),FireBaseUtil.currentUserId());
        }

        DocumentReference userRef = FireBaseUtil.currentUserDetails();
        if (userRef != null) {
            userRef.set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Failed to save user details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(SignUpActivity.this, "User reference is null", Toast.LENGTH_SHORT).show();
        }
    }

    void getNickname(){
        FireBaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    userModel=task.getResult().toObject(UserModel.class);
                    if (userModel!= null){
                        signupNickname.setText(userModel.getNickname());
                    }
                }
            }
        });
    }
    private void saveUserToFirestore(String userId, String email, String nickname) {

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", userId);
        userMap.put("email", email);
        userMap.put("nickname", nickname);
        userMap.put("createdAt", Timestamp.now());


        firestore.collection("users").document(userId).set(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "User created successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                        } else {
                            Toast.makeText(SignUpActivity.this, "Failed to save user details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
