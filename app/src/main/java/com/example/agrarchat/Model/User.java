package com.example.agrarchat.Model;

public class User {

    private String fullname;
    private String id;
    private String imageURL;

    public User(String username, String id, String imageURL) {
        this.fullname = username;
        this.id = id;
        this.imageURL = imageURL;
    }

    public User() {
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
