package com.mohammedsalah.learnhowtolearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class comunity extends AppCompatActivity {

    List<postsclass> list;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    HomeFragment reviewsfragment;
    BottomNavigationView bottomNavigationView;
    ImageView imageView , search;
    TextView tex ;
    public String Id;
    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comunity);



        Toolbar toolbar = findViewById(R.id.comnity_toolbar);
        setSupportActionBar (toolbar);
        setTitle("");


        reviewsfragment = new HomeFragment();
        bottomNavigationView=findViewById(R.id.bottomnavigationview);
        imageView = findViewById(R.id.imageneeds);
        search = findViewById(R.id.sear);
        tex = findViewById(R.id.textconutrev);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                Intent in = getIntent();
                String Id_search = in.getStringExtra("Id_post");

                Intent intent = new Intent(getApplicationContext(),Search.class);
                intent.putExtra("id",Id_search);
                startActivity(intent);

            }
        });


        list = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        Intent in = getIntent();
        Id = in.getStringExtra("Id_post");
        if (firebaseAuth.getCurrentUser() != null){
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
                            tex.setText(list.size()+" Reviews");

                        }

                    }





                }
            });
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // switch
                switch (menuItem.getItemId()) {
                    case R.id.reviewId:
                        imageView.setVisibility(View.INVISIBLE);
                        Intent inten = getIntent();
                        String Id = inten.getStringExtra("Id_post");
                        Bundle bundle = new Bundle();
                        bundle.putString("I_D", Id);
                        // set MyFragment Arguments
                        reviewsfragment.setArguments(bundle);
                        ReplaceFragmen(reviewsfragment);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    // add method to replace the clicked item with the fragment needed , we will pass the fragment as parameter

    public void ReplaceFragmen(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.maincontainer,fragment);
        fragmentTransaction.commit();
    }

    public void add_new_ask(View view) {

        Intent intent = new Intent(getApplicationContext(),comments.class);
        Intent inten = getIntent();
        String Id = inten.getStringExtra("Id_post");
        intent.putExtra("id",Id);
        startActivity(intent);

    }

    public void new_User_image(View view) {
        Intent intent = new Intent(getApplicationContext(),upload_user_image.class);
        startActivity(intent);
    }

    public void add_new_review(View view) {
        // get intent
        Intent inten = getIntent();
        String Id = inten.getStringExtra("Id_post");
        String tit = inten.getStringExtra("ti");

        Intent intent = new Intent(getApplicationContext(),postreview.class);
        intent.putExtra("id",Id);
        intent.putExtra("tt",tit);
        startActivity(intent);
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
