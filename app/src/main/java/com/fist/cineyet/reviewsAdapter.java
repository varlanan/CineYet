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

public class reviewsAdapter extends RecyclerView.Adapter<reviewsAdapter.ViewHolder> {
    /* Firebase variables */
    private DatabaseReference userRef;
    FirebaseAuth myFirebaseAuth;
    String currentUserID;


    private static final String TAG="reviewAdapter";
    ArrayList<reviewItems> mainModels;
    Context context;
    public reviewsAdapter(Context context, ArrayList<reviewItems> mainModels){
        this.context = context;
        this.mainModels = mainModels;
    }
    @NonNull
    @Override
    public reviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item,parent,false);

        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = myFirebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        return new reviewsAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "review on bind on " + mainModels.get(position).reviewedMovie);

        holder.reviewText.setText(mainModels.get(position).userReview);

    }

    @Override
    public int getItemCount() {
        return mainModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView personName, reviewText;
        ConstraintLayout parentLayout;
        CircleImageView profileImg;
        String personNewName, reviewNew;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.reviewer_profile_pic);
            personName = itemView.findViewById(R.id.reviewer_name);;
            personNewName = personName.getText().toString();
            reviewText = itemView.findViewById(R.id.review_text);
            reviewNew = reviewText.getText().toString();
            parentLayout = itemView.findViewById(R.id.parent_news);

            /* Setting user profile pic and name */
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        String image_string = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image_string).placeholder(R.drawable.ic_account_circle_black_24dp).into(profileImg);

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
