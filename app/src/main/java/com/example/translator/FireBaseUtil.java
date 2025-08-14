package com.example.translator;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireBaseUtil {
    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }
    public static DocumentReference currentUserDetails() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUserId());
        }
        return null;
    }

    public static CollectionReference allUsersCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }
    public static DocumentReference getChatRoomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatRoomMessageReference(String chatroomId){
        return getChatRoomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId_1, String userId_2){
        if(userId_1.hashCode()<userId_2.hashCode()){
            return userId_1+"_"+userId_2;
        }else{
            return userId_2+"_"+userId_1;
        }
    }
}
