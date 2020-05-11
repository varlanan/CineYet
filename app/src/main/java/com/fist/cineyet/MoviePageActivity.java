package com.fist.cineyet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MoviePageActivity extends AppCompatActivity {
    private static final String TAG = "MainAdapter";
    private String imdbID;
    private Boolean noGoodImgFound, isReqSuccess, isReq2Success, isReq3Success;
    String movieTitle, movieYear, movieRunningTime, type, posterUrl, movieRating, movieSummary, movieSceneImg;
    ArrayList<String> movieGenres = new ArrayList<>();
    private String mainDirectors, mainGenres;
    private static final String API_KEY = BuildConfig.ApiKey;

    private TextView title, directors, year, rating, genres, summary, runningTime, sceneImg;
    private ImageView poster, movieScene;

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
//                directors = (TextView) findViewById(R.id.movie_director);
                genres = (TextView) findViewById(R.id.movie_genre);
                runningTime = (TextView) findViewById(R.id.movie_time);
                poster = (ImageView) findViewById(R.id.movie_poster);


                title.setText(movieTitle);
                year.setText(movieYear);
                rating.setText(movieRating);
//                mainDirectors="Mama, Papa";
//                directors.setText(mainDirectors);
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
        isReqSuccess = false;
        isReq2Success = false;

        imdbID = getIntent().getExtras().getString("imdbID");

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

                        movieTitle = mainInfo.getString("title");
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

                        /* first two main directors if the list is longer */
                        if(directors.length() == 1){
                            mainDirectors = (directors.getJSONObject(0)).getString("name");
                        }
                        else {
                            mainDirectors = (directors.getJSONObject(0)).getString("name") + " & " + (directors.getJSONObject(1)).getString("name");
                        }

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
}

