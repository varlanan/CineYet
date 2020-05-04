package com.fist.cineyet;

class newsfeedItems {
    String postTime;
    String postPersonName;
    String postActivityType;
    String postComment;
    String postMovie;
    Integer movieDrawable;
    Integer profilePic;
    public newsfeedItems(String time, String personName, String movie,  String comment, String activity,Integer movieDrawable,Integer profilePic){
        this.postTime=time;
        this.postActivityType=activity;
        this.postComment=comment;
        this.postPersonName=personName;
        this.postMovie=movie;
        this.movieDrawable=movieDrawable;
        this.profilePic=profilePic;
    }

}
