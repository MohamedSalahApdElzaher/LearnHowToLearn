package com.mohammedsalah.learnhowtolearn;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class comments_adapter extends RecyclerView.Adapter<comments_adapter.viewHolder> {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    List<comments_class> list;
    Context context;

    public comments_adapter(List<comments_class> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.commentslistitem, viewGroup, false);
        context = viewGroup.getContext();
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, final int i) {
        comments_class c = list.get(i);
        viewHolder.date.setText(c.getDate());
        viewHolder.text.setText("\""+c.getAsk()+"\"");

        final String postId = list.get(i).postId;

        final String user_id = list.get(i).getId();
        final String current_Id=firebaseAuth.getCurrentUser().getUid();
          final ProgressDialog progressDialog = new ProgressDialog(context);


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current_Id.equals(user_id)){ // that mean the post is belong to who logged in or admin

                    final comments_class p = list.get(i);
                    final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Are You Sure to delete this Question ?");

                    alert.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.setMessage("Deleting question");
                            progressDialog.create();
                            progressDialog.show();

                            if (firebaseAuth.getCurrentUser() != null) {
                                firebaseFirestore.collection(p.getComm_post()+"_comments").document(postId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        try{ // Handel an Exception Error
                                            list.remove(i);
                                            progressDialog.cancel();
                                            Intent intent = new Intent(context,category.class); // as refresh
                                            context.startActivity(intent);
                                            Toast.makeText(context,"deleted Successful!",Toast.LENGTH_SHORT).show();
                                        }catch (Exception e){
                                            Toast.makeText(context,"Error :" + e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            }

                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alert.setCancelable(true);
                        }
                    });

                    alert.create();
                    alert.show();


                }else {
                    Toast.makeText(context,"Try again",Toast.LENGTH_SHORT).show();
                }
            }
        });

        String uri = list.get(i).getUserimage();
        String tex_name = list.get(i).getNameuser();
        viewHolder.name.setText(tex_name);
        Glide.with(context).load(uri).into(viewHolder.imageView);

        viewHolder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comments_class c = list.get(i);
                Intent intent = new Intent(context, replycomments.class);
                intent.putExtra("postId",postId);
                intent.putExtra("c",c.getComm_post());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView name, date, text;
        CircleImageView imageView ;
        ImageView reply;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_comment_name);
            date = itemView.findViewById(R.id.date_text_ask);
            text = itemView.findViewById(R.id.ask_text);
            imageView = itemView.findViewById(R.id.user_comment_image);
            reply = itemView.findViewById(R.id.replay);
        }
    }
}