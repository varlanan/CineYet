package com.fist.cineyet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MoviePageActivity extends AppCompatActivity {
    private static final String TAG = "MainAdapter";
    /* Movie Description Variables */
    private String imdbID;
    private Boolean noGoodImgFound, isTitle2Lines, isTitle3Lines, isTitle4Lines, isReqSuccess, isReq2Success, isReq3Success;
    String movieTitle, movieYear, movieRunningTime, type, posterUrl, movieRating, movieSummary, movieSceneImg;
    ArrayList<String> movieGenres = new ArrayList<>();
    private String mainDirectors, mainGenres;
    private static final String API_KEY = BuildConfig.ApiKey;

    private TextView title, directors, year, rating, genres, summary, runningTime, sceneImg;
    private ImageView poster, movieScene;

    /* Navigation bar buttons */
    private ImageButton tell_a_friend, add_to_list, write_review;

    private RecyclerView allReviewsLayout;
    private reviewsAdapter listAdapter;

    private FirebaseAuth myFirebaseAuth;
    private DatabaseReference movieRef;
    private DatabaseReference userRef;
    String currentUserID;

    String userProfilePic, userName;
    ArrayList<reviewItems> movieReviews = new ArrayList<reviewItems>();
//    private ProgressBar spinner;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(!isReqSuccess){
                Toast.makeText(MoviePageActivity.this, "Bad JSON Response, Please Try Again",Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(MoviePageActivity.this, HomeActivity.class);
                startActivity(myIntent);
            }
            /* No JSON errors encountered */
            else{
                title = (TextView) findViewById(R.id.movie_title);
                year = (TextView) findViewById(R.id.movie_year);
                rating = (TextView) findViewById(R.id.movie_rating);
                summary = (TextView) findViewById(R.id.movie_summary);
                genres = (TextView) findViewById(R.id.movie_genre);
                runningTime = (TextView) findViewById(R.id.movie_time);
                poster = (ImageView) findViewById(R.id.movie_poster);

//                setContentView(R.layout.activity_movie_page);
//                ViewGroup.LayoutParams params = title.getLayoutParams();
                if(isTitle2Lines){
                    title.getLayoutParams().height = 170;
                }
                else if(isTitle3Lines){
                    title.getLayoutParams().height = 250;
                }
                else if(isTitle4Lines){
                    title.getLayoutParams().height = 330;
                }
                else {
                    title.getLayoutParams().height = 100;
                }
                title.setText(movieTitle);
                year.setText(movieYear);
                rating.setText(movieRating);
                summary.setText(movieSummary);
                genres.setText(mainGenres);
                summary.setText(movieSummary);
                String time_mins = movieRunningTime + " mins";
                runningTime.setText(time_mins);

                Picasso.get().load(posterUrl).placeholder(R.drawable.gradient_flip).into(poster);

            }
        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler2 = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (!isReq2Success) {
                Toast.makeText(MoviePageActivity.this, "Bad JSON Response, Please Try Again", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(MoviePageActivity.this, HomeActivity.class);
                startActivity(myIntent);
            }
            /* No JSON errors encountered */
            else {
                directors = (TextView) findViewById(R.id.movie_director);
                directors.setText(mainDirectors);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler3 = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (!isReq3Success) {
                Toast.makeText(MoviePageActivity.this, "Bad JSON Response, Please Try Again", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(MoviePageActivity.this, HomeActivity.class);
                startActivity(myIntent);
            }
            /* No JSON errors encountered */
            else {
                movieScene = (ImageView) findViewById(R.id.movie_scene);
                if(noGoodImgFound){
                    movieScene.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
                Picasso.get().load(movieSceneImg).placeholder(R.drawable.gradient_flip).into(movieScene);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_page);
        Log.d(TAG,"oncreate started");

        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = myFirebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        movieRef = FirebaseDatabase.getInstance().getReference().child("ReviewedMovies");

        imdbID = getIntent().getExtras().getString("imdbID");

        tell_a_friend = (ImageButton) findViewById(R.id.tell_a_friend);
        add_to_list = (ImageButton) findViewById(R.id.add_to_list);
        write_review = (ImageButton) findViewById(R.id.write_review);

        tell_a_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        add_to_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        isReqSuccess = false;
        isReq2Success = false;
        isReq3Success = false;

//        spinner = (ProgressBar) findViewById(R.id.progress_bar);
//        spinner.setVisibility(View.VISIBLE);

        /* Get movie information from IMDB API */
        getOverviewMovieDetails(imdbID);
        getTopCrewDetails(imdbID);
        getMovieImages(imdbID);

        write_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MoviePageActivity.this, WriteReviewActivity.class);
                myIntent.putExtra("imdbID",imdbID);
                /* This won't work since we exceeded API quota */
//                myIntent.putExtra("movieTitle", movieTitle);
//                myIntent.putExtra("movieScene", movieSceneImg);
//                myIntent.putExtra("isTitle2Lines", isTitle2Lines);
//                myIntent.putExtra("isTitle3Lines", isTitle3Lines);
//                myIntent.putExtra("isTitle4Lines", isTitle4Lines);
//                myIntent.putExtra("noGoodImgFound", noGoodImgFound);
                startActivity(myIntent);

            }
        });
        /* Retrieve all reviews for the movie with the given imdbID */
        movieRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(imdbID)){
                    for(DataSnapshot snapshot : dataSnapshot.child(imdbID).getChildren()) {
                        reviewItems newReviewItem = new reviewItems(snapshot.child("userProfilePic").getValue().toString(), snapshot.child("userName").getValue().toString(),
                                snapshot.child("review").getValue().toString(), imdbID);
                        movieReviews.add(newReviewItem);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        displayReviews(movieReviews);

//        spinner.setVisibility(View.GONE);
    }

    private void getMovieImages(String imdbID) {
        OkHttpClient client3 = new OkHttpClient();
        Request request3 = new Request.Builder()
                .url("https://imdb8.p.rapidapi.com/title/get-images?limit=25&tconst=" + imdbID)
                .get()
                .addHeader("x-rapidapi-host", "imdb8.p.rapidapi.com")
                .addHeader("x-rapidapi-key",  BuildConfig.ApiKey)
                .build();

        client3.newCall(request3).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                //Toast.makeText(MoviePageActivity.this, "Access unsuccessful, Please Try Again",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseString = response.body().string();
                    try {
                        JSONObject object = new JSONObject(responseString);
                        JSONArray images = object.getJSONArray("images");

                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        int screenWidth = displayMetrics.widthPixels;
                        int screenHeight = displayMetrics.heightPixels;
                        double dim_red_ratio;
                        int og_height, og_width, img_index;
                        noGoodImgFound = false;
                        img_index = 1;

                        for(int i = 0; i < images.length(); i++){
                            og_width = Integer.parseInt(images.getJSONObject(i).getString("width"));
                            og_height = Integer.parseInt(images.getJSONObject(i).getString("height"));
                            dim_red_ratio = ((double)screenWidth)/ ((double) og_width);
                            if( ( (og_height*dim_red_ratio) < (screenHeight * 0.7) ) && dim_red_ratio < 1.00 ){
                                img_index = i;
                                break;
                            }
                            /* if no pictures were found that can be nicely reduced while maintaining original ratio,
                             * consider changing scaletype or even looking through the other set of images (requires another request from server) */
                            if( (i == images.length() - 1) && img_index != i){
                                noGoodImgFound = true;
                            }
                        }
                        movieSceneImg = images.getJSONObject(img_index).getString("url");

                        isReq3Success = true;
                        handler3.sendEmptyMessage(0);

                    } catch (JSONException e) {
                        isReq3Success = false;
                        e.printStackTrace();
                        //Toast.makeText(MoviePageActivity.this, "Bad JSON Response, Please Try Again",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    private void getTopCrewDetails(String imdbID) {
        OkHttpClient client2 = new OkHttpClient();
        Request request2 = new Request.Builder()
                .url("https://imdb8.p.rapidapi.com/title/get-top-crew?tconst=" + imdbID)
                .get()
                .addHeader("x-rapidapi-host", "imdb8.p.rapidapi.com")
                .addHeader("x-rapidapi-key",  BuildConfig.ApiKey)
                .build();

        client2.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                //Toast.makeText(MoviePageActivity.this, "Access unsuccessful, Please Try Again",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseString = response.body().string();
                    try {
                        JSONObject object = new JSONObject(responseString);
                        JSONArray directors = object.getJSONArray("directors");

                        mainDirectors = (directors.getJSONObject(0)).getString("name");

//                        /* first two main directors if the list is longer */
//                        if(directors.length() == 1){
//                            mainDirectors = (directors.getJSONObject(0)).getString("name");
//                        }
//                        else {
//                            /* Number of directors */
//                            String first_dir = directors.getJSONObject(0).getString("name");
//                            String second_dir = directors.getJSONObject(1).getString("name");
//                            String[] first_dir_name = first_dir.split("");
//                            String[] second_dir_name = second_dir.split("");
//                            int max_length = 20;
//
//                            if( (first_dir_name[1].length() + second_dir_name[1].length()) <= max_length ){
////                                mainDirectors = (directors.getJSONObject(0)).getString("name") + " & " + (directors.getJSONObject(1)).getString("name");
//                                mainDirectors = first_dir_name[0].charAt(0) + ". " + first_dir_name[1] + " & " + second_dir_name[0].charAt(0) + ". " + second_dir_name[1];
//                            }
//                            else{
//                                mainDirectors = (directors.getJSONObject(0)).getString("name");
//                            }
//                        }

                        isReq2Success = true;
                        handler2.sendEmptyMessage(0);

                    } catch (JSONException e) {
                        isReq2Success = false;
                        e.printStackTrace();
                        //Toast.makeText(MoviePageActivity.this, "Bad JSON Response, Please Try Again",Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });
    }

    private void getOverviewMovieDetails(String imdbID) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://imdb8.p.rapidapi.com/title/get-overview-details?currentCountry=US&tconst=" + imdbID)
                .get()
                .addHeader("x-rapidapi-host", "imdb8.p.rapidapi.com")
                .addHeader("x-rapidapi-key",  BuildConfig.ApiKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Toast.makeText(MoviePageActivity.this, "Access unsuccessful, Please Try Again",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseString = response.body().string();
                    try {
                        JSONObject object = new JSONObject(responseString);
                        JSONObject mainInfo = object.getJSONObject("title");
                        JSONObject image = mainInfo.getJSONObject("image");
                        JSONObject ratings = object.getJSONObject("ratings");
                        JSONObject plotOutline = object.getJSONObject("plotOutline");
                        JSONArray genres = object.getJSONArray("genres");

                        isTitle2Lines = false;
                        isTitle3Lines = false;
                        isTitle4Lines = false;

                        movieTitle = mainInfo.getString("title");
                        int num_chars_title = movieTitle.length();
                        int line = 20;
                        if(num_chars_title > line){
                            if(num_chars_title <= line*2){
                                isTitle2Lines = true;
                            }
                            else if(num_chars_title <= line*3){
                                isTitle3Lines = true;
                            }
                            else{
                                isTitle4Lines = true;
                            }
                        }
                        movieYear = String.valueOf(mainInfo.getInt("year"));
                        movieRunningTime = String.valueOf(mainInfo.getInt("runningTimeInMinutes"));
                        type = mainInfo.getString("titleType");
                        posterUrl = image.getString("url");
                        movieRating = String.valueOf(ratings.getDouble("rating"));
                        movieSummary = plotOutline.getString("text");
                        if(genres.length() == 1){
                            mainGenres = genres.getString(0);
                        }
                        else {
                            mainGenres = genres.getString(0)+ ", " + genres.getString(1);
                        }
                        for(int i = 0; i < genres.length(); i++){
                            movieGenres.add(genres.getString(i));
                        }
                        isReqSuccess = true;
                        handler.sendEmptyMessage(0);


                    } catch (JSONException e) {
                        isReqSuccess = false;
                        e.printStackTrace();
                        Toast.makeText(MoviePageActivity.this, "Bad JSON Response, Please Try Again",Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });
    }

    private void displayReviews(ArrayList<reviewItems> allReviews) {
        /* Hardcoded reviews for testing */
//        ArrayList<reviewItems> allReviews = new ArrayList<>();
//        for(int i = 0; i < 5; i++){
//            allReviews.add(new reviewItems("pic-url", "Big Mama", "wow this was an amazingggggggggggggg movie 10/10 would recommend to a friend", imdbID));
//        }
        if(allReviews != null) {
            allReviewsLayout = (RecyclerView) findViewById(R.id.review_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(MoviePageActivity.this, LinearLayoutManager.VERTICAL, false);
            allReviewsLayout.setLayoutManager(layoutManager);
            allReviewsLayout.setItemAnimator(new DefaultItemAnimator());
            listAdapter = new reviewsAdapter(MoviePageActivity.this, allReviews);
            allReviewsLayout.setAdapter(listAdapter);
        }
    }



}

