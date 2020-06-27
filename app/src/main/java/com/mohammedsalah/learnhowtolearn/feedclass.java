package com.mohammedsalah.learnhowtolearn;

public class feedclass {
    String  date , reply , userid , name , imageuri ;

    public feedclass(String date ,String reply,String userid , String name , String imageuri){
        this.name=name;
        this.imageuri=imageuri;
        this.date=date;
        this.reply =reply;
        this.userid=userid;
    }

    public feedclass(){}

    public String getDate() {
        return date;
    }

    public String getReply() {
        return reply;
    }

    public String getUserid() {
        return userid;
    }

    public String getName() {
        return name;
    }

    public String getImageuri() {
        return imageuri;
    }
}
