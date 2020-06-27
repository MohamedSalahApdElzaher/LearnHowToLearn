package com.mohammedsalah.learnhowtolearn;


import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class rev_id {

    @Exclude
    public String rev_id;

    public <T extends rev_id> T withId(@NonNull final String id){
        this.rev_id = id;
        return (T) this;
    }
}
