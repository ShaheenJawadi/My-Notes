package com.shaheenj.notes.app.view.lockscreen.security;

import android.content.Context;

import com.shaheenj.notes.app.view.lockscreen.security.callbacks.PinCodeHelperCallback;

public interface PinCodeHelper {

    /**
     * Encode pin
     * @param context any context.
     * @param pin pin code string to check.
     * @param callBack PFPinCodeHelperCallback callback object.
     * @return true if pin codes matches.
     * @throws SecurityException  throw exception if something went wrong.
     */
    void encodePin(Context context, String pin, PinCodeHelperCallback<String> callBack);

    /**
     * Check if pin code is valid.
     * @param context any context.
     * @param encodedPin encoded pin code string.
     * @param pin pin code string to check.
     * @param callback PFPinCodeHelperCallback callback object.
     * @return true if pin codes matches.
     * @throws SecurityException  throw exception if something went wrong.
     */
    void checkPin(Context context, String encodedPin, String pin, PinCodeHelperCallback<Boolean> callback);

    /**
     * Delete pin code encryption key.
     * @param callback PFPinCodeHelperCallback callback object.
     * @throws SecurityException throw exception if something went wrong.
     */
    void delete(PinCodeHelperCallback<Boolean> callback);

    /**
     * Check if pin code encryption key is exist.
     * @param callback PFPinCodeHelperCallback callback object.
     * @return true if key exist in KeyStore.
     * @throws SecurityException throw exception if something went wrong.
     */
    void isPinCodeEncryptionKeyExist(PinCodeHelperCallback<Boolean> callback);

}
