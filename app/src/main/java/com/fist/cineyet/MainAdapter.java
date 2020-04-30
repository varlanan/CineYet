package com.fist.cineyet;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>{
    private static final String TAG="MainAdapter";
    ArrayList<Integer> mainModels;
    Context context;
    public MainAdapter(Context context, ArrayList<Integer> mainModels){
            this.context=context;
            this.mainModels=mainModels;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "on bind called on" + mainModels.get(position));
        holder.imgView.setImageResource(mainModels.get(position));
        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"clicked on image");
                Intent myIntent=new Intent(context,MoviePageActivity.class);

                context.startActivity(myIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mainModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView;
        ConstraintLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView=itemView.findViewById(R.id.row_item_image);
            parentLayout=itemView.findViewById(R.id.parent_layout);
        }
    }
}
