package com.shaheenj.notes.app.view.lockscreen.security;

import android.content.Context;

import androidx.annotation.NonNull;

public interface ISecurityUtils {

    String encode(@NonNull Context context, String alias, String input, boolean isAuthorizationRequared)
            throws SecurityException ;

    String decode(String alias, String encodedString) throws SecurityException;

    boolean isKeystoreContainAlias(String alias) throws SecurityException;

    void deleteKey(String alias) throws SecurityException;

}
