package com.mohammedsalah.learnhowtolearn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class upload_user_image extends AppCompatActivity {

    CircleImageView chooceImage;
    private static final int PICK_IMAGE = 1;
    private Uri imageuri = null;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private String user_id;
    private EditText username;
    private ProgressDialog dialog;

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
        setContentView(R.layout.activity_upload_user_image);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        dialog = new ProgressDialog(this);
        chooceImage=findViewById(R.id.addnewimage);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = firebaseAuth.getCurrentUser().getUid(); // get current user id
        chooceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(upload_user_image.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                        },1);

                        // hint message to user about need permissions
                        Toast.makeText(getApplicationContext(),"Need Permissions!",Toast.LENGTH_SHORT).show();
                    }else {
                        // make function setImage now
                        setImage();
                    }
                }else {
                    // make function setImage now
                    setImage();
                }
            }
        });


        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        // check if data is exist
                        if (task.getResult().exists()) {
                            // get data from data base
                            String image = task.getResult().getString("image");
                            String nameText = task.getResult().getString("name");

                            username.setText(nameText);
                            // to get images we should use Glide library or Picasso
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.placeholder(R.mipmap.prof); // put defalut profile image
                            Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptions).load(image).into(chooceImage);

                        }
                    }
                }
            });
        }
        username = findViewById(R.id.edit_name);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode==RESULT_OK){
            // need an uri for images
            imageuri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageuri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            chooceImage.setImageBitmap(bitmap);
        }
    }

    // setImage methode
    public void setImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select image") , PICK_IMAGE);
    }

    public void upload_image(View view) {
        final String name = username.getText().toString().trim();

        if (!TextUtils.isEmpty(name) &&imageuri!=null) {
            dialog.setMessage("upload..");
            dialog.setCanceledOnTouchOutside(false);
            dialog.create();
            dialog.show();
            user_id = firebaseAuth.getCurrentUser().getUid(); // get current user id

            StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");

            image_path.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Uri uri_download = task.getResult().getDownloadUrl();
                        // creat a map object to upload our data using it
                        Map<String, Object> map = new HashMap<>();
                        map.put("image", uri_download.toString());
                        map.put("name",name);
                        map.put("id",user_id);
                        // upload now
                       final MediaPlayer mp = MediaPlayer.create(getApplicationContext(),R.raw.notif);
                        firebaseFirestore.collection("users").document(user_id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.cancel();
                                    mp.start();
                                    Toast.makeText(getApplicationContext(),"UPLOADED SUCCESSFUL!",Toast.LENGTH_SHORT).show();

                                    if (firebaseAuth.getCurrentUser() != null) {
                                        String user_id = firebaseAuth.getCurrentUser().getUid(); // get current user id
                                        firebaseFirestore.collection("online_users_counter").document(user_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "logged out", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        Map<String, Object> mp = new HashMap<>();
                                        mp.put("status_id", "");
                                        firebaseFirestore.collection("users").document(user_id).update(mp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "status updated!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        Thread thread = new Thread() {
                                            @Override
                                            public void run() {
                                                try {
                                                    sleep(3000);
                                                    firebaseAuth.signOut();
                                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                    startActivity(i);
                                                    finish();

                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                super.run();
                                            }
                                        };

                                        thread.start();

                                    }

                                    } else {
                                    dialog.cancel();
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else
            Toast.makeText(getApplicationContext(),"Choose Image",Toast.LENGTH_SHORT).show();
    }


}
