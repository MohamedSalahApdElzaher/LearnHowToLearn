package com.mohammedsalah.learnhowtolearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class users extends AppCompatActivity {
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
        setContentView(R.layout.activity_users);

        firebaseAuth = FirebaseAuth.getInstance();
        Toast.makeText(this,"Online Users",Toast.LENGTH_SHORT).show();
        final List<usersclass> list = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.rv_users);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        final useradapter adapter = new useradapter(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);



        if (firebaseAuth.getCurrentUser() != null){
            firebaseFirestore = FirebaseFirestore.getInstance();
            Query query = firebaseFirestore.collection("users");
            query.addSnapshotListener(users.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(final QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                             usersclass users = documentChange.getDocument().toObject(usersclass.class);
                            String st = users.getStatus_id();
                             if (st!=null && !st.isEmpty()) {
                               list.add(users);
                               adapter.notifyDataSetChanged();
                           }

                        }
                    }
                }
            });
        }


    }



}
