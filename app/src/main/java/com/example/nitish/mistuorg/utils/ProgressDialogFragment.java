package com.example.nitish.mistuorg.utils;

import android.app.Fragment;
import android.support.annotation.VisibleForTesting;

/**
 * Created by nitish on 09-08-2016.
 */
public class ProgressDialogFragment extends Fragment {

    @VisibleForTesting
    public android.app.ProgressDialog mProgressDialog;

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new android.app.ProgressDialog(getActivity());
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
