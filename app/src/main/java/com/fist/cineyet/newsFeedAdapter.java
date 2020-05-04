package com.fist.cineyet;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class newsFeedAdapter extends RecyclerView.Adapter<newsFeedAdapter.ViewHolder>{
    private static final String TAG="newsFeedAdapter";
    ArrayList<newsfeedItems> mainModels;
    Context context;
    public newsFeedAdapter(Context context, ArrayList<newsfeedItems> mainModels){
        this.context=context;
        this.mainModels=mainModels;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.news_feed_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "on bind called on" + mainModels.get(position).postMovie);

        holder.imgView.setImageResource(mainModels.get(position).movieDrawable);
        holder.profilePic.setImageResource(mainModels.get(position).profilePic);
        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"clicked on image");
                Intent myIntent=new Intent(context,MoviePageActivity.class);

                context.startActivity(myIntent);
            }
        });
        holder.personName.setText(mainModels.get(position).postPersonName);
        holder.movieName.setText(mainModels.get(position).postMovie);
        holder.activity.setText(mainModels.get(position).postActivityType);
        holder.comment.setText(mainModels.get(position).postComment);
        holder.date.setText(mainModels.get(position).postTime);

    }

    @Override
    public int getItemCount() {
        return mainModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView;
        ImageView profilePic;
        TextView personName;
        TextView movieName;
        TextView activity;
        TextView comment;
        TextView date;
        ConstraintLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView=itemView.findViewById(R.id.news_movie_poster);
            profilePic=itemView.findViewById(R.id.news_profile_image);
             personName=itemView.findViewById(R.id.news_profile_name);;
             movieName=itemView.findViewById(R.id.news_movie_name);;
             activity=itemView.findViewById(R.id.news_activity_type);;
             comment=itemView.findViewById(R.id.news_snippet);;
             date=itemView.findViewById(R.id.news_time);;
            parentLayout=itemView.findViewById(R.id.parent_news);
        }
    }
}
