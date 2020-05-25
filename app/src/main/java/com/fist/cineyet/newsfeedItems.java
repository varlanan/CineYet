package com.fist.cineyet;

class newsfeedItems {
    String postTime, postPersonName, postActivityType, postComment, postMovie, movieRating;
    String movieDrawable, profilePic;

    public newsfeedItems(String time, String personName, String movie,  String comment, String activity, String movieDrawable, String profilePic, String rating){
        this.postTime = time;
        this.postActivityType = activity;
        this.postComment = comment;
        this.postPersonName = personName;
        this.postMovie = movie;
        this.movieDrawable = movieDrawable;
        this.profilePic = profilePic;
        this.movieRating = rating;
    }

}
