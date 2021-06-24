package com.shaheenj.notes.app.view.lockscreen.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.shaheenj.notes.app.R;
import com.shaheenj.notes.app.view.lockscreen.LockScreenConfiguration;
import com.shaheenj.notes.app.view.lockscreen.security.Result;
import com.shaheenj.notes.app.view.lockscreen.viewmodels.PFPinCodeViewModel;
import com.shaheenj.notes.app.view.lockscreen.views.CodeView;

 
public class LockScreenFragment extends Fragment {

    private static final String TAG = LockScreenFragment.class.getName();

    private static final String FINGERPRINT_DIALOG_FRAGMENT_TAG = "FingerprintDialogFragment";

    private static final String INSTANCE_STATE_CONFIG
            = "com.shaheenj.notes.app.view.pflockscreen.instance_state_config";

    private View mFingerprintButton;
    private View mDeleteButton;
    private TextView mLeftButton;
    private Button mNextButton;
    private CodeView mCodeView;
    private TextView titleView;

    private boolean mUseFingerPrint = true;
    private boolean mFingerprintHardwareDetected = false;
    private boolean mIsCreateMode = false;

    private OnPFLockScreenCodeCreateListener mCodeCreateListener;
    private OnPFLockScreenLoginListener mLoginListener;

    private String mCode = "";
    private String mCodeValidation = "";
    private String mEncodedPinCode = "";

    private LockScreenConfiguration mConfiguration;
    private View mRootView;

    private final PFPinCodeViewModel mPFPinCodeViewModel = new PFPinCodeViewModel();

    private View.OnClickListener mOnLeftButtonClickListener = null;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(INSTANCE_STATE_CONFIG, mConfiguration);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_lock_screen_pf, container,
                false);

        if (mConfiguration == null) {
            mConfiguration = (LockScreenConfiguration) savedInstanceState.getSerializable(
                    INSTANCE_STATE_CONFIG
            );
        }

        mFingerprintButton = view.findViewById(R.id.button_finger_print);
        mDeleteButton = view.findViewById(R.id.button_delete);

        mLeftButton = view.findViewById(R.id.button_left);
        mNextButton = view.findViewById(R.id.button_next);

        mDeleteButton.setOnClickListener(mOnDeleteButtonClickListener);
        mDeleteButton.setOnLongClickListener(mOnDeleteButtonOnLongClickListener);
        mFingerprintButton.setOnClickListener(mOnFingerprintClickListener);

        mCodeView = view.findViewById(R.id.code_view);
        initKeyViews(view);

        mCodeView.setListener(mCodeListener);

        if (!mUseFingerPrint) {
            mFingerprintButton.setVisibility(View.GONE);
        }

        mFingerprintHardwareDetected = isFingerprintApiAvailable(getContext());

        mRootView = view;
        applyConfiguration(mConfiguration);

        return view;
    }

    @Override
    public void onStart() {
        if (!mIsCreateMode && mUseFingerPrint && mConfiguration.isAutoShowFingerprint() &&
                isFingerprintApiAvailable(getActivity()) && isFingerprintsExists(getActivity())) {
            mOnFingerprintClickListener.onClick(mFingerprintButton);
        }
        super.onStart();
    }

    public void setConfiguration(LockScreenConfiguration configuration) {
        this.mConfiguration = configuration;
        applyConfiguration(configuration);
    }

    private void applyConfiguration(LockScreenConfiguration configuration) {
        if (mRootView == null || configuration == null) {
            return;
        }
        titleView = mRootView.findViewById(R.id.title_text_view);
        titleView.setText(configuration.getTitle());
        if (TextUtils.isEmpty(configuration.getLeftButton())) {
            mLeftButton.setVisibility(View.GONE);
        } else {
            mLeftButton.setText(configuration.getLeftButton());
            mLeftButton.setOnClickListener(mOnLeftButtonClickListener);
        }

        if (!TextUtils.isEmpty(configuration.getNextButton())) {
            mNextButton.setText(configuration.getNextButton());
        }

        mUseFingerPrint = configuration.isUseFingerprint();
        if (!mUseFingerPrint) {
            mFingerprintButton.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.VISIBLE);
        }
        mIsCreateMode = mConfiguration.getMode() == LockScreenConfiguration.MODE_CREATE;

        if (mIsCreateMode) {
            mLeftButton.setVisibility(View.GONE);
            mFingerprintButton.setVisibility(View.GONE);
        }

        if (mIsCreateMode) {
            mNextButton.setOnClickListener(mOnNextButtonClickListener);
        } else {
            mNextButton.setOnClickListener(null);
        }

        mNextButton.setVisibility(View.INVISIBLE);
        mCodeView.setCodeLength(mConfiguration.getCodeLength());
    }

    private void initKeyViews(View parent) {
        parent.findViewById(R.id.button_0).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_1).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_2).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_3).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_4).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_5).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_6).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_7).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_8).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_9).setOnClickListener(mOnKeyClickListener);
    }

    private final View.OnClickListener mOnKeyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof TextView) {
                final String string = ((TextView) v).getText().toString();
                if (string.length() != 1) {
                    return;
                }
                final int codeLength = mCodeView.input(string);
                configureRightButton(codeLength);
            }
        }
    };

    private final View.OnClickListener mOnDeleteButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int codeLength = mCodeView.delete();
            configureRightButton(codeLength);
        }
    };

    private final View.OnLongClickListener mOnDeleteButtonOnLongClickListener
            = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            mCodeView.clearCode();
            configureRightButton(0);
            return true;
        }
    };

    private final View.OnClickListener mOnFingerprintClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                    !isFingerprintApiAvailable(getActivity())) {
                return;
            }


            if (!isFingerprintsExists(getActivity())) {
                showNoFingerprintDialog();
                return;
            }

            final FingerprintAuthDialogFragment fragment
                    = new FingerprintAuthDialogFragment();
            fragment.show(getFragmentManager(), FINGERPRINT_DIALOG_FRAGMENT_TAG);
            fragment.setAuthListener(new FingerprintAuthListener() {
                @Override
                public void onAuthenticated() {
                    if (mLoginListener != null) {
                        mLoginListener.onFingerprintSuccessful();
                    }
                    fragment.dismiss();
                }

                @Override
                public void onError() {
                    if (mLoginListener != null) {
                        mLoginListener.onFingerprintLoginFailed();
                    }
                }
            });
        }
    };

    private void configureRightButton(int codeLength) {
        if (mIsCreateMode) {
            if (codeLength > 0) {
                mDeleteButton.setVisibility(View.VISIBLE);
            } else {
                mDeleteButton.setVisibility(View.GONE);
            }
            return;
        }

        if (codeLength > 0) {
            mFingerprintButton.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.VISIBLE);
            mDeleteButton.setEnabled(true);
            return;
        }

        if (mUseFingerPrint && mFingerprintHardwareDetected) {
            mFingerprintButton.setVisibility(View.VISIBLE);
            mDeleteButton.setVisibility(View.GONE);
        } else {
            mFingerprintButton.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.VISIBLE);
        }

        mDeleteButton.setEnabled(false);

    }


    private boolean isFingerprintApiAvailable(Context context) {
        return FingerprintManagerCompat.from(context).isHardwareDetected();
    }


    private boolean isFingerprintsExists(Context context) {
        return FingerprintManagerCompat.from(context).hasEnrolledFingerprints();
    }


    private void showNoFingerprintDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.no_fingerprints_title_pf)
                .setMessage(R.string.no_fingerprints_message_pf)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel_pf, null)
                .setPositiveButton(R.string.settings_pf, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(
                                new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS)
                        );
                    }
                }).create().show();
    }


    private final CodeView.OnPFCodeListener mCodeListener = new CodeView.OnPFCodeListener() {
        @Override
        public void onCodeCompleted(String code) {
            if (mIsCreateMode) {
                mNextButton.setVisibility(View.VISIBLE);
                mCode = code;
                return;
            }
            mCode = code;
            mPFPinCodeViewModel.checkPin(getContext(), mEncodedPinCode, mCode).observe(
                    LockScreenFragment.this,
                    new Observer<Result<Boolean>>() {
                        @Override
                        public void onChanged(@Nullable Result<Boolean> result) {
                            if (result == null) {
                                return;
                            }
                            if (result.getError() != null) {
                                return;
                            }
                            final boolean isCorrect = result.getResult();
                            if (mLoginListener != null) {
                                if (isCorrect) {
                                    mLoginListener.onCodeInputSuccessful();
                                } else {
                                    mLoginListener.onPinLoginFailed();
                                    errorAction();
                                }
                            }
                            if (!isCorrect && mConfiguration.isClearCodeOnError()) {
                                mCodeView.clearCode();
                            }
                        }
                    });

        }

        @Override
        public void onCodeNotCompleted(String code) {
            if (mIsCreateMode) {
                mNextButton.setVisibility(View.INVISIBLE);
                return;
            }
        }
    };


    private final View.OnClickListener mOnNextButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mConfiguration.isNewCodeValidation() && TextUtils.isEmpty(mCodeValidation)) {
                mCodeValidation = mCode;
                cleanCode();
                titleView.setText(mConfiguration.getNewCodeValidationTitle());
                return;
            }
            if (mConfiguration.isNewCodeValidation() && !TextUtils.isEmpty(mCodeValidation) && !mCode.equals(mCodeValidation)) {
                mCodeCreateListener.onNewCodeValidationFailed();
                titleView.setText(mConfiguration.getTitle());
                cleanCode();
                return;
            }
            mCodeValidation = "";
            mPFPinCodeViewModel.encodePin(getContext(), mCode).observe(LockScreenFragment.this, new Observer<Result<String>>() {
                        @Override
                        public void onChanged(@Nullable Result<String> result) {
                            if (result == null) {
                                return;
                            }
                            if (result.getError() != null) {
                                Log.d(TAG, "Can not encode pin code");
                                deleteEncodeKey();
                                return;
                            }
                            final String encodedCode = result.getResult();
                            if (mCodeCreateListener != null) {
                                mCodeCreateListener.onCodeCreated(encodedCode);
                            }
                        }
                    }
            );
        }
    };

    private void cleanCode() {
        mCode = "";
        mCodeView.clearCode();
    }


    private void deleteEncodeKey() {
        mPFPinCodeViewModel.delete().observe(
                this,
                new Observer<Result<Boolean>>() {
                    @Override
                    public void onChanged(@Nullable Result<Boolean> result) {
                        if (result == null) {
                            return;
                        }
                        if (result.getError() != null) {
                            Log.d(TAG, "Can not delete the alias");
                            return;
                        }

                    }
                }
        );
    }

    private void errorAction() {
        if (mConfiguration.isErrorVibration()) {
            final Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                v.vibrate(400);
            }
        }

        if (mConfiguration.isErrorAnimation()) {
            final Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_pf);
            mCodeView.startAnimation(animShake);
        }
    }

    public void setOnLeftButtonClickListener(View.OnClickListener onLeftButtonClickListener) {
        this.mOnLeftButtonClickListener = onLeftButtonClickListener;
    }


  
    public void setCodeCreateListener(OnPFLockScreenCodeCreateListener listener) {
        mCodeCreateListener = listener;
    }

    
    public void setLoginListener(OnPFLockScreenLoginListener listener) {
        mLoginListener = listener;
    }

   
    public void setEncodedPinCode(String encodedPinCode) {
        mEncodedPinCode = encodedPinCode;
    }


    
    public interface OnPFLockScreenCodeCreateListener {

    
        void onCodeCreated(String encodedCode);

       
        void onNewCodeValidationFailed();

    }


    
    public interface OnPFLockScreenLoginListener {

       
        void onCodeInputSuccessful();

      
        void onFingerprintSuccessful();

      
        void onPinLoginFailed();
 
        void onFingerprintLoginFailed();

    }
}