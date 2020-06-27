package com.mohammedsalah.learnhowtolearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class comments extends AppCompatActivity {
    ImageView postbtn;
    EditText editText;
    String CurrentUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        MobileAds.initialize(this,"ca-app-pub-5553020504531246~2909425441");
        adView = findViewById(R.id.ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        postbtn = findViewById(R.id.post_ask);
        editText = findViewById(R.id.Edit_ask);
        CurrentUser=firebaseAuth.getCurrentUser().getUid();

        final List<comments_class> list  = new ArrayList<>();
        Intent intent = getIntent();
        final String Id = intent.getStringExtra("id");

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @SuppressLint("RestrictedApi")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getText().toString().trim().isEmpty()){
                    postbtn.setVisibility(View.INVISIBLE);
                }else {
                    postbtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        RecyclerView recyclerView = findViewById(R.id.comment_recyclerVeiw);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        final comments_adapter adapter = new comments_adapter(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        // Reterive comments

        firebaseFirestore.collection(Id+"_comments")
            .addSnapshotListener(comments.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        String post_id = documentChange.getDocument().getId();
                        comments_class comment = documentChange.getDocument().toObject(comments_class.class).withId(post_id);
                        list.add(comment);
                        adapter.notifyDataSetChanged();
                    }
                }
            });


        // post comments

        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                if (firebaseAuth.getCurrentUser() != null) {

                    Toast.makeText(getApplicationContext(), "Post...", Toast.LENGTH_LONG).show();
                    final String k = editText.getText().toString().trim();
                    final String user_id = firebaseAuth.getCurrentUser().getUid();

                    firebaseFirestore.collection("users").document(user_id).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        // check if data is exist
                                        if (task.getResult().exists()) {
                                            String nameText = task.getResult().getString("name");
                                            String uri = task.getResult().getString("image");

                                            Toast.makeText(getApplicationContext(), "Post..", Toast.LENGTH_LONG).show();
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("nameuser", nameText);
                                            map.put("userimage", uri);
                                            map.put("ask",k );
                                            map.put("userid", CurrentUser);
                                            map.put("comm_post", Id);
                                            map.put("id",user_id);
                                            map.put("date", DateFormat.format("dd-MM-yyyy , hh:mm a", new Date().getTime()));
                                            final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.notif);
                                            firebaseFirestore.collection(Id+"_comments")
                                                    .add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        mp.start();
                                                        Toast.makeText(getApplicationContext(), "Question added!", Toast.LENGTH_SHORT).show();
                                                        editText.setText("");
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        editText.setText("");
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                }
            }
        });
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
