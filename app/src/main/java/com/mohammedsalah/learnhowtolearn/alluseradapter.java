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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class alluseradapter extends RecyclerView.Adapter <alluseradapter.userViewHolder>{


    FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
    private Context context;
    List<usersclass> list ;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public alluseradapter (List <usersclass> list){
        this.list = list;
    }

    @NonNull
    @Override
    public alluseradapter.userViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_user,viewGroup,false);
        context =  viewGroup.getContext();
        return new alluseradapter.userViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final alluseradapter.userViewHolder userViewHolder, final int i) {

        final String name = list.get(i).getName();
        final String image = list.get(i).getImage();

        userViewHolder.Setusers(name,image);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class userViewHolder extends RecyclerView.ViewHolder{

        TextView name ;
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


    }
}

