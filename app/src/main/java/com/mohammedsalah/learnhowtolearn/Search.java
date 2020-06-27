package com.mohammedsalah.learnhowtolearn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {
    RecyclerView recyclerView;
    List<postsclass> list;
    List <category_class> cat;
    postsadapter adapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    EditText text_search;
    ImageView image_search;


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
        setContentView(R.layout.activity_search);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Toolbar toolbar = findViewById(R.id.searchtoolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        list = new ArrayList<>();
        cat = new ArrayList<>();
        recyclerView = findViewById(R.id.rc_search);
        adapter = new postsadapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        firebaseAuth = FirebaseAuth.getInstance();
        text_search = findViewById(R.id.textsearch);
        image_search = findViewById(R.id.search_btn);
        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");




        image_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                recyclerView.setVisibility(View.INVISIBLE);
                if(!TextUtils.isEmpty(text_search.getText().toString())) {
                    Toast.makeText(Search.this, "Search...", Toast.LENGTH_SHORT).show();

                    if (firebaseAuth.getCurrentUser() != null) {
                        firebaseFirestore = FirebaseFirestore.getInstance();
                        Query query = firebaseFirestore.
                                collection(id)
                                .orderBy("date", Query.Direction.DESCENDING); // arrange posts from new to old
                        query.addSnapshotListener(Search.this, new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                        String revId = documentChange.getDocument().getId();
                                        postsclass post = documentChange.getDocument().toObject(postsclass.class).withId(revId);

                                        if (post.getTitle().contains(text_search.getText().toString().trim()) ||
                                                post.getName().contains(text_search.getText().toString().trim()) ||
                                                post.getDate().contains(text_search.getText().toString().trim()) ||
                                                post.getTags().contains(text_search.getText().toString().trim()) ||
                                                post.getMin().contains(text_search.getText().toString().trim())) {
                                            list.add(post);
                                            adapter.notifyDataSetChanged();
                                        }

                                    }
                                }
                                recyclerView.setVisibility(View.VISIBLE);

                                if (list.size() > 0)
                                    Toast.makeText(Search.this, "" + list.size() + " Results", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(Search.this, "Not Found!", Toast.LENGTH_SHORT).show();

                                text_search.setText("");

                            }
                        });
                    }

                }
            }
        });



    }
}


