package com.onaopemipodimowo.O4Homes;

public class ReadWriteUserDetails {
    public String doB , gender, college, username, id, imageURL, search, status;

    //Constructor
    public ReadWriteUserDetails(){};

    public ReadWriteUserDetails( String textDoB, String textGender, String textCollege, String username, String id, String imageURL, String search, String status){
        this.doB = textDoB;
        this.gender = textGender;
        this.college = textCollege;
        this.username = username;
        this.id = id;
        this.imageURL = imageURL;
        this.search = search;
        this.status = status;
    }
}
