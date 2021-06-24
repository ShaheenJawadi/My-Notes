package com.shaheenj.notes.app.view.lockscreen.security;

import android.content.Context;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.shaheenj.notes.app.view.lockscreen.security.callbacks.PinCodeHelperCallback;

 
public class FingerprintPinCodeHelper implements PinCodeHelper {


    private static final String FINGERPRINT_ALIAS = "fp_fingerprint_lock_screen_key_store";
    private static final String PIN_ALIAS = "fp_pin_lock_screen_key_store";

    private static final FingerprintPinCodeHelper ourInstance = new FingerprintPinCodeHelper();

    public static FingerprintPinCodeHelper getInstance() {
        return ourInstance;
    }

    private final SecurityUtils pfSecurityUtils
            = SecurityUtilsFactory.getPFSecurityUtilsInstance();

    private FingerprintPinCodeHelper() {

    }

 
    @Override
    public void encodePin(Context context, String pin, PinCodeHelperCallback<String> callback) {
        try {
            final String encoded = pfSecurityUtils.encode(context, PIN_ALIAS, pin, false);
            if (callback != null) {
                callback.onResult(new Result(encoded));
            }
        } catch (SecurityException e) {
            if (callback != null) {
                callback.onResult(new Result(e.getError()));
            }
        }
    }
 
    @Override
    public void checkPin(Context context, String encodedPin, String pin, PinCodeHelperCallback<Boolean> callback) {
        try {
            final String pinCode = pfSecurityUtils.decode(PIN_ALIAS, encodedPin);
            if (callback != null) {
                callback.onResult(new Result(pinCode.equals(pin)));
            }
        } catch (SecurityException e) {
            if (callback != null) {
                callback.onResult(new Result(e.getError()));
            }
        }
    }


    private boolean isFingerPrintAvailable(Context context) {
        return FingerprintManagerCompat.from(context).isHardwareDetected();
    }

    private boolean isFingerPrintReady(Context context) {
        return FingerprintManagerCompat.from(context).hasEnrolledFingerprints();
    }
 
    @Override
    public void delete(PinCodeHelperCallback<Boolean> callback) {
        try {
            pfSecurityUtils.deleteKey(PIN_ALIAS);
            if (callback != null) {
                callback.onResult(new Result(true));
            }
        } catch (SecurityException e) {
            if (callback != null) {
                callback.onResult(new Result(e.getError()));
            }
        }
    }

 
    @Override
    public void isPinCodeEncryptionKeyExist(PinCodeHelperCallback<Boolean> callback) {
        try {
            final boolean isExist = pfSecurityUtils.isKeystoreContainAlias(PIN_ALIAS);
            if (callback != null) {
                callback.onResult(new Result(isExist));
            }
        } catch (SecurityException e) {
            if (callback != null) {
                callback.onResult(new Result(e.getError()));
            }
        }
    }

}
