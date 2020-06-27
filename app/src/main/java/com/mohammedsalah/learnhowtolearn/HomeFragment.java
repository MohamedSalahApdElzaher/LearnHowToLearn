package com.mohammedsalah.learnhowtolearn;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    List<postsclass> list;
    postsadapter adapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    TextView textView;
    AdView adView;
    public HomeFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MobileAds.initialize(container.getContext(),"ca-app-pub-5553020504531246~2909425441");
        adView = view.findViewById(R.id.av);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rc_review);
        adapter = new postsadapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(adapter);
        firebaseAuth = FirebaseAuth.getInstance();

        textView = view.findViewById(R.id.no_rev);


        // check intertnt connection
        InterntConections connectionInternet = new InterntConections(container.getContext());
        Boolean c = connectionInternet.IsConnectToInternt();
        if (!c){
            final AlertDialog.Builder alert = new AlertDialog.Builder(container.getContext());
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


        String Id = null;
        if (getArguments() != null) {
            Id = getArguments().getString("I_D");
        }


        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();
            Query query = firebaseFirestore.
                    collection(Id)
                    .orderBy("date", Query.Direction.DESCENDING); // arrange posts from new to old
            query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            String revId = documentChange.getDocument().getId();
                            postsclass post = documentChange.getDocument().toObject(postsclass.class).withId(revId);
                            list.add(post);
                            adapter.notifyDataSetChanged();
                        }
                    }
                  if (list.size() != 0){
                      Snackbar.make(container,  "Wait Loading...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                      recyclerView.setVisibility(View.VISIBLE);
                  }else {
                     textView.setVisibility(View.VISIBLE);
                  }

                }
            });
        }
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser()==null){
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }



}
