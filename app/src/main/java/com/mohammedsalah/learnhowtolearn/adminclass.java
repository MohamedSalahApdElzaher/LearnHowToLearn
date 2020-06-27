package com.mohammedsalah.learnhowtolearn;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class adminclass extends AppCompatDialogFragment {
    private EditText editcode;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.admin_dialog, null);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        builder.setView(view)
                .setTitle("Confirm Admin Code")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String code = editcode.getText().toString();
                        if(code.equals("mo**##sala7")){
                            if (firebaseAuth.getCurrentUser() != null) {
                                final String user_id = firebaseAuth.getCurrentUser().getUid();
                                final Map<String, Object> map = new HashMap<>();
                                map.put("Admin'sTime", FieldValue.serverTimestamp()); //time when user click like image
                                firebaseFirestore.collection("users").document(user_id).
                                        collection("Admin").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (!task.getResult().exists()) {
                                            firebaseFirestore.collection("users").document(user_id).
                                                    collection("Admin").document(user_id).set(map);
                                            //Toast.makeText(getContext(),"Set ADMIN",Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getContext(),"Not ADMIN",Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });

                            }

                        }else{
                            Toast.makeText(getContext(),"Invalid Code",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        editcode = view.findViewById(R.id.editcode);

        return builder.create();
    }


}