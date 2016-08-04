package com.example.nitish.mistuorg.utils;

import android.support.v7.app.AppCompatActivity;

import com.example.nitish.mistuorg.R;

/**
 * Created by nitish on 30-07-2016.
 */
public class ProgressDialog extends AppCompatActivity {

    private android.app.ProgressDialog mProgressDialog;

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new android.app.ProgressDialog(this);
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
