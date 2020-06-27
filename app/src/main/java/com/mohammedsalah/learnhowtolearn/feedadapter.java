package com.mohammedsalah.learnhowtolearn;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class feedadapter extends RecyclerView.Adapter<feedadapter.viewHolder> {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    List<feedclass> list;
    Context context;

    public feedadapter(List<feedclass> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feedback_item, viewGroup, false);
        context = viewGroup.getContext();
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, final int i) {

        feedclass c = list.get(i);
        viewHolder.date.setText(c.getDate());
        viewHolder.text.setText("\"" + c.getReply() + "\"");
        viewHolder.name.setText(c.getName());

        Glide.with(context).load(c.getImageuri()).into(viewHolder.imageView);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView name, date, text;
        CircleImageView imageView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.feed_user_name);
            date = itemView.findViewById(R.id.feed_date_text);
            text = itemView.findViewById(R.id.feedback_text);
            imageView = itemView.findViewById(R.id.feed_user_image);
        }
    }

}
