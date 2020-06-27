package com.mohammedsalah.learnhowtolearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class settingoptions extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingoptions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        MobileAds.initialize(this,"ca-app-pub-5553020504531246~2909425441");
        adView = findViewById(R.id.a_d);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    public void sign_out(View view) {
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

        firebaseAuth.signOut();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();

    }

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser()==null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void contact_us(View view) {
        Intent i = new Intent(getApplicationContext(),cont.class);
        startActivity(i);
    }

    public void rate(View view) {
        Intent i = new Intent(getApplicationContext(),feedback.class);
        startActivity(i);
    }
}
