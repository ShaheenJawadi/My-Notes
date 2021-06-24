package com.shaheenj.notes.app.view.lockscreen.security;

public class Result<T> {

    private SecurityError mError = null;
    private T mResult = null;

    public Result(SecurityError mError) {
        this.mError = mError;
    }

    public Result(T result) {
        mResult = result;
    }

    public SecurityError getError() {
        return mError;
    }

    public T getResult() {
        return mResult;
    }
}
