package com.shaheenj.notes.app.view.lockscreen.security;

public class SecurityUtilsFactory {

    public static SecurityUtils getPFSecurityUtilsInstance() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return SecurityUtils.getInstance();
        } else {
            return SecurityUtils.getInstance();
        }
    }

}
