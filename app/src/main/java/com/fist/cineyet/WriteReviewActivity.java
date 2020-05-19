package com.fist.cineyet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class WriteReviewActivity extends AppCompatActivity {
    private FirebaseAuth myFirebaseAuth;
    private DatabaseReference movieRef;
    private DatabaseReference userRef;
    String currentUserID, userProfilePic, userName, reviewText ,imdbID, movieScene;
    private Boolean isTitle2Lines, isTitle3Lines, isTitle4Lines, noGoodImgFound;

    private Button submitReview;
    private ImageButton exitReview;
    private EditText reviewTextBox;
    private TextView movieTitle;
    private ImageView movieSceneImg;
    ArrayList<reviewItems> movieReviews = new ArrayList<reviewItems>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_review_page);

        imdbID = getIntent().getExtras().getString("imdbID");

        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = myFirebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        submitReview = (Button) findViewById(R.id.submit_review);
        exitReview = (ImageButton) findViewById(R.id.exit_review);
        reviewTextBox = (EditText) findViewById(R.id.review_text);
        movieTitle = (TextView) findViewById(R.id.movie_title_review);

        /* this won't work since we exceeded API quota */
        isTitle2Lines = getIntent().getExtras().getBoolean("isTitle2Lines");
        isTitle3Lines = getIntent().getExtras().getBoolean("isTitle3Lines");
        isTitle4Lines = getIntent().getExtras().getBoolean("isTitle4ines");

        if(isTitle2Lines){
            movieTitle.getLayoutParams().height = 170;
        }
        else if(isTitle3Lines){
            movieTitle.getLayoutParams().height = 250;
        }
        else if(isTitle4Lines){
            movieTitle.getLayoutParams().height = 330;
        }
        else {
            movieTitle.getLayoutParams().height = 100;
        }
        if(movieTitle != null){
            movieTitle.setText(getIntent().getExtras().getString("movieTitle"));
            noGoodImgFound = getIntent().getExtras().getBoolean("noGoodImgFound");
            movieSceneImg = (ImageView) findViewById(R.id.movie_scene_review);
            if(noGoodImgFound){
                movieSceneImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
            Picasso.get().load(getIntent().getExtras().getString("movieScene")).placeholder(R.drawable.gradient_flip).into(movieSceneImg);
        }

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userProfilePic = dataSnapshot.child("profileimage").getValue().toString();
                    userName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewText = reviewTextBox.getText().toString();
                movieRef = FirebaseDatabase.getInstance().getReference().child("ReviewedMovies");

                movieRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        movieRef = movieRef.child(imdbID);
                        String id = movieRef.child(imdbID).push().getKey();

                        HashMap<String, String> reviewMap = new HashMap<>();
                        reviewMap.put("imdbID", imdbID);
                        reviewMap.put("userProfilePic", userProfilePic);
                        reviewMap.put("userName", userName);
                        reviewMap.put("review", reviewText);
                        if(id != null){
                            movieRef.child(id).setValue(reviewMap);
                        }
                        Intent myIntent = new Intent(WriteReviewActivity.this, MoviePageActivity.class);
                        myIntent.putExtra("imdbID", imdbID);
                        startActivity(myIntent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        });

        exitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(WriteReviewActivity.this, MoviePageActivity.class);
                myIntent.putExtra("imdbID", imdbID);
                startActivity(myIntent);
                finish();
            }
        });

    }
}
