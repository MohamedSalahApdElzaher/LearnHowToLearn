package com.mohammedsalah.learnhowtolearn;
import java.util.Date;

public class chatclass {
    String name , text;
    long date;

    public chatclass(String name , String text){
        this.name = name;
        this.text=text;
        this.date=new Date().getTime();
    }

    public chatclass(){}

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public long getDate() {
        return date;
    }
}
