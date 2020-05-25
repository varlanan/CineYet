package com.fist.cineyet;

class newsfeedItems {
    String postTime, postPersonName, postActivityType, postComment, postMovie, movieRating, reviewID, userID;
    String movieDrawable, profilePic;

    public newsfeedItems(String time, String personName, String movie,  String comment, String activity, String movieDrawable, String profilePic, String rating, String reviewID, String userID){
        this.postTime = time;
        this.postPersonName = personName;
        this.postMovie = movie;
        this.postComment = comment;
        this.postActivityType = activity;
        this.movieDrawable = movieDrawable;
        this.profilePic = profilePic;
        this.movieRating = rating;
        this.reviewID = reviewID;
        this.userID = userID;

    }

}
