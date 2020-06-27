package com.mohammedsalah.learnhowtolearn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Reciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            String user_id = firebaseAuth.getCurrentUser().getUid(); // get current user id
            firebaseFirestore.collection("online_users_counter").document(user_id).delete();
        }

        if (firebaseAuth.getCurrentUser()!=null){
            String user_id = firebaseAuth.getCurrentUser().getUid(); // get current user id
            Map<String, Object> mp = new HashMap<>();
            mp.put("status_id", "");
            firebaseFirestore.collection("users").document(user_id).update(mp);
        }
    }
}
