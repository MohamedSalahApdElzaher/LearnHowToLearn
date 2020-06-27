package com.mohammedsalah.learnhowtolearn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class cont extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (firebaseAuth.getCurrentUser() != null) {
            String user_id = firebaseAuth.getCurrentUser().getUid(); // get current user id
            // todo
            firebaseFirestore.collection("online_users_counter").document(user_id).delete();
            firebaseAuth.signOut();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cont);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        WebView w = findViewById(R.id.web);
        w.loadUrl("https://www.facebook.com/Learn-how-to-Learn-103991261177216/");
    }
}
