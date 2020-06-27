package com.mohammedsalah.learnhowtolearn;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class review_details extends AppCompatActivity {
    TextView name , title , description , date  , time , tag;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    CircleImageView imageView;
    AdView adView;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_details);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        MobileAds.initialize(this,"ca-app-pub-5553020504531246~2909425441");
        adView = findViewById(R.id.adV);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // check intertnt connection
        InterntConections connectionInternet = new InterntConections(getApplicationContext());
        Boolean c = connectionInternet.IsConnectToInternt();
        if (!c){
            final AlertDialog.Builder alert = new AlertDialog.Builder(review_details.this);
            alert.setMessage("\" No Internet Connection \"");
            alert.setTitle("Network Error!");
            alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alert.setCancelable(true);
                }
            });
            alert.create();
            alert.show();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("");
        progressDialog.create();
        progressDialog.show();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        imageView = findViewById(R.id.rev_detail_image);
        name = findViewById(R.id.username_detail);
        description = findViewById(R.id.desc_details);
        title = findViewById(R.id.title_details);
        date = findViewById(R.id.date_details);
        time = findViewById(R.id.min_details);
        tag = findViewById(R.id.detai_tag);



        Intent intent = getIntent();
        String tit = intent.getStringExtra("T");
        String des = intent.getStringExtra("D");
        String mm = intent.getStringExtra("M");
        String dat = intent.getStringExtra("DA");
        String n = intent.getStringExtra("N");
        String im = intent.getStringExtra("I");
        String t = intent.getStringExtra("TAG");


        title.setText(tit);
        description.setText(des);
        date.setText(dat);
        time.setText( mm + " min read");
        name.setText("\""+n+"\"");
        tag.setText("#"+t);

        if (!im.isEmpty()) {
            Glide.with(getApplicationContext()).load(im).into(imageView);
            progressDialog.cancel();
        }

    }

    public void publisher_posts(View view) {
        // get name
        Intent intent = getIntent();
        String name = intent.getStringExtra("N");
        String id = intent.getStringExtra("id");
        String im = intent.getStringExtra("I");


        Intent i = new Intent(getApplicationContext(),publisher_posts.class);
        i.putExtra("id",id);
        i.putExtra("name",name);
        i.putExtra("imageuri",im);
        startActivity(i);
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


