package com.shaheenj.notes.app.view.lockscreen;

import android.content.Context;

import androidx.annotation.IntDef;


import java.io.Serializable;
import java.lang.annotation.Retention;

import com.shaheenj.notes.app.R;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class LockScreenConfiguration implements Serializable {

    private String mLeftButton = "";
    private String mNextButton = "";
    private boolean mUseFingerprint = false;
    private boolean mAutoShowFingerprint = false;
    private String mTitle = "";
    private int mMode = MODE_AUTH;
    private int mCodeLength = 4;
    private boolean mClearCodeOnError = false;
    private boolean mErrorVibration = true;
    private boolean mErrorAnimation = true;
    private boolean mNewCodeValidation = false;
    private String mNewCodeValidationTitle = "";

    private LockScreenConfiguration(Builder builder) {
        mLeftButton = builder.mLeftButton;
        mNextButton = builder.mNextButton;
        mUseFingerprint = builder.mUseFingerprint;
        mAutoShowFingerprint = builder.mAutoShowFingerprint;
        mTitle = builder.mTitle;
        mMode = builder.mMode;
        mCodeLength = builder.mCodeLength;
        mClearCodeOnError = builder.mClearCodeOnError;
        mErrorVibration = builder.mErrorVibration;
        mErrorAnimation = builder.mErrorAnimation;
        mNewCodeValidation = builder.mNewCodeValidation;
        mNewCodeValidationTitle = builder.mNewCodeValidationTitle;
    }

    public String getLeftButton() {
        return mLeftButton;
    }

    public String getNextButton() {
        return mNextButton;
    }

    public boolean isUseFingerprint() {
        return mUseFingerprint;
    }

    public boolean isAutoShowFingerprint() {
        return mAutoShowFingerprint;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getCodeLength() {
        return mCodeLength;
    }

    public boolean isClearCodeOnError() {
        return mClearCodeOnError;
    }

    public boolean isErrorVibration() {
        return mErrorVibration;
    }

    public boolean isErrorAnimation() {
        return mErrorAnimation;
    }

    public boolean isNewCodeValidation() {
        return mNewCodeValidation;
    }

    public String getNewCodeValidationTitle() {
        return mNewCodeValidationTitle;
    }

    @LockScreenMode
    public int getMode() {
        return this.mMode;
    }

    public static class Builder {

        private String mLeftButton = "";
        private String mNextButton = "";
        private boolean mUseFingerprint = false;
        private boolean mAutoShowFingerprint = false;
        private String mTitle = "";
        private int mMode = 0;
        private int mCodeLength = 4;
        private boolean mClearCodeOnError = false;
        private boolean mErrorVibration = true;
        private boolean mErrorAnimation = true;
        private boolean mNewCodeValidation = false;
        private String mNewCodeValidationTitle = "";


        public Builder(Context context) {
            mTitle = context.getResources().getString(R.string.lock_screen_title_pf);
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setLeftButton(String leftButton) {
            mLeftButton = leftButton;
            return this;
        }

        public Builder setNextButton(String nextButton) {
            mNextButton = nextButton;
            return this;
        }

        public Builder setUseFingerprint(boolean useFingerprint) {
            mUseFingerprint = useFingerprint;
            return this;
        }

        public Builder setAutoShowFingerprint(boolean autoShowFingerprint) {
            mAutoShowFingerprint = autoShowFingerprint;
            return this;
        }

        public Builder setMode(@LockScreenMode int mode) {
            mMode = mode;
            return this;
        }

        public Builder setCodeLength(int codeLength) {
            this.mCodeLength = codeLength;
            return this;
        }

        public Builder setClearCodeOnError(boolean clearCodeOnError) {
            mClearCodeOnError = clearCodeOnError;
            return this;
        }

        public Builder setErrorVibration(boolean errorVibration) {
            mErrorVibration = errorVibration;
            return this;
        }

        public Builder setErrorAnimation(boolean errorAnimation) {
            mErrorAnimation = errorAnimation;
            return this;
        }

        public Builder setNewCodeValidation(boolean newCodeValidation) {
            this.mNewCodeValidation = newCodeValidation;
            return this;
        }

        public Builder setNewCodeValidationTitle(String newCodeValidationTitle) {
            this.mNewCodeValidationTitle = newCodeValidationTitle;
            return this;
        }

        public LockScreenConfiguration build() {
            return new LockScreenConfiguration(
                    this);
        }



    }

    @Retention(SOURCE)
    @IntDef({MODE_CREATE, MODE_AUTH})
    public @interface LockScreenMode {}
    public static final int MODE_CREATE = 0;
    public static final int MODE_AUTH = 1;

}
