package com.shaheenj.notes.app.Methods;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import com.shaheenj.notes.app.entities.Note;
import com.shaheenj.notes.app.listeners.InterAdListener;


public class Methods {

    private Context context;
    private InterAdListener interAdListener;

    public Methods(Context context) {
        this.context = context;
    }

    public Methods(Context context, InterAdListener interAdListener) {
        this.context = context;
        this.interAdListener = interAdListener;
    }

    public void showSnackBar(String message, String type) {
        if (type.equals("success")){
            Toasty.success(context, message, Toast.LENGTH_SHORT, true).show();
        }else {
            Toasty.error(context, message, Toast.LENGTH_SHORT, true).show();
        }
    }





    public void showInter(final int pos, final Note note, final String type) {
        interAdListener.onClick(pos, note, type);
    }






}