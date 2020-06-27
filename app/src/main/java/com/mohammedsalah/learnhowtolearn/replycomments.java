package com.mohammedsalah.learnhowtolearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class replycomments extends AppCompatActivity {
    ImageView postbtn;
    EditText editText;
    String CurrentUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String postid;


    @Override
    public void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser()==null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replycomments);

        Intent intent = getIntent();
        final String c = intent.getStringExtra("c");

        postid = getIntent().getStringExtra("postId");


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        postbtn = findViewById(R.id.post_reply);
        editText = findViewById(R.id.Edit_reply);
        CurrentUser = firebaseAuth.getCurrentUser().getUid();

        final List<replyclass> list = new ArrayList<>();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @SuppressLint("RestrictedApi")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getText().toString().trim().isEmpty()) {
                    postbtn.setVisibility(View.INVISIBLE);
                } else {
                    postbtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        RecyclerView recyclerView = findViewById(R.id.comment_recyclerVeiw);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        final replyadapter adapter = new replyadapter(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        // Reterive comments

        firebaseFirestore.collection(c).document(postid).collection("comments")
                .addSnapshotListener(replycomments.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            replyclass reply = documentChange.getDocument().toObject(replyclass.class);
                            list.add(reply);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });


        // post comments

        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                if (firebaseAuth.getCurrentUser() != null) {

                    Toast.makeText(getApplicationContext(), "Comment...", Toast.LENGTH_LONG).show();

                    final String user_id = firebaseAuth.getCurrentUser().getUid();
                    final String x = editText.getText().toString();
                    firebaseFirestore.collection("users").document(user_id).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        // check if data is exist
                                        if (task.getResult().exists()) {
                                            String nameText = task.getResult().getString("name");
                                            String uri = task.getResult().getString("image");
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("name", nameText);
                                            map.put("imageuri", uri);
                                            map.put("reply", x);
                                            map.put("userid", CurrentUser);
                                            map.put("date", DateFormat.format("dd-MM-yyyy , hh:mm a", new Date().getTime()));


                                            firebaseFirestore.collection(c).document(postid).collection("comments")
                                                    .add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "comment added!", Toast.LENGTH_SHORT).show();
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



}