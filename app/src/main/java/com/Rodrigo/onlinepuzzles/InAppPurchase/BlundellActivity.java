package com.Rodrigo.onlinepuzzles.InAppPurchase;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Leenah on 10/11/2018.
 * leenah.apps@gmail.com
 */

public abstract class BlundellActivity extends AppCompatActivity {

    private Navigator navigator;
    private Toaster toaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navigator = new Navigator(this);
        toaster = new Toaster(this);
    }

    protected Navigator navigate() {
        return navigator;
    }

    protected void popBurntToast(String msg) {
        toaster.popBurntToast(msg);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        navigator = null;
        toaster = null;
    }
}
