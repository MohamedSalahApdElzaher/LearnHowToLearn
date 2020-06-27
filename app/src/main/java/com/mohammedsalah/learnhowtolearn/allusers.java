package com.mohammedsalah.learnhowtolearn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class allusers extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore ;
    FirebaseAuth firebaseAuth;

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
        setContentView(R.layout.activity_allusers);


        firebaseAuth = FirebaseAuth.getInstance();
        Toast.makeText(this,"All Users",Toast.LENGTH_SHORT).show();
        final List<usersclass> list = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.rv_users);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        final alluseradapter adapter = new alluseradapter(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if (firebaseAuth.getCurrentUser() != null){
            firebaseFirestore = FirebaseFirestore.getInstance();
            Query query = firebaseFirestore.collection("users");
            query.addSnapshotListener(allusers.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(final QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            usersclass users = documentChange.getDocument().toObject(usersclass.class);
                                list.add(users);
                                adapter.notifyDataSetChanged();

                        }
                    }
                }
            });
        }

    }

    public void onlineusers(View view) {
        Intent intent= new Intent(getApplicationContext(),users.class);
        startActivity(intent);
    }
}
