package com.mohammedsalah.learnhowtolearn;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InterntConections {
    private Context context;
    InterntConections(Context context){
        this.context = context;
    }

    public boolean IsConnectToInternt(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo!=null && networkInfo.isConnected()){
                return true;
            }
        }
        return false;
    }
}