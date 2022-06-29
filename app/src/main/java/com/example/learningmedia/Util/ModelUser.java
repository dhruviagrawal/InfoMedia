package com.example.learningmedia.Util;

public class ModelUser {
    private String Name;
    private String id;
    private String email;
    private String phone;
    private String imageurl;
    private String bio;
   public ModelUser(String Name,String id,String email,String phone,String imageurl,String bio){
       this.Name=Name;
       this.id=id;
       this.email=email;
       this.phone=phone;
       this.imageurl=imageurl;
       this.bio=bio;
   }

    public ModelUser() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
