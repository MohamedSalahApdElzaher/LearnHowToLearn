package com.mohammedsalah.learnhowtolearn;


import android.app.AlertDialog;
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

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class categ_adapter extends RecyclerView.Adapter <categ_adapter.viewHolder> {

    private List<category_class> list;
    private Context context;
    private FirebaseAuth firebaseAuth;
    final static int n=0;


    public categ_adapter(List <category_class> list){
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item,viewGroup,false);
        context = viewGroup.getContext();
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, final int i) {

        final category_class currentitem = list.get(i);
        viewHolder.title.setText(currentitem.getTitle());
        viewHolder.imageView.setImageResource(currentitem.getImage_id());

        firebaseAuth = FirebaseAuth.getInstance();

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                if (firebaseAuth.getCurrentUser() != null || firebaseAuth.getCurrentUser()==null) {
                    category_class cat = list.get(i);
                    if (cat.getTitle().equals("Flutter")  || cat.getTitle().equals("C#")||
                    cat.getTitle().equals("Xamarin") || cat.getTitle().equals("IOS") || cat.getTitle().equals("Testing")
                    || cat.getTitle().equals("Cv & Resume")){
                        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setMessage("This Group not available right now\nif you have a good knowledge?\nplease join us through page messages!");
                        alert.setTitle("Group Info");
                        alert.setCancelable(false);

                        alert.setPositiveButton("settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, settingoptions.class);
                                context.startActivity(intent);
                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alert.setCancelable(true);
                            }
                        });
                        alert.create();
                        alert.show();
                    }else{
                        Intent intent = new Intent(context, images_view.class);
                        intent.putExtra("TITLE", cat.getTitle());
                        intent.putExtra("DESC", cat.getDesc());
                        intent.putExtra("IMAGE", cat.getImageurl());
                        intent.putExtra("Id", cat.getId());
                        context.startActivity(intent);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        private TextView title , desc ;
        private ImageView imageView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textcategory);
            imageView=itemView.findViewById(R.id.ctegory_image);
           desc = itemView.findViewById(R.id.descText);
        }
    }
}

