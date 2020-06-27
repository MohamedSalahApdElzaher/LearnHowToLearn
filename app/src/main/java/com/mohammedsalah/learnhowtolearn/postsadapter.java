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
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class postsadapter extends RecyclerView.Adapter <postsadapter.viewHolder> {

    List<postsclass> list;
    List<category_class> cat;
    Context context;
    private String n;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    StorageReference storageReference;
    private String current_id;
    private DatabaseReference root;
    private DatabaseReference root_count;
    private ProgressDialog progressDialog;

    public postsadapter(List<postsclass> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reviews_listitem, viewGroup, false);
        context = viewGroup.getContext();
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, final int i) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        cat = new ArrayList<>();


        String t = list.get(i).getTitle();
        String d = list.get(i).getDate();
        String m = list.get(i).getMin();
        String tag = list.get(i).getTags();

        final String postId = list.get(i).rev_id;
        root_count = FirebaseDatabase.getInstance().getReference().child(postId+ "_count_seen_rev");

        final String user_id = list.get(i).getId();
        final String current_Id=firebaseAuth.getCurrentUser().getUid();

        if (current_Id.equals(user_id)){ // that mean the post is belong to who logged in or admin
            viewHolder.deletepost.setVisibility(View.VISIBLE);
        }

        // add notification here


        if (firebaseAuth.getCurrentUser()!=null) {
            firebaseFirestore.collection("users").document(current_Id).
                    collection("Admin").document(current_Id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                        viewHolder.deletepost.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        progressDialog = new ProgressDialog(context);
        viewHolder.deletepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final postsclass p = list.get(i);
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("Are You Sure to delete this Review ?");

                alert.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setMessage("Deleting Review");
                        progressDialog.create();
                        progressDialog.show();

                        if (firebaseAuth.getCurrentUser() != null) {
                            firebaseFirestore.collection(p.getCom()).document(postId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    root = FirebaseDatabase.getInstance().getReference().child(p.getCom() + "_notification");
                                    root.removeValue();
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

            }
        });


        viewHolder.titelText.setText(t);
        viewHolder.date.setText(d);
        viewHolder.min.setText(m + " min read");
        viewHolder.tags.setText("#" + tag);

        String uri = list.get(i).getUserimage();
        Glide.with(context).load(uri).into(viewHolder.imageView);



        // read number from database and increment it then update again




        final String revId = list.get(i).rev_id; // rev id
        if (firebaseAuth.getCurrentUser() != null) {
            current_id = firebaseAuth.getCurrentUser().getUid(); // user id
        }

        // get all rec in real time
        firebaseFirestore.collection("android").document(revId).collection("recs").document(current_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    viewHolder.rec.setImageDrawable(context.getDrawable(R.drawable.reced));
                } else {
                    viewHolder.rec.setImageDrawable(context.getDrawable(R.drawable.rec));
                }
            }
        });

        // get all rec counters

        firebaseFirestore.collection("android").document(revId).collection("recs").
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            int con = queryDocumentSnapshots.size();
                            viewHolder.recount.setText("" + con);
                        } else {
                            viewHolder.recount.setText("" + 0);
                        }
                    }
                });

        viewHolder.rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View vi) {
                viewHolder.rec.setImageDrawable(context.getDrawable(R.drawable.reced));
                firebaseFirestore.collection("android").document(revId).collection("recs").document(current_id)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            Snackbar.make(vi, "Your recommendation was added!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                            Map<String, Object> map = new HashMap<>();
                            map.put("like'sTime", FieldValue.serverTimestamp()); //time when user click like image
                            firebaseFirestore.collection("android").document(revId).collection("recs").document(current_id)
                                    .set(map);
                        } else {
                            firebaseFirestore.collection("android").document(revId).collection("recs").document(current_id)
                                    .delete();
                            Snackbar.make(vi, "recommendation deleted", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        }
                    }
                });

            }
        });

        // Likes Features

        // get all like in real time
        firebaseFirestore.collection("android").document(revId).collection("likes").document(current_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    viewHolder.like.setImageDrawable(context.getDrawable(R.drawable.liked));
                } else {
                    viewHolder.like.setImageDrawable(context.getDrawable(R.drawable.like));
                }
            }
        });

        // get all rec counters

        firebaseFirestore.collection("android").document(revId).collection("likes").
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            int con = queryDocumentSnapshots.size();
                            viewHolder.likecount.setText("" + con);
                        } else {
                            viewHolder.likecount.setText("" + 0);
                        }
                    }
                });

        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View vi) {
                viewHolder.like.setImageDrawable(context.getDrawable(R.drawable.liked));
                firebaseFirestore.collection("android").document(revId).collection("likes").document(current_id)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            Snackbar.make(vi, "like this review!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                            Map<String, Object> map = new HashMap<>();
                            map.put("like'sTime", FieldValue.serverTimestamp()); //time when user click like image
                            firebaseFirestore.collection("android").document(revId).collection("likes").document(current_id)
                                    .set(map);
                        } else {
                            firebaseFirestore.collection("android").document(revId).collection("likes").document(current_id)
                                    .delete();
                            Snackbar.make(vi, "dislike this review!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        }
                    }
                });

            }
        });


        // read seen
        firebaseFirestore.collection("android").document(revId).collection("seen").
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            int con = queryDocumentSnapshots.size();
                            viewHolder.count_seen_review.setText("" + con);
                        } else {
                            viewHolder.count_seen_review.setText("" + 0);
                        }
                    }
                });

        viewHolder.seen_rv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get usreers
                // read seen
                firebaseFirestore.collection("android").document(revId).collection("seen").
                        addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    int con = queryDocumentSnapshots.size();
                                    viewHolder.count_seen_review.setText("" + con);
                                } else {
                                    viewHolder.count_seen_review.setText("" + 0);
                                }
                            }
                        });
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                postsclass post = list.get(i);


                firebaseFirestore.collection("android").document(revId).collection("seen").document(current_id)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("seen'sTime", FieldValue.serverTimestamp()); //time when user click like image
                            firebaseFirestore.collection("android").document(revId).collection("seen").document(current_id)
                                    .set(map);
                        }
                    }
                });


                Intent intent = new Intent(context, review_details.class);
                intent.putExtra("T", post.getTitle());
                intent.putExtra("D", post.getDescription());
                intent.putExtra("M", post.getMin());
                intent.putExtra("L", post.getLocation());
                intent.putExtra("DA", post.getDate());
                intent.putExtra("I", post.getUserimage());
                intent.putExtra("TAG", post.getTags());
                intent.putExtra("id",post.getCom());
                intent.putExtra("N",post.getName());
                context.startActivity(intent);


            }

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView titelText , date , min , loc , tags , likecount , recount , tex,count_seen_review ;
        CircleImageView imageView;
        ImageView rec , like , deletepost , seen_rv ;


        public viewHolder(@NonNull View itemView) {
            super(itemView);

            titelText = itemView.findViewById(R.id.text_title_reviws);
            date = itemView.findViewById(R.id.text_date_reviws);
            min= itemView.findViewById(R.id.text_min_reviws);
            imageView = itemView.findViewById(R.id.user_image_reviws);
            tags = itemView.findViewById(R.id.tags_text);
            rec = itemView.findViewById(R.id.rec_rev);
            like = itemView.findViewById(R.id.like_rev);
            recount = itemView.findViewById(R.id.reccount);
            likecount = itemView.findViewById(R.id.likecount);
            tex = itemView.findViewById(R.id.textconutrev);
            deletepost = itemView.findViewById(R.id.delete_post);
            count_seen_review = itemView.findViewById(R.id.count_seen_rev);
            seen_rv = itemView.findViewById(R.id.seen_rev);

        }

    }
}

