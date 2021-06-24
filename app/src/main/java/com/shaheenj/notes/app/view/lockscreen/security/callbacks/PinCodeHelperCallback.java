package com.shaheenj.notes.app.view.lockscreen.security.callbacks;

import com.shaheenj.notes.app.view.lockscreen.security.Result;

public interface PinCodeHelperCallback<T> {
    void onResult(Result<T> result);
}
