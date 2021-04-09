package com.Rodrigo.onlinepuzzles.InAppPurchase;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Leenah on 10/11/2018.
 * leenah.apps@gmail.com
 */
public class Toaster {

    private final Context context;

    public Toaster(Context context) {
        this.context = context;
    }

    public void popBurntToast(String msg) {
        makeToast(msg, Toast.LENGTH_LONG).show();
    }

    private Toast makeToast(String msg, int length) {
        return Toast.makeText(context, msg, length);
    }
}
