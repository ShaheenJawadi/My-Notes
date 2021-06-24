package com.shaheenj.notes.app.Activity.LockScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.shaheenj.notes.app.view.lockscreen.LockScreenConfiguration;
import com.shaheenj.notes.app.view.lockscreen.fragments.LockScreenFragment;
import com.shaheenj.notes.app.view.lockscreen.security.Result;
import com.shaheenj.notes.app.view.lockscreen.viewmodels.PFPinCodeViewModel;

import com.shaheenj.notes.app.Activity.MainActivity;
import com.shaheenj.notes.app.Methods.Methods;
import com.shaheenj.notes.app.R;
import com.shaheenj.notes.app.SharedPref.Setting;
import com.shaheenj.notes.app.SharedPref.SharedPref;

public class LockScreenActivity extends AppCompatActivity {

    private SharedPref sharedPre;
    private Methods methods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lockscreen);

        sharedPre = new SharedPref(this);
        methods = new Methods(this);
        showLockScreenFragment();
    }


    private final LockScreenFragment.OnPFLockScreenCodeCreateListener mCodeCreateListener = new LockScreenFragment.OnPFLockScreenCodeCreateListener() {
                @Override
                public void onCodeCreated(String encodedCode) {
                    methods.showSnackBar("Code created","success");
                    sharedPre.saveToPref(encodedCode, true);
                    Setting.in_code = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    },500);
                }

                @Override
                public void onNewCodeValidationFailed() {
                    methods.showSnackBar("Code validation error","error");
                }
            };

    private final LockScreenFragment.OnPFLockScreenLoginListener mLoginListener = new LockScreenFragment.OnPFLockScreenLoginListener() {

                @Override
                public void onCodeInputSuccessful() {
                    methods.showSnackBar("Code successfull","success");
                    showMainFragment();
                }

                @Override
                public void onFingerprintSuccessful() {
                    methods.showSnackBar("Fingerprint successfull","success");
                    showMainFragment();
                }

                @Override
                public void onPinLoginFailed() {
                    methods.showSnackBar("Pin failed","error");
                }

                @Override
                public void onFingerprintLoginFailed() {
                    methods.showSnackBar("Fingerprint failed","error");
                }
            };

    private void showMainFragment() {
        Intent main = new Intent(LockScreenActivity.this, MainActivity.class);
        startActivity(main);
        finish();
    }

    private void showLockScreenFragment() {
        new PFPinCodeViewModel().isPinCodeEncryptionKeyExist().observe(
                this,
                new Observer<Result<Boolean>>() {
                    @Override
                    public void onChanged(@Nullable Result<Boolean> result) {
                        if (result == null) {
                            return;
                        }
                        if (result.getError() != null) {
                            methods.showSnackBar("Can not get pin code info","error");
                            return;
                        }
                        showLockScreenFragment(result.getResult());
                    }
                }
        );
    }

    private void showLockScreenFragment(boolean isPinExist) {
        final LockScreenConfiguration.Builder builder = new LockScreenConfiguration.Builder(this)
                .setTitle(isPinExist ? "Unlock with your pin code or fingerprint" : "Create Code")
                .setCodeLength(4)
                .setLeftButton("Can't remeber")
                .setNewCodeValidation(true)
                .setNewCodeValidationTitle("Please input code again")
                .setUseFingerprint(true);
        final LockScreenFragment fragment = new LockScreenFragment();

        fragment.setOnLeftButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showSnackBar("Left button pressed","success");
            }
        });

        builder.setMode(isPinExist
                ? LockScreenConfiguration.MODE_AUTH
                : LockScreenConfiguration.MODE_CREATE);
        if (isPinExist) {
            fragment.setEncodedPinCode(sharedPre.getCode());
            fragment.setLoginListener(mLoginListener);
        }

        fragment.setConfiguration(builder.build());
        fragment.setCodeCreateListener(mCodeCreateListener);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_view, fragment).commit();

    }
}
