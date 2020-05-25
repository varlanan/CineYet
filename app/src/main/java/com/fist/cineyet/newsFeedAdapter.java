package com.fist.cineyet;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class newsFeedAdapter extends RecyclerView.Adapter<newsFeedAdapter.ViewHolder>{
    /* Firebase variables */
    private DatabaseReference userRef, reviewLikesRef;
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
        reviewLikesRef = FirebaseDatabase.getInstance().getReference().child("ReviewLikes");

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "newsfeed on bind on " + mainModels.get(position).postMovie);

        String image_string = mainModels.get(position).movieDrawable;
        Picasso.get().load(image_string).placeholder(R.drawable.ic_account_circle_black_24dp).into(holder.imgView);

        image_string = mainModels.get(position).profilePic;
        Picasso.get().load(image_string).placeholder(R.drawable.ic_account_circle_black_24dp).into(holder.profile_img);
//        holder.imgView.setImageResource(mainModels.get(position).movieDrawable);
        //holder.profile_img.setImageResource(mainModels.get(position).profilePic);
        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"clicked on image");


                Intent myIntent = new Intent(context,MoviePageActivity.class);

                context.startActivity(myIntent);
            }
        });

        String person_name = mainModels.get(position).postPersonName;
        String[] name_split = person_name.split(" ");
        String uppercase_name = name_split[0].substring(0, 1).toUpperCase() + name_split[0].substring(1).toLowerCase() + " "
                +  name_split[1].substring(0, 1).toUpperCase() + name_split[1].substring(1).toLowerCase();
        holder.personName.setText(uppercase_name);

        holder.movieName.setText(mainModels.get(position).postMovie);
        holder.activity.setText(mainModels.get(position).postActivityType);
        holder.comment.setText(mainModels.get(position).postComment);
        holder.date.setText(mainModels.get(position).postTime);
        if(mainModels.get(position).movieRating != null){
            holder.rating.setRating(Float.parseFloat(mainModels.get(position).movieRating));
        }

        reviewLikesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if( dataSnapshot.hasChild(mainModels.get(position).reviewID) ){
//                    DataSnapshot all_user_likes = dataSnapshot.child(mainModels.get(position).reviewID);
                    Integer num_likes = 0;
                    if(dataSnapshot.child(mainModels.get(position).reviewID).hasChild(currentUserID)){
                        String temp = dataSnapshot.child(mainModels.get(position).reviewID).child(currentUserID).child("liked").getValue().toString();
                        if(temp.equals("true")){
                            holder.likeB.setText("Unlike");
                        }
                        else {
                            holder.likeB.setText("Like");
                        }
                    }
                    for( DataSnapshot snapshot_userID : dataSnapshot.child(mainModels.get(position).reviewID).getChildren() ){
                        String temp = snapshot_userID.child("liked").getValue().toString();
                        if(temp.equals("true")){
                            num_likes++;
                        }

                    }
//                    Query all_true_likes = reviewLikesRef.child(mainModels.get(position).reviewID).orderByChild("liked").equalTo("true");
//                    Long num_likes = all_user_likes.getChildrenCount();
                    if(num_likes == 1){
                        holder.numLikes.setText("1 like");
                    }
                    else {
                        holder.numLikes.setText(num_likes + " likes");
                    }
                }
                else{
                    holder.numLikes.setText("0 likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.likeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewLikesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        /* reviewID already in the list - may or may not have stored likes in it */
                        if( dataSnapshot.hasChild(mainModels.get(position).reviewID) ){
                            /* OLD MODEL: is the userID exists, it means that they already liked the post, and now wish to unlike it - remove ID from db */
                            if( dataSnapshot.child(mainModels.get(position).reviewID).hasChild(currentUserID) ){
//                                reviewLikesRef.child(mainModels.get(position).reviewID).child(currentUserID).removeValue();
//                                holder.liked = true;
                                DataSnapshot like_stored_state = dataSnapshot.child(mainModels.get(position).reviewID).child(currentUserID).child("liked");
                                DatabaseReference user_review_like = reviewLikesRef.child(mainModels.get(position).reviewID).child(currentUserID).child("liked");
                                if( Boolean.parseBoolean(like_stored_state.getValue().toString()) ){
                                    holder.likeB.setText("Like");
                                }
                                else {
                                    holder.likeB.setText("Unlike");
                                }
                                user_review_like.setValue( String.valueOf( !Boolean.parseBoolean(like_stored_state.getValue().toString()) ) );
                            }
                            /* User never interacted before with post, therefore he is liking it for the first time */
                            else {
                                reviewLikesRef.child(mainModels.get(position).reviewID).child(currentUserID).child("liked").setValue("true");
                                holder.likeB.setText("Unlike");
                            }
//                            for( DataSnapshot snapshot_userID : dataSnapshot.child(mainModels.get(position).reviewID).getChildren() ){
//
//                            }
                        }
                        /* is the reviewID does not exist, this means this is the first */
                        else {
                            reviewLikesRef.child(mainModels.get(position).reviewID).child(currentUserID).child("liked").setValue("true");
                            holder.likeB.setText("Unlike");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return mainModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView;
        TextView personName, movieName, activity, comment, date;
        EditText numLikes;
        ConstraintLayout parentLayout;
        RatingBar rating;
        Button likeB;

        CircleImageView profile_img;
        String personNewName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.news_movie_poster);
            profile_img = itemView.findViewById(R.id.news_profile_image);
            personName = itemView.findViewById(R.id.news_profile_name);
            personNewName = personName.getText().toString();
            movieName = itemView.findViewById(R.id.news_movie_name);
            activity = itemView.findViewById(R.id.news_activity_type);
            comment = itemView.findViewById(R.id.news_snippet);
            date = itemView.findViewById(R.id.news_time);
            rating = itemView.findViewById(R.id.review_rating);
            parentLayout = itemView.findViewById(R.id.parent_news);
            likeB = itemView.findViewById(R.id.news_like);
            numLikes = itemView.findViewById(R.id.num_likes);
        }
    }
}
