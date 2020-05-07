package com.fist.cineyet;

public class searchbarItems {

    String title, year, url,id;
    searchbarItems(String movieTitle,String movieYear,String posterURL,String imdbID){
        this.id=imdbID;
        this.title=movieTitle;
        this.year=movieYear;
        this.url=posterURL;
    }
}
