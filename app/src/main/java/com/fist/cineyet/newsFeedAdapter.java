package com.fist.cineyet;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class newsFeedAdapter extends RecyclerView.Adapter<newsFeedAdapter.ViewHolder>{
    /* Firebase variables */
    private DatabaseReference userRef;
    FirebaseAuth myFirebaseAuth;
    String currentUserID;


    private static final String TAG="newsFeedAdapter";
    ArrayList<newsfeedItems> mainModels;
    Context context;
    public newsFeedAdapter(Context context, ArrayList<newsfeedItems> mainModels){
        this.context = context;
        this.mainModels = mainModels;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.news_feed_item,parent,false);

        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = myFirebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "on bind called on" + mainModels.get(position).postMovie);

        holder.imgView.setImageResource(mainModels.get(position).movieDrawable);
        //holder.profile_img.setImageResource(mainModels.get(position).profilePic);
        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"clicked on image");


                Intent myIntent = new Intent(context,MoviePageActivity.class);

                context.startActivity(myIntent);
            }
        });

        //holder.personName.setText(mainModels.get(position).postPersonName);
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
        TextView personName, movieName, activity, comment, date;
        ConstraintLayout parentLayout;

        CircleImageView profile_img;
        String personNewName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.news_movie_poster);
            //profilePic = itemView.findViewById(R.id.news_profile_image);
            profile_img = itemView.findViewById(R.id.news_profile_image);
            personName = itemView.findViewById(R.id.news_profile_name);;
            personNewName = personName.getText().toString();
            movieName = itemView.findViewById(R.id.news_movie_name);;
            activity = itemView.findViewById(R.id.news_activity_type);;
            comment = itemView.findViewById(R.id.news_snippet);;
            date = itemView.findViewById(R.id.news_time);;
            parentLayout = itemView.findViewById(R.id.parent_news);

            /* Setting user profile pic and name */
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        String image_string = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image_string).placeholder(R.drawable.ic_account_circle_black_24dp).into(profile_img);

                        personNewName = dataSnapshot.child("name").getValue().toString();
                        personName.setText(personNewName);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
