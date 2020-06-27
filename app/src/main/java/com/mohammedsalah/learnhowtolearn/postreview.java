package com.mohammedsalah.learnhowtolearn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class postreview extends AppCompatActivity {

    private EditText postTitle, post_desc, min_no, tag;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;
    private String nameText;
    private Map<String, Object> map;
    private String user_id;
    int new_size, old_size;

    List<postsclass> list;

    private DatabaseReference root;


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
        setContentView(R.layout.activity_postreview);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        post_desc = findViewById(R.id.post_description);
        postTitle = findViewById(R.id.post_Title);
        min_no = findViewById(R.id.textmin);
        tag = findViewById(R.id.edit_tags);

        list = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);

        map = new HashMap<>();
        Intent intent = getIntent();
        final String ti = intent.getStringExtra("id");
        root = FirebaseDatabase.getInstance().getReference().child(ti + "_notification");

        getLis();


    }

    public void upload(View view) {
        // get intent
        Intent intent = getIntent();
        final String Id = intent.getStringExtra("id");

        final String tit = postTitle.getText().toString().trim();
        final String desc = post_desc.getText().toString().trim();
        final String min = min_no.getText().toString().trim();
        final String t = tag.getText().toString().trim();

        if (firebaseAuth.getCurrentUser() != null) {
            if (min.length() <= 2 && t.length() <= 30) {
                if (!TextUtils.isEmpty(tit) && !TextUtils.isEmpty(t) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(min)) {
                    Toast.makeText(getApplicationContext(), "Post...", Toast.LENGTH_LONG).show();

                    final String user_id = firebaseAuth.getCurrentUser().getUid();

                    firebaseFirestore.collection("users").document(user_id).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        // check if data is exist
                                        if (task.getResult().exists()) {
                                            nameText = task.getResult().getString("name");
                                            String uri = task.getResult().getString("image");

                                            map.put("name", nameText);
                                            map.put("userimage", uri);
                                            map.put("title", tit);
                                            map.put("description", desc);
                                            map.put("min", min);
                                            map.put("date", DateFormat.format(" EEEE , dd-MM-yyyy , hh:mm a", new Date().getTime()));
                                            map.put("tags", t);
                                            map.put("id", user_id);
                                            map.put("com", Id);

                                            final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.notif);
                                            firebaseFirestore.collection(Id).add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        mp.start();
                                                        root.push().setValue(" ");
                                                        Toast.makeText(getApplicationContext(), "Posted Successfull!", Toast.LENGTH_LONG).show();
                                                        Intent intent = new Intent(getApplicationContext(), category.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        } else {
                                            Toast.makeText(getApplicationContext(), "Fill Texts!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Fill Texts!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }else
                Toast.makeText(getApplicationContext(), "max 2 digits , 30 characters tag", Toast.LENGTH_SHORT).show();

        }
    }

    void noti() {
        Intent intent = getIntent();
        String Id = intent.getStringExtra("id");
        old_size = 0; // hold the current size in database
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();
            Query query = firebaseFirestore.
                    collection(Id)
                    .orderBy("date", Query.Direction.DESCENDING); // arrange posts from new to old
            query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            String revId = documentChange.getDocument().getId();
                            postsclass post = documentChange.getDocument().toObject(postsclass.class).withId(revId);
                            list.add(post);
                        }
                    }
                    new_size = list.size();
                }

            });
        }


        String id = intent.getStringExtra("tt");
        //  Toast.makeText(getApplicationContext(),id,Toast.LENGTH_LONG).show();

        if (firebaseAuth.getCurrentUser() != null) {

            user_id = firebaseAuth.getCurrentUser().getUid();
            final Map<String, Object> map = new HashMap<>();
            map.put("like'sTime", FieldValue.serverTimestamp()); //time when user click like image
            firebaseFirestore.collection("users").document(user_id).
                    collection(id + " Likes").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                        // push notifications
                        if (true) {

                            Calendar calendar = Calendar.getInstance();
                            Intent it = new Intent(getApplicationContext(), AlertReciver.class);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(getApplicationContext(), 0
                                    , it, PendingIntent.FLAG_UPDATE_CURRENT));

                        }

                    }
                }
            });
        }
    }

    void getLis(){

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                noti();
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                noti();
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
}
