package com.fist.cineyet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HotFragment extends Fragment {

    View myview;
    RecyclerView newsfeedLayout;
    newsFeedAdapter mainAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myview= inflater.inflate(R.layout.fragment_hot, container, false);
        ArrayList<newsfeedItems> myMovies=new ArrayList<newsfeedItems>();
        for(int i=0;i<10;i++){
            myMovies.add(new newsfeedItems("Sept-06-2018", "YiFei Tang", "Midsommar",  "Midsommar scared the shit out of me. What the hell was that",
                    "Reviewed",R.drawable.midsommarposter,R.drawable.roundprofilepic));
        }
        scrollFunction(R.id.newsFeed,myMovies);
        return myview;
    }

    private void scrollFunction(Integer id, ArrayList<newsfeedItems> moviesList){
        newsfeedLayout=(RecyclerView) myview.findViewById(id);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        newsfeedLayout.setLayoutManager(layoutManager);
        newsfeedLayout.setItemAnimator(new DefaultItemAnimator());
        mainAdapter= new newsFeedAdapter(getActivity(),moviesList);
        newsfeedLayout.setAdapter(mainAdapter);
    }
}

