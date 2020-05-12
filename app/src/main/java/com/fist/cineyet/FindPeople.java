package com.fist.cineyet;

public class FindPeople {
    public String profileimage, name, interests;
   public FindPeople(){

    }
    public FindPeople(String profileimage, String name, String interests) {
        this.profileimage = profileimage;
        this.name = name;
        this.interests=interests;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }
}
