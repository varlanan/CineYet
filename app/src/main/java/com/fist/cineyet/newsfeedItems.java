package com.fist.cineyet;

class newsfeedItems {
    String postTime, postPersonName, postActivityType, postComment, postMovie;
    Integer movieDrawable, profilePic;

    public newsfeedItems(String time, String personName, String movie,  String comment, String activity, Integer movieDrawable, Integer profilePic){
        this.postTime = time;
        this.postActivityType = activity;
        this.postComment = comment;
        this.postPersonName = personName;
        this.postMovie = movie;
        this.movieDrawable = movieDrawable;
        this.profilePic = profilePic;
    }

}
