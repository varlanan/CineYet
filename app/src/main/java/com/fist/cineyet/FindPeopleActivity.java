package com.fist.cineyet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindPeopleActivity extends AppCompatActivity {

    Button searchPeople;
    EditText inputText;
    private DatabaseReference userRef;
    RecyclerView searchPeopleRecycler;
    String currentUserID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseAuth myFirebaseAuth=FirebaseAuth.getInstance();
        currentUserID = myFirebaseAuth.getCurrentUser().getUid();

        setContentView(R.layout.activity_search_people);
        searchPeople=findViewById(R.id.search_people_activity_button);
        searchPeopleRecycler=findViewById(R.id.search_people_results);
        inputText=findViewById(R.id.search_people_bar);
        searchPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myinput=inputText.getText().toString();
                searchPeople(myinput);
            }
        });
    }
    private void searchPeople(String searchBarInput){
        FirebaseRecyclerOptions<FindPeople> options=new FirebaseRecyclerOptions.Builder<FindPeople>()
                .setQuery(userRef.orderByChild("name").startAt(searchBarInput).endAt(searchBarInput+"\uf8ff"),FindPeople.class)
                .build();

        FirebaseRecyclerAdapter<FindPeople,FindFriendsViewholder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<FindPeople, FindFriendsViewholder>(options){

            @NonNull
            @Override
            public FindFriendsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.people_search_result,parent,false);
                FindFriendsViewholder viewholder=new FindFriendsViewholder(view);
                return viewholder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final FindFriendsViewholder holder, final int position, @NonNull final FindPeople model) {
                holder.name.setText(model.getName());
                holder.interests.setText(model.getInterests());
                Picasso.get().load(model.getProfileimage())
                        .placeholder(R.drawable.ic_account_circle_black_24dp)
                        .into(holder.profileimage);
                holder.profileimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment profFrag=new profile_page();
                        Bundle mybund=new Bundle();
                        String userKey=getRef(position).getKey();
                        if(userKey.equals(currentUserID))
                            mybund.putString("isPersonalProfile","PERSONAL");
                        else{
                            mybund.putString("isPersonalProfile","NOTFRIENDS"); //change later when you figure out friends lists
                            mybund.putString("UserID",getRef(position).getKey());
                        }
                        profFrag.setArguments(mybund);
                        getSupportFragmentManager().beginTransaction().replace(R.id.search_frag_container,profFrag).commit();

                    }
                });
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        searchPeopleRecycler.setLayoutManager(layoutManager);
        searchPeopleRecycler.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
    public static class FindFriendsViewholder extends RecyclerView.ViewHolder{
        TextView name;
        CircleImageView profileimage;
        TextView interests;

        public FindFriendsViewholder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.search_person_name);
            profileimage=itemView.findViewById(R.id.search_person_image);
            interests=itemView.findViewById(R.id.search_person_interests);
        }
    }
}
