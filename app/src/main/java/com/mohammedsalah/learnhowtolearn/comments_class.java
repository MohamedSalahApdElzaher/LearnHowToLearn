package com.mohammedsalah.learnhowtolearn;

public class comments_class extends postId{
    String  date , ask , userid , nameuser , userimage , comm_post ,id;
    public comments_class(String date ,String ask,String userid , String nameuser, String userimage , String id , String comm_post ){
     this.comm_post=comm_post;
        this.date=date;
        this.ask =ask;
        this.userid=userid;
        this.nameuser = nameuser;
        this.userimage = userimage;
        this.id=id;
    }

    public comments_class(){}
    public String getUserid() {
        return userid;
    }

    public String getAsk() {
        return ask;
    }

    public String getId() {
        return id;
    }

    public String getNameuser() {
        return nameuser;
    }

    public String getUserimage() {
        return userimage;
    }

    public String getComm_post() {
        return comm_post;
    }

    public String getDate() {
        return date;
    }


}

