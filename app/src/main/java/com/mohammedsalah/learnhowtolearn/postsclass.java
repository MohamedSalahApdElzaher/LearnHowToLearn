package com.mohammedsalah.learnhowtolearn;

public class postsclass extends rev_id{
    String title , description , min ,date , location,name,userimage , tags , id,com;

    public postsclass(String title, String description, String min, String date  , String name , String userimage , String tags , String id ,  String com) {
        this.title = title;
        this.description = description;
        this.min = min;
        this.date = date;
        this.name = name;
        this.userimage = userimage;
        this.tags = tags;
        this.id=id;
        this.com = com;
    }

    public postsclass(){}

    public String getTags(){
        return tags;
    }

    public String getLocation() {
        return location;
    }

    public String getUserimage() {
        return userimage;
    }


    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }

    public String getMin() {
        return min;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public String getCom() {
        return com;
    }
}

