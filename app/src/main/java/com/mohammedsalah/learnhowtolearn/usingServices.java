package com.mohammedsalah.learnhowtolearn;

import android.app.Service;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class usingServices extends Service {


    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try{

            Uri no = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),no);
            r.play();

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

        }catch (Exception e){
            e.printStackTrace();
        }


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    }


