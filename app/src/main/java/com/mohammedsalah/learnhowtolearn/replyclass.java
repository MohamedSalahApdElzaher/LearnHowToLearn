package com.mohammedsalah.learnhowtolearn;


public class replyclass {
    String  date , reply , userid , name , imageuri ;
    public replyclass(String date ,String reply,String userid , String name , String imageuri){
        this.name=name;
        this.imageuri=imageuri;
        this.date=date;
        this.reply =reply;
        this.userid=userid;
    }

    public replyclass(){}
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getReply() {
        return reply;
    }

    public String getImageuri() {
        return imageuri;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}

