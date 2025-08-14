package com.example.translator;

import android.util.Log;
import com.example.translator.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import java.util.HashMap;
import java.util.Map;

public class Firebase {
    private static final String TAG = "FireBaseUtil";
    private static final String COLLECTION_USERS = "users";
    private static FirebaseFirestore db;


    public static void initFirestore() {
        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true) // Enable offline persistence
                .build();
        db.setFirestoreSettings(settings);

        Log.d(TAG, "Firestore initialized successfully.");
    }


    public static void addUser(UserModel userModel) {
        if (db == null) {
            initFirestore();
        }

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("nickname", userModel.getNickname());
        userMap.put("email", userModel.getEmail());
        userMap.put("createdTimestamp", userModel.getCreatedTimestamp());
        userMap.put("userId", userModel.getUserId());

        db.collection(COLLECTION_USERS)
                .document(userModel.getUserId())
                .set(userMap)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User added successfully: " + userModel.getUserId()))
                .addOnFailureListener(e -> Log.e(TAG, "Error adding user: ", e));
    }


    public static void getUser(String userId, OnCompleteListener<DocumentSnapshot> listener) {
        if (db == null) {
            initFirestore();
        }

        db.collection(COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnCompleteListener(listener)
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching user: ", e));
    }


    public static void updateUser(String userId, Map<String, Object> updates) {
        if (db == null) {
            initFirestore();
        }

        db.collection(COLLECTION_USERS)
                .document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User updated successfully: " + userId))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating user: ", e));
    }


    public static void deleteUser(String userId) {
        if (db == null) {
            initFirestore();
        }

        db.collection(COLLECTION_USERS)
                .document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User deleted successfully: " + userId))
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting user: ", e));
    }
}
