package com.mohammedsalah.learnhowtolearn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class publisher_posts extends AppCompatActivity {

    RecyclerView recyclerView;
    List<postsclass> list;
    List<category_class> cat;
    postsadapter adapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    TextView textView;
    ImageView imageView;


    AdView adView;
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
        setContentView(R.layout.activity_publisher_posts);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        MobileAds.initialize(this,"ca-app-pub-5553020504531246~2909425441");
        adView = findViewById(R.id.adVv);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        textView = findViewById(R.id.publisher_name);
        imageView = findViewById(R.id.publisher_image);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        String id = intent.getStringExtra("id");
        String im = intent.getStringExtra("imageuri");

        if (!im.isEmpty()) {
            Glide.with(getApplicationContext()).load(im).into(imageView);
        }
        textView.setText(name);

        Toast.makeText(getApplicationContext(),"" + id + "\n" + "\""+ name + "\"",Toast.LENGTH_LONG ).show();

        list = new ArrayList<>();
        cat = new ArrayList<>();
        recyclerView = findViewById(R.id.rc_publiser_posts);
        adapter = new postsadapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        firebaseAuth = FirebaseAuth.getInstance();


        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();
            Query query = firebaseFirestore.
                    collection(id)
                    .orderBy("date", Query.Direction.DESCENDING); // arrange posts from new to old
            query.addSnapshotListener(publisher_posts.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            String revId = documentChange.getDocument().getId();
                            postsclass post = documentChange.getDocument().toObject(postsclass.class).withId(revId);
                            // todo
                            if (name.equals(post.getName())){
                                list.add(post);
                                adapter.notifyDataSetChanged();
                            }

                        }

                    }
                }


            });
        }

    }
}






