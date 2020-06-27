package com.mohammedsalah.learnhowtolearn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class chat extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private EditText text_message;
    private ImageView imag_delete;
    private DatabaseReference root;
    private ImageView send_msg;
    private String user_id;
    String name;


    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        MobileAds.initialize(this,"ca-app-pub-5553020504531246~2909425441");
        adView = findViewById(R.id.addd);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        Intent intent = getIntent();
        String ti = intent.getStringExtra("id");

        root = FirebaseDatabase.getInstance().getReference().child(ti+"_ChatRoom");

        text_message = findViewById(R.id.Edit_chatmessage);
        imag_delete = findViewById(R.id.delete);
        send_msg = findViewById(R.id.send);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid(); // get current user id



        text_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (text_message.getText().toString().isEmpty()) {
                    send_msg.setVisibility(View.INVISIBLE);
                } else {
                    send_msg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    // check if data is exist
                    if (task.getResult().exists()){
                        // get data from data base
                        name = task.getResult().getString("name");
                    }
                }
            }
        });

        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.push().setValue(new chatclass(name,text_message.getText().toString().trim()));
                text_message.setText("");
                display_messg();
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                display_messg();

            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                display_messg();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public void display_messg () {
        ListView listView = findViewById(R.id.listview);
        FirebaseListAdapter<chatclass> adap;
        adap = new FirebaseListAdapter<chatclass>(this, chatclass.class, R.layout.chat_item, root) {
            @Override
            protected void populateView(View v, chatclass model, int position) {
                TextView textuser, textmessg,texttime;
                textmessg = v.findViewById(R.id.mess_text);
                textuser = v.findViewById(R.id.mess_username);
                texttime = v.findViewById(R.id.mess_time);

                textmessg.setText(model.getText());
                textuser.setText(model.getName());
                texttime.setText(DateFormat.format("dd-MM-yyyy , hh:mm a",model.getDate()));

            }
        };
        adap.notifyDataSetChanged();
        listView.setAdapter(adap);
    }


    public void delete(View view) {
        text_message.setText("");
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


    }

