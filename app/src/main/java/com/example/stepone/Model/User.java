package com.example.stepone.Model;

public class User {
    private String id;
    private String username;
    private String imageURL;
    private String status;

    public User(String id, String username, String imageURL , String status) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURl() {
        return imageURL;
    }

    public void setImageURl(String imageURl) {
        this.imageURL = imageURl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
