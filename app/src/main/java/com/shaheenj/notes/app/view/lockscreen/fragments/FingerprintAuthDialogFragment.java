package com.shaheenj.notes.app.view.lockscreen.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.fragment.app.DialogFragment;

import com.shaheenj.notes.app.R;

 
@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintAuthDialogFragment extends DialogFragment {

    private Button mCancelButton;
    private View mFingerprintContent;

    private Stage mStage = Stage.FINGERPRINT;

    private FingerprintManagerCompat.CryptoObject mCryptoObject;

    private FingerprintUIHelper mFingerprintCallback;

    private Context mContext;

    private FingerprintAuthListener mAuthListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(getString(R.string.sign_in_pf));
        View v = inflater.inflate(R.layout.view_pf_fingerprint_dialog_container, container,
                false);
        mCancelButton = v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mFingerprintContent = v.findViewById(R.id.fingerprint_container);


        FingerprintManagerCompat manager = FingerprintManagerCompat.from(getContext());
        mFingerprintCallback = new FingerprintUIHelper(manager,
                (ImageView) v.findViewById(R.id.fingerprint_icon),
                (TextView) v.findViewById(R.id.fingerprint_status),
                mAuthListener);
        updateStage();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mStage == Stage.FINGERPRINT) {
            mFingerprintCallback.startListening(mCryptoObject);
        }
    }

    public void setStage(Stage stage) {
        mStage = stage;
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintCallback.stopListening();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }



    private void updateStage() {
        switch (mStage) {
            case FINGERPRINT:
                mCancelButton.setText(R.string.cancel_pf);
                mFingerprintContent.setVisibility(View.VISIBLE);
                break;
        }
    }


    public void setAuthListener(FingerprintAuthListener authListener) {
        mAuthListener = authListener;
    }


    public enum Stage {
        FINGERPRINT
    }
}
