package com.fist.cineyet;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

class SearchBarAdapter extends RecyclerView.Adapter<SearchBarAdapter.ViewHolder>{
    Context c;
    ArrayList<searchbarItems> myModels;
    final static String TAG="my adapter";
    SearchBarAdapter(Context context, ArrayList<searchbarItems> mainModels){
        this.c=context;
        this.myModels=mainModels;

    }
    @NonNull
    @Override
    public SearchBarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_search_result_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchBarAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "search bar on bind called on" + myModels.get(position));

        holder.myTitle.setText(myModels.get(position).title);
        holder.yearOfRelease.setText(myModels.get(position).year);
        Picasso.get().load(myModels.get(position).url).placeholder(R.drawable.movieposter).into(holder.moviePoster);

    }

    @Override
    public int getItemCount() {
        return myModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView moviePoster;
        TextView myTitle;
        TextView yearOfRelease;

        public ViewHolder(@NonNull View view) {
            super(view);
            moviePoster=view.findViewById(R.id.search_result_movie_poster);
            myTitle=view.findViewById(R.id.search_result_movie_title);
            yearOfRelease=view.findViewById(R.id.search_result_movie_year);
        }
    }
}
