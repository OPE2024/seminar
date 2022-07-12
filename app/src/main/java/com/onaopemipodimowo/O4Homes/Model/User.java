package com.onaopemipodimowo.O4Homes.Model;

public class User {

    private String username;
    private String gender;
    private String imageURL;
    private String college;
    private String dob;
    private String id;
    private String status;

    public User() {
    }

    public User(String username, String gender, String imageURL, String college, String dob, String id, String status) {
        this.username = username;
        this.gender = gender;
        this.imageURL = imageURL;
        this.college = college;
        this.dob = dob;
        this.id = id;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }
}
