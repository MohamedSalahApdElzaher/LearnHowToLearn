package com.mohammedsalah.learnhowtolearn;


public class category_class {

    String  title , desc , imageurl , Id;
    int image_id;


    public category_class(String Id,int image_id, String title , String description
            , String imageurl   ) {
        this.image_id = image_id;
        this.title = title;
        this.desc = description;
        this.imageurl = imageurl;

        this.Id = Id;
    }

    public String getId()
    {
        return Id;
    }

    public category_class(){}

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}

