package com.mohammedsalah.learnhowtolearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class images_view extends AppCompatActivity {
    private TextView desc ;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    CircleImageView Imageliked;
    public DatabaseReference root;
    private String user_id;

    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_view);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        // put ads
        MobileAds.initialize(this,"ca-app-pub-5553020504531246~2909425441");
        adView = findViewById(R.id.add);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        Toolbar toolbar = findViewById(R.id.imagetoolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        ImageView imageView= findViewById(R.id.imag);
        TextView title = findViewById(R.id.titleText);
        desc = findViewById(R.id.descText);

        Imageliked = findViewById(R.id.like);



       // desc.setText(root);


        Intent intent = getIntent();

        String i = intent.getStringExtra("IMAGE");
        String ti = intent.getStringExtra("TITLE");
       // String ds = intent.getStringExtra("DESC");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.user); // put defalut profile image
        Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptions)
                .load(i)
                .into(imageView);
        title.setText(ti);
        //desc.setText(ds);

        if (firebaseAuth.getCurrentUser() != null){
            user_id = firebaseAuth.getCurrentUser().getUid();
        }

        Intent in = getIntent();
        final String Title = in.getStringExtra("TITLE");


        if (firebaseAuth.getCurrentUser()!=null) {

            firebaseFirestore.collection("users").document(user_id).
                    collection(Title + " Likes").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                        Imageliked.setImageDrawable(images_view.this.getDrawable(R.drawable.notifi));
                    } else {

                        Imageliked.setImageDrawable(images_view.this.getDrawable(R.drawable.not));
                    }
                }
            });
        }


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent inten = getIntent();
                String Id = inten.getStringExtra("Id");
                String tex = dataSnapshot.child(Id+ "_description").getValue(String.class);
                desc.setText(tex);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Some thing wrong!",Toast.LENGTH_SHORT).show();
            }
        };
        FirebaseDatabase.getInstance().getReference().addValueEventListener(postListener);

    }



    @Override
    protected void onStart() {
        super.onStart();
        // check intertnt connection
        InterntConections connectionInternet = new InterntConections(getApplicationContext());
        Boolean c = connectionInternet.IsConnectToInternt();
        if (!c){

            final AlertDialog.Builder alert = new AlertDialog.Builder(images_view.this);
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

            if (firebaseAuth.getCurrentUser()==null){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }


    }



    public void admin_image(View view) {
        openDialog();
        if (firebaseAuth.getCurrentUser()!=null) {
            firebaseFirestore.collection("users").document(user_id).
                    collection("Admin").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                        Intent intent = new Intent(getApplicationContext(),update_admin.class);
                        startActivity(intent);
                    }
                }
            });
        }


    }
    public void openDialog() {
        adminclass exampleDialog = new adminclass();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    public void Reviews(View view) {

        if (firebaseAuth.getCurrentUser() == null){
            final AlertDialog.Builder alert = new AlertDialog.Builder(images_view.this);
            alert.setMessage("Need To Create an account!");
            alert.setTitle("");
            alert.setPositiveButton("Create account", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alert.setCancelable(true);
                }
            });
            alert.create();
            alert.show();
        }else {

            // get intent
            Intent inten = getIntent();
            String Id = inten.getStringExtra("Id");


            Intent i = getIntent();
            String title = i.getStringExtra("TITLE");
            Toast.makeText(getApplicationContext(),title + " Community",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(),comunity.class);
            intent.putExtra("Id_post",Id);
            intent.putExtra("ti",title);
            startActivity(intent);

        }

    }

    public void like(final View view) {

        if (firebaseAuth.getCurrentUser() != null) {
            Imageliked.setImageDrawable(images_view.this.getDrawable(R.drawable.notifi));
            final String user_id = firebaseAuth.getCurrentUser().getUid();
            Intent intent = getIntent();
            final String title = intent.getStringExtra("TITLE");
            final Map<String, Object> map = new HashMap<>();
            map.put("like'sTime", FieldValue.serverTimestamp()); //time when user click like image
            firebaseFirestore.collection("users").document(user_id).
                    collection(title + " Likes").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (!task.getResult().exists()) {
                        firebaseFirestore.collection("users").document(user_id).
                                collection(title + " Likes").document(user_id).set(map);
                        Snackbar.make(view, "Follow " + title + " Community", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    } else {
                        firebaseFirestore.collection("users").document(user_id).
                                collection(title + " Likes").document(user_id).delete();
                        Imageliked.setImageDrawable(images_view.this.getDrawable(R.drawable.not));
                        Snackbar.make(view, "UnFollow " + title + " Community", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                    }
                }
            });

        }else {
            Toast.makeText(getApplicationContext(),"Creat account!",Toast.LENGTH_SHORT).show();
        }


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.translatemenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.trans:
                Intent in  = new Intent(getApplicationContext(),divinfo.class);
                startActivity(in);
                return true;

            case R.id.set:
                Intent i = new Intent(getApplicationContext(),settingoptions.class);
                startActivity(i);
                return true;

            case R.id.users:
                Intent t = new Intent(getApplicationContext(),allusers.class);
                startActivity(t);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void chat(View view) {

        if (firebaseAuth.getCurrentUser() == null){
            final AlertDialog.Builder alert = new AlertDialog.Builder(images_view.this);
            alert.setMessage("Need To Create an account!");
            alert.setTitle("");
            alert.setPositiveButton("Create account", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alert.setCancelable(true);
                }
            });
            alert.create();
            alert.show();
        }else {

            Intent i = getIntent();
            String title = i.getStringExtra("TITLE");
            Intent intent = new Intent(getApplicationContext(),chat.class);
            intent.putExtra("id",title);
            startActivity(intent);
        }


    }




}
