package com.fist.cineyet;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>{
    private static final String TAG="MainAdapter";
    ArrayList<searchbarItems> mainModels;
    Context context;
    Boolean addable;
    FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener myAuthListener;
    private DatabaseReference userRef;
    String currentUserID;
    Boolean isFavouriteList;
    String profileType;
    public MainAdapter(Context context, ArrayList<searchbarItems> mainModels, Boolean list_is_addable, Boolean isFavouriteList,String profileType){
            this.context=context;
            this.mainModels=mainModels;
            this.addable=list_is_addable;
            this.isFavouriteList=isFavouriteList;
            this.profileType=profileType;
            myFirebaseAuth = FirebaseAuth.getInstance();
            currentUserID = myFirebaseAuth.getCurrentUser().getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child(isFavouriteList?"favouritelist":"watchlist");

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "on bind called on" +mainModels.get(position).url);
        if(mainModels.get(position).url.isEmpty())
            holder.imgView.setImageResource(R.drawable.plusbutton);
        else
            Picasso.get().load(mainModels.get(position).url).into(holder.imgView);

        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"clicked on image");
                    if(position!=mainModels.size()-1 && addable || !addable) {
                        Intent myIntent = new Intent(context, MoviePageActivity.class);
                        myIntent.putExtra("imdbID", mainModels.get(position).id);
                        context.startActivity(myIntent);
                    }
                    else if(position==mainModels.size()-1&&addable){
                        Intent myIntent=new Intent(context, AddToListActivity.class);
                        myIntent.putExtra("isFavourite",isFavouriteList);
                        myIntent.putExtra("addButton",true);
                        context.startActivity(myIntent);
                    }
            }
        });
        holder.imgView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view){
                if(position!=(mainModels.size()-1)) {
                    holder.rowItemParent.addView(holder.deleteContainer);
                    holder.rowItemParent.removeView(holder.imgView);
                    return true;
                }
                return false; //triggers plus button activity

            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mainModels.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mainModels.size());
                userRef.child(mainModels.get(position).id).removeValue();

            }
        });
        holder.cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                holder.rowItemParent.addView(holder.imgView);
                holder.rowItemParent.removeView(holder.deleteContainer);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mainModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView;
        LinearLayout deleteContainer;
        ConstraintLayout parentLayout;
        LinearLayout rowItemParent;
        Button cancelButton;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView=itemView.findViewById(R.id.row_item_image);
            parentLayout=itemView.findViewById(R.id.parent_layout);
            deleteContainer=itemView.findViewById(R.id.delete_menu_row_item);
            rowItemParent=itemView.findViewById(R.id.row_item_parent_layout);
            cancelButton=itemView.findViewById(R.id.cancel_button);
            deleteButton=itemView.findViewById(R.id.delete_button);
            rowItemParent.removeView(deleteContainer);


        }
    }
}
