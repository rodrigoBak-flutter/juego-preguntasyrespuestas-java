package com.Rodrigo.onlinepuzzles.InAppPurchase;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by Leenah on 10/11/2018.
 * leenah.apps@gmail.com
 */

public class Navigator {

    public static final int REQUEST_PASSPORT_PURCHASE = 2012;

    private final Activity activity;

    public Navigator(Activity activity) {
        this.activity = activity;
    }

    public void toPurchaseSKUActivityForResult() {
        Intent intent = new Intent(activity, PurchaseSKUActivity.class);
        activity.startActivityForResult(intent, REQUEST_PASSPORT_PURCHASE);
    }

}
