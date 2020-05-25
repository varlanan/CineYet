package com.fist.cineyet;

public class reviewItems {
    String userProfilePic, userName, userReview, reviewedMovie, movieRating;

    public reviewItems(String pic, String userName, String review,  String movie, String rating){
        this.userProfilePic = pic;
        this.userName = userName;
        this.userReview = review;
        this.reviewedMovie = movie;
        this.movieRating = rating;
    }
}
