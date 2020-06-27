package com.mohammedsalah.learnhowtolearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

    /*

          BY : MOHAMED SALAH
          DATE : 10 / 4 / 2020

     */

public class MainActivity extends AppCompatActivity {
    private EditText email, password;
    FirebaseAuth firebaseAuth;
    TextView forgetPass;
    private FirebaseFirestore firebaseFirestore;
    String user_id;

    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-5553020504531246~2909425441");
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        email = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        firebaseAuth = FirebaseAuth.getInstance();
        forgetPass = findViewById(R.id.forget_pass);
        firebaseFirestore = FirebaseFirestore.getInstance();

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, reset_pass.class));
            }
        });


    }

    public void OpenSignupPage(View view) {
        startActivity(new Intent(MainActivity.this, SignupActivity.class));
        finish();
    }

    public void login(final View view) {
        log();
    }
    public void log(){
        final String Textmail = email.getText().toString().trim();
        final String Textpass = password.getText().toString().trim();
        if (!TextUtils.isEmpty(Textmail) && !TextUtils.isEmpty(Textpass)) {
            Toast.makeText(getApplicationContext(), "Login...", Toast.LENGTH_SHORT).show();

            firebaseAuth.signInWithEmailAndPassword(Textmail, Textpass).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            update();
                            Toast.makeText(getApplicationContext(), "Welcome back!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        } else
            Toast.makeText(getApplicationContext(), "Fill All Texts", Toast.LENGTH_LONG).show();
    }



    public void update(){

        if (firebaseAuth.getCurrentUser() != null) {
            Map<String, Object> map = new HashMap<>();
            Map<String, Object> mp = new HashMap<>();
            user_id = firebaseAuth.getCurrentUser().getUid();
            Intent intent = new Intent(MainActivity.this,Reciver.class);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mint = new SimpleDateFormat("mm");
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

            String m = mint.format(calendar.getTime());
            int cur = Integer.valueOf(m);
            if (cur <= 20)calendar.set(Calendar.MINUTE,50);
            else if(cur > 20 && cur<=30) calendar.set(Calendar.MINUTE,00);
            else if(cur > 30 && cur<=40) calendar.set(Calendar.MINUTE,10);
            else if(cur > 40 && cur<=50) calendar.set(Calendar.MINUTE,20);
            else if(cur > 50) calendar.set(Calendar.MINUTE,30);


            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR ,
                    PendingIntent.getBroadcast(getApplicationContext(),
                            0,intent,PendingIntent.FLAG_UPDATE_CURRENT));

            map.put("id", user_id);
            firebaseFirestore.collection("online_users_counter").document(user_id).set(map);

            mp.put("status_id", m);
            firebaseFirestore.collection("users").document(user_id).update(mp);

            Intent i = new Intent(getApplicationContext(), category.class);
            startActivity(i);
            finish();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        // check intertnt connection
        InterntConections connectionInternet = new InterntConections(getApplicationContext());
        Boolean c = connectionInternet.IsConnectToInternt();
        if (!c){
            final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setMessage("\" No Internet Connection \"");
            alert.setTitle("Network Error!");
            alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alert.setCancelable(true);
                }
            });
            alert.create();
            alert.show();
        }
        else {

        firebaseFirestore.collection("online_users_counter").
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        try {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                int counter = queryDocumentSnapshots.size();
                                if (counter >= 100 && firebaseAuth.getCurrentUser() == null) {
                                    Toast.makeText(getApplicationContext(), "App has max " + counter + " online users\nTry later!", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(getApplicationContext(), SignupActivity.class);
                                    startActivity(i);

                                }   else if (counter >= 100 && firebaseAuth.getCurrentUser() != null){
                                    Toast.makeText(getApplicationContext(), "App has max " + counter + " online users\nTry later!", Toast.LENGTH_LONG).show();
                                    firebaseAuth.signOut();
                                    Intent i = new Intent(getApplicationContext(), SignupActivity.class);
                                    startActivity(i);
                                }else if (firebaseAuth.getCurrentUser()!=null){
                                    Intent i = new Intent(getApplicationContext(), category.class);
                                    startActivity(i);
                                }

                            }

                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });



        }

        }



        }

