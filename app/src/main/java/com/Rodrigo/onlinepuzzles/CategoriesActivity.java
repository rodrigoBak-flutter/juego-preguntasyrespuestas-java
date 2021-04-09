package com.Rodrigo.onlinepuzzles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


/**
 * Created by Leenah on 12/02/2020.
 * leenah.apps@gmail.com
 */

public class CategoriesActivity extends BaseActivity {
    private static final String TAG = "AdMob ads consent";
    public List<String> Levels;
    static InterstitialAd mInterstitialAd;
    static SharedPreferences preferences;
    ConsentForm form;
    AdView mAdView;
    FirebaseUser user;
    String userName;
    RecyclerView recyclerViewCategories;
    private GridLayoutManager mLayoutManager;
    Query query;
    static Typeface typeface;
    String fontPath = "fonts/NeoSans.ttf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //To Remove categories activity
//        Intent i2 = new Intent(this, LevelsActivity.class);
//        startActivity(i2);
//        finish();
//        overridePendingTransition(R.anim.goup, R.anim.godown);

        setContentView(R.layout.activity_categories);
        preferences = PreferenceManager.getDefaultSharedPreferences(CategoriesActivity.this);

        recyclerViewCategories = (RecyclerView) findViewById(R.id.gridview);

        mLayoutManager = new GridLayoutManager(this, 2);

        //Change the number of columns for tablets
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches >= 6.5) {
            mLayoutManager = new GridLayoutManager(this, 4);

        }

        recyclerViewCategories.setLayoutManager(mLayoutManager);


        //**Ads EU conset**//
        String purchasedItem = preferences.getString("purchased", null);
        if (purchasedItem == null) {
            checkForConsent();
        }

        //AdMob Ads
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mAdView = findViewById(R.id.adView);
        if (purchasedItem == null) {
            if (preferences.getString("ads", "").equals("p")) {
                showPersonalizedAds();
            } else if (preferences.getString("ads", "").equals("n")) {
                showNonPersonalizedAds();
            }
        } else {
            mAdView.setVisibility(View.GONE);
        }
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_unit_id));
        requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            userName = extras.getString("userName");
        }

        // insert user name to the user object
        if (isOnline()) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && user.getDisplayName() == null) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(userName).build();
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Firebase", "User profile updated.");
                                }
                            }
                        });
            }
        } else {
            Toast.makeText(this, getString(R.string.checkInternet) + "", Toast.LENGTH_SHORT).show();
        }


        typeface = Typeface.createFromAsset(getAssets(), fontPath);
        /////////////////////////////////////////////////////
        query = FirebaseDatabase.getInstance().getReference("categories").orderByKey();


        final FirebaseRecyclerAdapter<CategoriesModelClass, CategoriesViewHolder> firebaseRecyclerAdapter2 =
                new FirebaseRecyclerAdapter<CategoriesModelClass, CategoriesViewHolder>(
                        CategoriesModelClass.class,
                        R.layout.category_item,
                        CategoriesViewHolder.class,
                        query) {

                    @Override
                    protected void populateViewHolder(CategoriesViewHolder viewHolder, CategoriesModelClass model, int position) {

                        viewHolder.setIsRecyclable(false);
                        viewHolder.setImage(getApplicationContext(), model.getImage());
                        viewHolder.setName(model.getName());

                    }

                    ///////For descending order////////
                    @Override
                    public CategoriesModelClass getItem(int position) {
                        return super.getItem(position);
                    }
                    /////////////////////////////////
                };

        recyclerViewCategories.setAdapter(firebaseRecyclerAdapter2);

        firebaseRecyclerAdapter2.notifyDataSetChanged();
        ////////////////
//Check for empty data
        query.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    firebaseRecyclerAdapter2.notifyDataSetChanged();
                } else {//is empty
                    Toast.makeText(CategoriesActivity.this, getString(R.string.nodata) + "", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Check for updating data
        query.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {
                firebaseRecyclerAdapter2.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {
                firebaseRecyclerAdapter2.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                firebaseRecyclerAdapter2.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

        /////////////////////////////////////////////////////
    }

    private static void requestNewInterstitial() {
        if (preferences.getString("ads", "").equals("p")) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mInterstitialAd.loadAd(adRequest);
        } else if (preferences.getString("ads", "").equals("n")) {
            AdRequest adRequest = new AdRequest
                    .Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, getNonPersonalizedAdsBundle())
                    .build();
            mInterstitialAd.loadAd(adRequest);
        }

    }

    /******************For Customize ads according to user choice - GDPR*************************/
    private void showPersonalizedAds() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
    }

    private void showNonPersonalizedAds() {
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, getNonPersonalizedAdsBundle())
                .build();
        mAdView.loadAd(adRequest);
    }

    public static Bundle getNonPersonalizedAdsBundle() {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        return extras;
    }
    /*******************************************************************************************/

    /******************GDPR*************************/
    private void checkForConsent() {
        ConsentInformation consentInformation = ConsentInformation.getInstance(CategoriesActivity.this);
        String[] publisherIds = {getString(R.string.pub_id)};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                switch (consentStatus) {
                    case PERSONALIZED:
                        Log.d(TAG, "Showing Personalized ads");
                        PrefPersonalizedAds();
                        break;
                    case NON_PERSONALIZED:
                        Log.d(TAG, "Showing Non-Personalized ads");
                        PrefNonPersonalizedAds();
                        break;
                    case UNKNOWN:
                        Log.d(TAG, "Requesting Consent");
                        if (ConsentInformation.getInstance(getBaseContext()).isRequestLocationInEeaOrUnknown()) {
                            requestConsent();
                        } else {
                            PrefPersonalizedAds();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });
    }

    private void requestConsent() {
        URL privacyUrl = null;
        try {
            privacyUrl = new URL(getString(R.string.gdpr_privacypolicy));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }
        form = new ConsentForm.Builder(CategoriesActivity.this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.
                        Log.d(TAG, "Requesting Consent: onConsentFormLoaded");
                        if (!((CategoriesActivity.this).isFinishing())) {
                            showForm();
                        }
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                        Log.d(TAG, "Requesting Consent: onConsentFormOpened");
                    }

                    @Override
                    public void onConsentFormClosed(
                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Log.d(TAG, "Requesting Consent: onConsentFormClosed");
                        if (userPrefersAdFree) {
                            // Buy or Subscribe
                            Log.d(TAG, "Requesting Consent: User prefers AdFree");
                            finish();

                            String purchasedItem = preferences.getString("purchased", null);
                            if (purchasedItem == null) {
                                navigate().toPurchaseSKUActivityForResult();
                            } else {
                                Toast.makeText(CategoriesActivity.this, getString(R.string.adsremovedone), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "Requesting Consent: Requesting consent again");
                            switch (consentStatus) {
                                case PERSONALIZED:
                                    PrefPersonalizedAds();
                                    break;
                                case NON_PERSONALIZED:
                                    PrefNonPersonalizedAds();
                                    break;
                                case UNKNOWN:
                                    PrefNonPersonalizedAds();
                                    break;
                            }

                        }
                        // Consent form was closed.
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.d(TAG, "Requesting Consent: onConsentFormError. c - " + errorDescription);
                        // Consent form error.
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .withAdFreeOption()
                .build();
        form.load();
    }

    private void showForm() {
        if (form == null) {
            Log.d(TAG, "Consent form is null");
        }
        if (form != null) {
            Log.d(TAG, "Showing consent form");
            form.show();
        } else {
            Log.d(TAG, "Not Showing consent form");
        }
    }

    private void PrefPersonalizedAds() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ads", "p");
        editor.apply();
    }

    private void PrefNonPersonalizedAds() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ads", "n");
        editor.apply();
    }

    /*******************************************/

//    @Override
//    protected void onResume() {
//        super.onResume();
//        levelAdapter.notifyDataSetChanged();
//    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    //View Holder For Recycler View
    public static class CategoriesViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public CategoriesViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    ConnectivityManager cm = (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                        Intent i = new Intent(v.getContext().getApplicationContext(), LevelsActivity.class);
                        i.putExtra("code", getAdapterPosition() + 1);
                        v.getContext().startActivity(i);
//                            overridePendingTransition(R.anim.goup, R.anim.godown);
                        String purchasedItem = preferences.getString("purchased", null);
                        if (purchasedItem == null) {
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                                requestNewInterstitial();
                            }
                        }

//                        }
                    } else {
                        Toast.makeText(v.getContext(), v.getContext().getString(R.string.checkInternet) + "", Toast.LENGTH_SHORT).show();
                    }

                }

            });
        }

        public void setImage(Context ctx, final String i) {
            final ImageView post_image = (ImageView) mView.findViewById(R.id.levelItem);
            Glide.with(ctx).asBitmap().load(i).into(post_image);

        }

        public void setName(final String n) {
            final TextView textView = (TextView) mView.findViewById(R.id.levelName);
            textView.setText(n);
            textView.setTypeface(typeface);


        }


    }


}
