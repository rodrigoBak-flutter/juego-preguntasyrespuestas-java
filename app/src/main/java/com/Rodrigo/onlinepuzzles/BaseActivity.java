package com.Rodrigo.onlinepuzzles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;

import androidx.annotation.LayoutRes;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.Rodrigo.onlinepuzzles.InAppPurchase.BlundellActivity;
import com.Rodrigo.onlinepuzzles.InAppPurchase.Navigator;
import com.google.ads.consent.ConsentInformation;

/**
 * Created by Leenah on 12/02/2020.
 * leenah.apps@gmail.com
 */


public class BaseActivity extends BlundellActivity implements NavigationView.OnNavigationItemSelectedListener {
    int code;
    SharedPreferences preferences;
    String order;
    SharedPreferences.Editor editor;
    private NavigationView navigationView;
    private DrawerLayout fullLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private int selectedNavItemId;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {

        /**
         * This is going to be our actual root layout.
         */
        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        /**
         * {@link FrameLayout} to inflate the child's view. We could also use a {@link android.view.ViewStub}
         */
        FrameLayout activityContainer = (FrameLayout) fullLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        /**
         * Note that we don'title pass the child's layoutId to the parent,
         * instead we pass it our inflated layout.
         */
        super.setContentView(fullLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        preferences = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        editor = preferences.edit();

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            applyFontToMenuItem(mi);
        }
        if (useToolbar()) {
            setSupportActionBar(toolbar);
        } else {
            toolbar.setVisibility(View.GONE);
        }
        //Enable / Disable Sounds Effects
        //=================================================================================//
        SwitchCompat drawerSwitch = (SwitchCompat) navigationView.getMenu().findItem(R.id.switch_item).getActionView();
        if (preferences.getBoolean("sounds", true) == true) {
            drawerSwitch.setChecked(true);
        } else if (preferences.getBoolean("sounds", true) == false) {
            drawerSwitch.setChecked(false);
        }
        drawerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("sounds", true);
                    editor.apply();
                } else {
                    editor.putBoolean("sounds", false);
                    editor.apply();

                }
            }
        });
        //=================================================================================//
        setUpNavView();
    }

    /**
     * Helper method that can be used by child classes to
     * specify that they don'title want a {@link Toolbar}
     *
     * @return true
     */
    protected boolean useToolbar() {
        return true;
    }

    protected void setUpNavView() {
        navigationView.setNavigationItemSelectedListener(this);

        if (useDrawerToggle()) { // use the hamburger menu
            drawerToggle = new ActionBarDrawerToggle(this, fullLayout, toolbar,
                    R.string.nav_drawer_opened,
                    R.string.nav_drawer_closed);

            fullLayout.setDrawerListener(drawerToggle);
            drawerToggle.syncState();
        } else if (useToolbar() && getSupportActionBar() != null) {
            // Use home/back button instead
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources()
                    .getDrawable(R.drawable.abc_ic_ab_back_material));
        }
    }

    /**
     * Helper method to allow child classes to opt-out of having the
     * hamburger menu.
     *
     * @return
     */
    protected boolean useDrawerToggle() {
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        fullLayout.closeDrawer(GravityCompat.START);
        selectedNavItemId = menuItem.getItemId();

        return onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Context context = this;
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.levels:
                Intent i = new Intent(this, CategoriesActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.goup, R.anim.godown);
                return true;

            case R.id.switch_item:
                return false;

            case R.id.leaderboard_item:
                Intent i2 = new Intent(this, LeaderBoardActivity.class);
                i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i2);
                overridePendingTransition(R.anim.goup, R.anim.godown);
                return true;


            case R.id.profile_item:
                Intent i5 = new Intent(this, ProfileActivity.class);
                startActivity(i5);
                return true;


            case R.id.remove_ads:
                String purchasedItem = preferences.getString("purchased", null);
                if (purchasedItem == null) {
                    navigate().toPurchaseSKUActivityForResult();
                } else {
                    Toast.makeText(this, getString(R.string.adsremovedone) + "", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.share:
                //share intent
                shareTextUrl();
                return true;

            case R.id.rate:
                String package_name = getPackageName();
                Intent r = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + package_name));
                startActivity(r);
                return true;


            case R.id.contact_us:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:" + getString(R.string.email));
                intent.setData(data);
                startActivity(intent);
                return true;
            case R.id.privacypolicy:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.gdpr_privacypolicy))));
                return true;
            case R.id.resetAds:
                adsSettings();
                return true;


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_container);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void shareTextUrl() {
        String package_name = getPackageName();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + ":  " + "https://play.google.com/store/apps/details?id=" + package_name);
        startActivity(Intent.createChooser(share, getString(R.string.share_title)));
    }

    private void applyFontToMenuItem(MenuItem mi) {
        String fontPath = "fonts/NeoSans.ttf";


        Typeface font = Typeface.createFromAsset(getAssets(), fontPath);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    /*******************************************/
    public void adsSettings() {
        ConsentInformation consentInformation = ConsentInformation.getInstance(this);
        consentInformation.reset();
        Intent i4 = new Intent(this, SplashActivity.class);
        i4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i4);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Navigator.REQUEST_PASSPORT_PURCHASE == requestCode) {
            if (RESULT_OK == resultCode) {
                dealWithSuccessfulPurchase();
            } else {
                dealWithFailedPurchase();
            }
        }
    }

    private void dealWithSuccessfulPurchase() {
        Toast.makeText(this, getString(R.string.adsremovedone) + "", Toast.LENGTH_SHORT).show();
        order = "Remove ads purchased";
        //store purchased
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("purchased", "yes");
        editor.apply();
        Intent i = new Intent(this, SplashActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }

    private void dealWithFailedPurchase() {
        Toast.makeText(this, getString(R.string.adsremovenot) + "", Toast.LENGTH_SHORT).show();
    }
}
