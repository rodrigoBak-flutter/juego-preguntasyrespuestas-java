package com.Rodrigo.onlinepuzzles;


import androidx.annotation.Keep;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.database.IgnoreExtraProperties;
/**
 * Created by Leenah on 12/02/2020.
 * leenah.apps@gmail.com
 */
@IgnoreExtraProperties
@Keep
public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

    }
}