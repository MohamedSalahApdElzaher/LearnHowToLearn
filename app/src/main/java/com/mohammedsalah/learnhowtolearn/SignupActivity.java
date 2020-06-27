package com.mohammedsalah.learnhowtolearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private EditText emailaddress, pass, confirmpass;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_signup);


        dialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        emailaddress = findViewById(R.id.edit_email);
        pass = findViewById(R.id.edit_pass);
        confirmpass = findViewById(R.id.edit_confpass);

    }

    public void Register(View view) {
        String email = emailaddress.getText().toString().trim();
        String password = pass.getText().toString().trim();
        String confirmpassword = confirmpass.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmpassword)) {

            if (!password.equals(confirmpassword))
                Toast.makeText(this, "Password not match!", Toast.LENGTH_SHORT).show();
            else {
                dialog.setMessage("Register..");
                dialog.setCanceledOnTouchOutside(false);
                dialog.create();
                dialog.show();

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.cancel();

                            AlertDialog.Builder alert = new AlertDialog.Builder(SignupActivity.this);
                            alert.setMessage("Successful Register!");
                            setTitle("");
                            alert.setCancelable(false);

                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), upload_user_image.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alert.create();
                            alert.show();

                        } else {
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }
    else

    {
        Toast.makeText(getApplicationContext(), "Fill All Texts", Toast.LENGTH_SHORT).show();
    }

}
    // login page
    public void log(View view) {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }
}
