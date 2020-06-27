package com.mohammedsalah.learnhowtolearn;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class useradapter extends RecyclerView.Adapter <useradapter.userViewHolder> {

    FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
    private Context context;
    List<usersclass> list ;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public useradapter (List <usersclass> list){
        this.list = list;
    }

    @NonNull
    @Override
    public userViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_list_items,viewGroup,false);
        context =  viewGroup.getContext();
        return new userViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final userViewHolder userViewHolder, final int i) {

        final String name = list.get(i).getName();
        final String image = list.get(i).getImage();

        userViewHolder.Setusers(name,image);
        userViewHolder.setStats("online");


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class userViewHolder extends RecyclerView.ViewHolder{

        TextView name , stats;
        ImageView imageView;
        View view;
        public userViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void Setusers(String text , String uri  ){
            name = view.findViewById(R.id.username);
            imageView =view.findViewById(R.id.imageview);
            Glide.with(context).load(uri).into(imageView);
            name.setText(text);

        }

        public void setStats(String st){
            stats =view.findViewById(R.id.status);
            stats.setText(st);
        }


    }
}
