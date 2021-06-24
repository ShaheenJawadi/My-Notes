package com.shaheenj.notes.app.view.lockscreen.security;

public class SecurityManager {
    private static final SecurityManager ourInstance = new SecurityManager();

    public static SecurityManager getInstance() {
        return ourInstance;
    }

    private SecurityManager() {
    }

    private PinCodeHelper mPinCodeHelper = FingerprintPinCodeHelper.getInstance();

    public void setPinCodeHelper(PinCodeHelper pinCodeHelper) {
        mPinCodeHelper = pinCodeHelper;
    }

    public PinCodeHelper getPinCodeHelper() {
        return mPinCodeHelper;
    }
}
