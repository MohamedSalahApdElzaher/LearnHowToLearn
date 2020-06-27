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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class feedback extends AppCompatActivity {
    ImageView postbtn , feedback_image;
    EditText editText;
    String CurrentUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ImageView post;
    TextView textView ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        postbtn = findViewById(R.id.post_feed);
        editText = findViewById(R.id.Edit_fees);
        CurrentUser = firebaseAuth.getCurrentUser().getUid();
        feedback_image = findViewById(R.id.feedback_image);
        textView = findViewById(R.id.count_feedback);

        feedback_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View vi) {
                firebaseFirestore.collection("feedback").document(CurrentUser)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            Snackbar.make(vi, "Like App", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                            Map<String, Object> map = new HashMap<>();
                            map.put("like'sTime", FieldValue.serverTimestamp()); //time when user click like image
                            firebaseFirestore.collection("feedback").document(CurrentUser)
                                    .set(map);
                            feedback_image.setImageDrawable(getApplicationContext().getDrawable(R.drawable.liked));

                        } else {
                            firebaseFirestore.collection("feedback").document(CurrentUser)
                                    .delete();
                            Snackbar.make(vi, "dislike App", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                            feedback_image.setImageDrawable(getApplicationContext().getDrawable(R.drawable.like));

                        }
                    }
                });

            }
        });

        firebaseFirestore.collection("feedback").document(CurrentUser)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    feedback_image.setImageDrawable(getApplicationContext().getDrawable(R.drawable.liked));

                } else {
                    feedback_image.setImageDrawable(getApplicationContext().getDrawable(R.drawable.like));

                }
            }
        });



        // read seen
        final int[] size = {0};
        firebaseFirestore.collection("feedback").
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                             int con = queryDocumentSnapshots.size();
                              textView.setText("Total " + con + " of users like this" );
                              size[0] = con;
                        }else {
                            textView.setText( "Total "+ (size[0] -1) + " of users like this" );
                        }
                    }
                });


        final List<feedclass> list = new ArrayList<>();
        post = findViewById(R.id.post_feed);

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


        RecyclerView recyclerView = findViewById(R.id.rv_feedback);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        final feedadapter adapter = new feedadapter(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Reterive feedbacks

        firebaseFirestore.collection("feedbacks")
                .addSnapshotListener(feedback.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            feedclass reply = documentChange.getDocument().toObject(feedclass.class);
                            list.add(reply);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

        // post feedbacks

        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                if (firebaseAuth.getCurrentUser() != null) {

                    Toast.makeText(getApplicationContext(), "post...", Toast.LENGTH_LONG).show();

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


                                            firebaseFirestore.collection("feedbacks")
                                                    .add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "thanks", Toast.LENGTH_SHORT).show();
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
