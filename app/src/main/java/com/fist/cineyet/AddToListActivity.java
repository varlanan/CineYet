package com.fist.cineyet;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddToListActivity  extends AppCompatActivity {
    final static String TAG="TAG";
    ImageView searchButton;
    RecyclerView myView;
    private static final String API_KEY = BuildConfig.ApiKey;
    EditText search_movie;
    ArrayList<searchbarItems> myMovies;
    SearchBarAdapter listAdapter;
    String listType;
    Boolean addButtonOn;
    FirebaseAuth myFirebaseAuth;
    String currentUserID;
    boolean fromHome;
    Handler handler= new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.d(TAG,"Starting adapter with size of "+myMovies.size());
            if(getIntent().hasExtra("profile_id"))
                listAdapter= new SearchBarAdapter(AddToListActivity.this,myMovies, listType,addButtonOn, getIntent().getExtras().getString("profile_id"),fromHome); //you are adding to someone else's rec list
            else
                listAdapter=new SearchBarAdapter(AddToListActivity.this, myMovies,listType,addButtonOn,currentUserID,fromHome); //adding to your own list
            myView.setAdapter(listAdapter);

        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_movie);
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = myFirebaseAuth.getCurrentUser().getUid();
        listType = getIntent().getExtras().getString("listType");
        addButtonOn=getIntent().getExtras().getBoolean("addButton");
        search_movie=findViewById(R.id.search_movie_bar);
        myView=findViewById(R.id.search_movie_results);
        searchButton=findViewById(R.id.search_movie_button);
        myMovies=new ArrayList<searchbarItems>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddToListActivity.this,LinearLayoutManager.VERTICAL,false);
        myView.setLayoutManager(layoutManager);
        myView.setItemAnimator(new DefaultItemAnimator());
        if(getIntent().hasExtra("fromHome"))
            fromHome=true;
        else
            fromHome=false;
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String movieToSearch=search_movie.getText().toString();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://movie-database-imdb-alternative.p.rapidapi.com/?page=1&r=json&s="+movieToSearch)
                        .get()
                        .addHeader("x-rapidapi-host", "movie-database-imdb-alternative.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", BuildConfig.AltApiKey)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                        Toast.makeText(AddToListActivity.this, "Search Unsuccessful, Please Try Again",Toast.LENGTH_SHORT).show();

                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if(response.isSuccessful()){
                            String responseString=response.body().string();
                            try {
                                JSONObject jObject=new JSONObject(responseString);
                                JSONArray Jarray = jObject.getJSONArray("Search");
                                for (int i = 0; i < Jarray.length(); i++) {
                                    JSONObject object     = Jarray.getJSONObject(i);
                                    String movieTitle= object.getString("Title");
                                    String movieYear= object.getString("Year");
                                    String type=object.getString("Type");
                                    String posterURL=object.getString("Poster");
                                    String imdbID=object.getString("imdbID");
                                    if(type.equals("movie")){
                                        Log.d(TAG, "add movie"+type);

                                        myMovies.add(new searchbarItems(movieTitle,movieYear,posterURL,imdbID));
                                    }
                                    handler.sendEmptyMessage(0);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
//                                Toast.makeText(AddToListActivity.this, "Bad JSON Response, Please Try Again",Toast.LENGTH_SHORT).show();

                            }


//                            Log.d(TAG,responseString);

                        }
                    }
                });



            }
        });



    }
}
