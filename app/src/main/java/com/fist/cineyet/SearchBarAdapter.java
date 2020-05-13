package com.fist.cineyet;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

class SearchBarAdapter extends RecyclerView.Adapter<SearchBarAdapter.ViewHolder>{
    Context c;
    ArrayList<searchbarItems> myModels;
    Boolean isFavourite;
    Boolean addButtonOn;
    final static String TAG="my adapter";
    FirebaseAuth myFirebaseAuth;
    String new_name, new_interests;
    private FirebaseAuth.AuthStateListener myAuthListener;
    private DatabaseReference userRef;
    private StorageReference UserProfileImageRef;
    String currentUserID;


    SearchBarAdapter(Context context, ArrayList<searchbarItems> mainModels,Boolean isFavourite,Boolean addButtonOn){
        this.c = context;
        this.myModels = mainModels;
        this.isFavourite = isFavourite;
        this.addButtonOn = addButtonOn;
        this.myFirebaseAuth = FirebaseAuth.getInstance();
        this.currentUserID = myFirebaseAuth.getCurrentUser().getUid();
        if(this.isFavourite)
            this.userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("favouritelist");
        else
            this.userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("watchlist");

    }
    @NonNull
    @Override
    public SearchBarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_search_result_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchBarAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "search bar on bind called on" + myModels.get(position));

        holder.myTitle.setText(myModels.get(position).title);
        holder.yearOfRelease.setText(myModels.get(position).year);
        Picasso.get().load(myModels.get(position).url).placeholder(R.drawable.movieposter).into(holder.moviePoster);
        /* Visit the movies page */
        holder.visitMoviePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(c, MoviePageActivity.class);
                myIntent.putExtra("imdbID", myModels.get(position).id);
                c.startActivity(myIntent);
            }
        });

        /* Add to list of movies */
        if(addButtonOn) {
            holder.addMovieToList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Map map = new HashMap();
                    map.put("poster", myModels.get(position).url);
                    map.put("title", myModels.get(position).title);
                    map.put("year", myModels.get(position).year);
                    userRef.child(myModels.get(position).id).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(c, HomeActivity.class);
                            intent.putExtra("name", "myname"); //confusing change later
                            c.startActivity(intent);
                        }
                    });

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return myModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView moviePoster;
        TextView myTitle;
        TextView yearOfRelease;
        ConstraintLayout layout;
        Button visitMoviePage;
        Button addMovieToList;

        public ViewHolder(@NonNull View view) {
            super(view);
            layout = view.findViewById(R.id.search_result_movie_parent);
            moviePoster = view.findViewById(R.id.search_result_movie_poster);
            myTitle = view.findViewById(R.id.search_result_movie_title);
            yearOfRelease = view.findViewById(R.id.search_result_movie_year);
            visitMoviePage = view.findViewById(R.id.search_result_item_visit_button);
            addMovieToList = view.findViewById(R.id.search_result_item_add_button);
            if(!addButtonOn){
                ViewGroup layout = (ViewGroup) addMovieToList.getParent();
                layout.removeView(addMovieToList);
            }
        }
    }
}