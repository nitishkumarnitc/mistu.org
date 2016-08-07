package com.example.nitish.mistuorg.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.example.nitish.mistuorg.R;

/**
 * Created by nitish on 30-07-2016.
 */
public class PDialog extends AppCompatActivity {

    private static ProgressDialog mProgressDialog;

    public static void showProgressDialog(Context context,String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public static void hideProgressDialog(Context context) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog(this);
    }

}
