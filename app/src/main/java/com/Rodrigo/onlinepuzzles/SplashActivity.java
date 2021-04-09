package com.Rodrigo.onlinepuzzles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Created by Leenah on 12/02/2020.
 * leenah.apps@gmail.com
 */

public class SplashActivity extends Activity {
    ImageView imageView, animatedBackground;
    TextView title;
    Runnable runnable;
    FirebaseAnalytics mFirebaseAnalytics;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //TODO Remove these 2 lines before production
//        ConsentInformation.getInstance(getBaseContext()).addTestDevice("EE9F7ABAF8C9FA8B0E11163835C4C7A2");
//        ConsentInformation.getInstance(getBaseContext()).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        Firebase.setAndroidContext(this);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        imageView = (ImageView) findViewById(R.id.imageView);
        animatedBackground = (ImageView) findViewById(R.id.animated_background);
        title = (TextView) findViewById(R.id.title);

        //animated imageView
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        imageView.startAnimation(animation);

        // animate the background
        int currentRotation = 0;
        Animation anim = new RotateAnimation(currentRotation, (360 * 4), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(5000);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(Animation.RESTART);
        anim.setFillEnabled(true);
        anim.setFillAfter(true);
        animatedBackground.startAnimation(anim);

        String fontPath = "fonts/NeoSans.ttf";
        Typeface font = Typeface.createFromAsset(getAssets(), fontPath);
        title.setTypeface(font);

        final Handler handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isOnline()) {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        //To Remove categories activity
//                        Intent intent = new Intent(SplashActivity.this, LevelsActivity.class);
                        Intent intent = new Intent(SplashActivity.this, CategoriesActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                } else {

                    Toast.makeText(SplashActivity.this, getString(R.string.checkInternet) + "", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        };
        handler.postDelayed(runnable, 3000);


    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
