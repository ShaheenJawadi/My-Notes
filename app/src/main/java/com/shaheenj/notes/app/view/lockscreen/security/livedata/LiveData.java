package com.shaheenj.notes.app.view.lockscreen.security.livedata;

public class LiveData<T> extends androidx.lifecycle.LiveData<T> {

    public void setData(T data) {
        setValue(data);
    }

}
