package com.Rodrigo.onlinepuzzles.InAppPurchase;

import android.os.Bundle;

import com.Rodrigo.onlinepuzzles.android.vending.billing.util.IabResult;
import com.Rodrigo.onlinepuzzles.android.vending.billing.util.Purchase;
import com.Rodrigo.onlinepuzzles.R;


/**
 * Created by Leenah on 10/11/2018.
 * leenah.apps@gmail.com
 */

public class PurchaseSKUActivity extends PurchaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the result as cancelled in case anything fails before we purchase the item
        setResult(RESULT_CANCELED);
        // Then wait for the callback if we have successfully setup in app billing or not (because we extend PurchaseActivity)
    }

    @Override
    protected void dealWithIabSetupFailure() {
        popBurntToast(getString(R.string.sorry));
        finish();
    }

    @Override
    protected void dealWithIabSetupSuccess() {
        purchaseItem(SKU.SKU);
    }

    @Override
    protected void dealWithPurchaseSuccess(IabResult result, Purchase info) {
        super.dealWithPurchaseSuccess(result, info);
        setResult(RESULT_OK);
        finish();
    }


    @Override
    protected void dealWithPurchaseFailed(IabResult result) {
        super.dealWithPurchaseFailed(result);
        setResult(RESULT_CANCELED);
        finish();
    }

}
