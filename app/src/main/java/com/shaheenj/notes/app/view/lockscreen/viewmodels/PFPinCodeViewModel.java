package com.shaheenj.notes.app.view.lockscreen.viewmodels;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.shaheenj.notes.app.view.lockscreen.security.Result;
import com.shaheenj.notes.app.view.lockscreen.security.SecurityManager;
import com.shaheenj.notes.app.view.lockscreen.security.callbacks.PinCodeHelperCallback;
import com.shaheenj.notes.app.view.lockscreen.security.livedata.LiveData;


public class PFPinCodeViewModel extends ViewModel {

    public androidx.lifecycle.LiveData<Result<String>> encodePin(Context context, String pin) {
        final LiveData<Result<String>> liveData = new LiveData<>();
        SecurityManager.getInstance().getPinCodeHelper().encodePin(
                context,
                pin,
                new PinCodeHelperCallback<String>() {
                    @Override
                    public void onResult(Result<String> result) {
                        liveData.setData(result);
                    }
                }
        );
        return liveData;
    }

    public androidx.lifecycle.LiveData<Result<Boolean>> checkPin(Context context, String encodedPin, String pin) {
        final LiveData<Result<Boolean>> liveData = new LiveData<>();
        SecurityManager.getInstance().getPinCodeHelper().checkPin(
                context,
                encodedPin,
                pin,
                new PinCodeHelperCallback<Boolean>() {
                    @Override
                    public void onResult(Result<Boolean> result) {
                        liveData.setData(result);
                    }
                }
        );
        return liveData;
    }

    public androidx.lifecycle.LiveData<Result<Boolean>> delete() {
        final LiveData<Result<Boolean>> liveData = new LiveData<>();
        SecurityManager.getInstance().getPinCodeHelper().delete(
                new PinCodeHelperCallback<Boolean>() {
                    @Override
                    public void onResult(Result<Boolean> result) {
                        liveData.setData(result);
                    }
                }
        );
        return liveData;
    }

    public androidx.lifecycle.LiveData<Result<Boolean>> isPinCodeEncryptionKeyExist() {
        final LiveData<Result<Boolean>> liveData = new LiveData<>();
        SecurityManager.getInstance().getPinCodeHelper().isPinCodeEncryptionKeyExist(
                new PinCodeHelperCallback<Boolean>() {
                    @Override
                    public void onResult(Result<Boolean> result) {
                        liveData.setData(result);
                    }
                }
        );
        return liveData;
    }

}
