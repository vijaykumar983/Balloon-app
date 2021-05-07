package com.balloon.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.balloon.R;


public class ProgressDialog {
    private static Dialog dialog = null;

    public static void showProgressDialog(Activity context) {
        if (dialog == null)
            dialog = new Dialog(context);
        dialog.setContentView(R.layout.fullscreen_loading);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void hideProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
