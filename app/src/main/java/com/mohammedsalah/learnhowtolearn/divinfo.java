package com.mohammedsalah.learnhowtolearn;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class divinfo extends AppCompatActivity {
    CircleImageView chooceImage;
    FirebaseFirestore firebaseFirestore;
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
        setContentView(R.layout.activity_divinfo);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        TextView textView = findViewById(R.id.textntitle);
        textView.setText("\" فضلا ادعي لوالداى بالرحمه والسكينه \"");
        chooceImage = findViewById(R.id.p);
        Glide.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/guideme-72974.appspot.com/o/IMG_20190213_183516.jpg?alt=media&token=678616b8-83aa-40a9-b251-cd233b3b48f4").into(chooceImage);

    }

    public void youtube(View view) {
        Intent intent = new Intent(getApplicationContext(), contacts_dev.class);
        startActivity(intent);
    }

    public void linled(View view) {
        Intent intent = new Intent(getApplicationContext(), likedin.class);
        startActivity(intent);
    }

    public void github(View view) {
        Intent intent = new Intent(getApplicationContext(), github.class);
        startActivity(intent);
    }

    public void facebook(View view) {
        Intent intent = new Intent(getApplicationContext(), face.class);
        startActivity(intent);
    }

    public void gmail(View view) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(divinfo.this);
        alert.setMessage("ms01010103727@gmail.com");
        setTitle("Email Info");
        alert.setCancelable(false);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.setCancelable(true);
            }
        });

        alert.create();
        alert.show();

    }
}
