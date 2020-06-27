package com.mohammedsalah.learnhowtolearn;

public class usersclass {
     String name, image , status_id , id ;
    public usersclass(String name , String image , String status_id , String id){
        this.name=name;
        this.image=image;
        this.status_id=status_id;
        this.id=id;
    }
   public usersclass(){}


    public String getId() {
        return id;
    }

    public String getStatus_id() {
        return status_id;
    }



    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
