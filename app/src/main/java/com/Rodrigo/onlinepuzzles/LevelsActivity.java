package com.Rodrigo.onlinepuzzles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Leenah on 12/02/2020.
 * leenah.apps@gmail.com
 */


public class LevelsActivity extends BaseActivity {
    public List<String> Levels;
    //    InterstitialAd mInterstitialAd;
    SharedPreferences preferences;
    LevelAdapter levelAdapter;
    AdView mAdView;
    int code;
    GridView gridview;
    static int categoriesLevels;
    Float percentage;
    ProgressBar progressBar;
    TextView scoreTextView;
    TextView titleTextView;
    static int NumberOfQuestions;
    SharedPreferences.Editor editor;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            code = extras.getInt("code");
        }
        //To Remove categories activity
//        code = 1;

        preferences = PreferenceManager.getDefaultSharedPreferences(LevelsActivity.this);
        editor = preferences.edit();

//        retrieveQuestionTile_LevelsNumber();


        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);


        }

        progressBar = (ProgressBar) toolbar.findViewById(R.id.progress);
        scoreTextView = (TextView) toolbar.findViewById(R.id.score);
        titleTextView = (TextView) toolbar.findViewById(R.id.title);
        int progress = preferences.getInt("CurrentQuestion" + code, 0);
        if (preferences.getInt("question_total" + code, 0) > 0) {
            percentage = ((preferences.getInt("total" + code, 0) / Float.parseFloat(String.valueOf(preferences.getInt("question_total" + code, 0)))) * Float.parseFloat(String.valueOf(100)));

        } else {
            percentage = Float.valueOf(0);
        }
        if (preferences.getInt("question_total" + code, 0) > 0) {
            progressBar.setVisibility(View.VISIBLE);
            scoreTextView.setVisibility(View.VISIBLE);
            progressBar.setMax(preferences.getInt("question_total" + code, 0));
            progressBar.setProgress(progress);
            scoreTextView.setText(String.format(Locale.ENGLISH, "%.2f", percentage) + "%");
        } else {
            progressBar.setProgress(0);
            scoreTextView.setText("0.00%");
        }

//        Toast.makeText(this, progress+"->"+percentage+"->>"+preferences.getInt("question_total" + code, 0), Toast.LENGTH_SHORT).show();

        String purchasedItem = preferences.getString("purchased", null);
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
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_unit_id));
//        requestNewInterstitial();
//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                // Load the next interstitial.
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
//            }
//        });


    }

//    private void requestNewInterstitial() {
//        if (preferences.getString("ads", "").equals("p")) {
//            AdRequest adRequest = new AdRequest.Builder()
//                    .build();
//            mInterstitialAd.loadAd(adRequest);
//        } else if (preferences.getString("ads", "").equals("n")) {
//            AdRequest adRequest = new AdRequest
//                    .Builder()
//                    .addNetworkExtrasBundle(AdMobAdapter.class, getNonPersonalizedAdsBundle())
//                    .build();
//            mInterstitialAd.loadAd(adRequest);
//        }
//
//    }

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

    public Bundle getNonPersonalizedAdsBundle() {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        return extras;
    }

    /*******************************************************************************************/


    public class LevelAdapter extends BaseAdapter {
        public List<Integer> mProgressIds;
        private LayoutInflater mInflater;

        public LevelAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return Levels.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.level_item, null);
                holder.levelNumberTextView = (TextView) convertView.findViewById(R.id.levelItem);
                holder.lockImageView = (ImageView) convertView.findViewById(R.id.lock_imageView);
                holder.isDoneImageView = (ImageView) convertView.findViewById(R.id.isDone);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //detect if the level is completed
            if (preferences.getString("done" + "_" + code + "_" + Integer.parseInt(String.valueOf(position + 1)), null) != null && preferences.getString("done" + "_" + code + "_" + Integer.parseInt(String.valueOf(position + 1)), null).equals("yes")) {
                holder.isDoneImageView.setVisibility(View.VISIBLE);
            } else {
                holder.isDoneImageView.setVisibility(View.GONE);

            }
            if (position > 0) {
                //detect if level opened
                //if (preferences.getInt("CurrentQuestion" + Integer.parseInt(String.valueOf(position)), 0) == preferences.getInt("question_total" + Integer.parseInt(String.valueOf(position)), 0) && preferences.getInt("CurrentQuestion" + Integer.parseInt(String.valueOf(position)), 0) > 0) {
                if (preferences.getString("done" + "_" + code + "_" + position, null) != null && preferences.getString("done" + "_" + code + "_" + position, null).equals("yes")) {
                    holder.levelNumberTextView.setText(position + 1 + "");
//                    holder.levelNumberTextView.setBackgroundResource(R.drawable.level_number_background);
                    holder.lockImageView.setVisibility(View.GONE);
                    holder.levelNumberTextView.setVisibility(View.VISIBLE);

                } else {
//                    holder.levelNumberTextView.setBackgroundResource(R.drawable.lock);
                    holder.levelNumberTextView.setText("");
                    holder.lockImageView.setVisibility(View.VISIBLE);
//                    holder.levelNumberTextView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    holder.levelNumberTextView.setVisibility(View.GONE);


                }
            } else {
                holder.levelNumberTextView.setText(position + 1 + "");
                holder.lockImageView.setVisibility(View.GONE);
                holder.levelNumberTextView.setVisibility(View.VISIBLE);


            }
            return convertView;
        }
    }

    class ViewHolder {
        TextView levelNumberTextView;
        ImageView lockImageView, isDoneImageView;
    }

    public void retrieveQuestionTile_LevelsNumber() {

        if (isOnline()) {
            //get total questions number in the category
            if (preferences.getInt("question_total" + code, 0) == 0) {
                Query query2 = FirebaseDatabase.getInstance().getReference("data").child(String.valueOf(code)).orderByKey();
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (int i = 0; i < dataSnapshot.getChildrenCount(); i++) {

                            NumberOfQuestions = Integer.parseInt(String.valueOf(NumberOfQuestions)) + Integer.parseInt(String.valueOf(dataSnapshot.child(String.valueOf(i + 1)).getChildrenCount()));


                        }

                        editor.putInt("question_total" + code, NumberOfQuestions);
                        editor.apply();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }

            //retrieve categories data

            Query query = FirebaseDatabase.getInstance().getReference("categories").orderByKey();
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(String.valueOf(code)).child("n").getValue() != null) {
                        titleTextView.setText(dataSnapshot.child(String.valueOf(code)).child("n").getValue() + "");
                    } else {
                        titleTextView.setText(getString(R.string.level) + " " + code);
                    }

                    ///get number of levels in the category//
                    Query query2 = FirebaseDatabase.getInstance().getReference("data").child(String.valueOf(code)).orderByKey();
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            categoriesLevels = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                            //Prepare levels
                            levelAdapter = new LevelAdapter();
                            Levels = new ArrayList<>();
                            for (int i = 1; i <= categoriesLevels; i++) {
                                Levels.add(String.valueOf(i));
                            }
                            gridview = (GridView) findViewById(R.id.gridview);
                            if (levelAdapter.getCount() > 0) {
                                gridview.setAdapter(levelAdapter);


                            }

                            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                    int Branches_num = position;
                                    if (Branches_num == 0) {
                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        i.putExtra("branch", position + 1);
                                        i.putExtra("code", code);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.goup, R.anim.godown);

//                                        String purchasedItem = preferences.getString("purchased", null);
//                                        if (purchasedItem == null) {
//                                            if (mInterstitialAd.isLoaded()) {
//                                                mInterstitialAd.show();
//                                                requestNewInterstitial();
//                                            }
//                                        }
                                    } else {
                                        if (preferences.getString("done" + "_" + code + "_" + Branches_num, null) != null && preferences.getString("done" + "_" + code + "_" + Branches_num, null).equals("yes")) {
                                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                            i.putExtra("branch", position + 1);
                                            i.putExtra("code", code);
                                            startActivity(i);
                                            overridePendingTransition(R.anim.goup, R.anim.godown);
//                                            String purchasedItem = preferences.getString("purchased", null);
//                                            if (purchasedItem == null) {
//                                                if (mInterstitialAd.isLoaded()) {
//                                                    mInterstitialAd.show();
//                                                    requestNewInterstitial();
//                                                }
//                                            }
                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        } else {
            Snackbar.make(gridview, getString(R.string.checkInternet), Snackbar.LENGTH_SHORT).show();
            finish();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveQuestionTile_LevelsNumber();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LevelsActivity.this, CategoriesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.goup, R.anim.godown);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                Intent i = new Intent(getApplicationContext(), CategoriesActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.goup, R.anim.godown);


                break;
        }
        return super.onOptionsItemSelected(item);
    }
}