package com.portaladdress.nms;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.portaladdress.nms.ui.main.SectionsPagerAdapter;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static InterstitialAd mInterstitialAd;
    private FirebaseAnalytics mFirebaseAnalytics;
    private AdView mAdView;
    private Purchases purchases;
    private boolean showAd = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#ffffff"));
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        purchases = new Purchases(MainActivity.this);

        showAd = getSharedPreferences("NoAd",MODE_PRIVATE).getBoolean("showAd",true);
     /*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Glyphs glyphs = new Glyphs();
                String g = glyphs.getCoordsDetails("0125:007C:08FF:00D9");
                Log.e("portal:", g);

            }
        });*/

        int rated = getSharedPreferences("rated", MODE_PRIVATE).getInt("time", 0);
        getSharedPreferences("rated", MODE_PRIVATE).edit().putInt("time", rated + 1).commit();

        if (rated == 5) {
            showRequestRateApp(MainActivity.this);
        } else if (rated == 30) {
            getSharedPreferences("rated", MODE_PRIVATE).edit().putInt("time", 0).commit();
        }



        RequestConfiguration requestConfiguration = new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("D2EB3B911E931B5E53C800624D4648CB")).build();
        MobileAds.setRequestConfiguration(requestConfiguration);


    }
    private void loadAd(){
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
    public static void loadAdInter(Context context) {
        AdRequest adRequest = new AdRequest.Builder().build();
        String id = context.getString(R.string.inters_ad_unit_id);

        InterstitialAd.load(context, id, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        interstitialAdListner();
                        Log.i("InterstitialAd","loaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                        Log.i("InterstitialAd","Failed: "+ loadAdError.toString());
                    }
                });

    }
    private static void interstitialAdListner(){
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                mInterstitialAd = null;
                Log.d("TAG", "The ad was dismissed.");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when fullscreen content failed to show.
                mInterstitialAd = null;
                Log.d("TAG", "The ad failed to show.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                // Make sure to set your reference to null so you don't
                // show it a second time.
                mInterstitialAd = null;
                Log.d("TAG", "The ad was shown.");
            }
        });


    }

    public static void showInterstitial(Context context){

        Activity activity = (Activity) context;
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
        }

    }

    private void showRequestRateApp(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Feedback");
        builder.setMessage("Could you please qualify our app?");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    rateApp(activity);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    FirebaseCrashlytics.getInstance().recordException(exception);
                }
            }
        });
        builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                activity.getSharedPreferences("rated", MODE_PRIVATE).edit().putInt("time", 0).commit();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void rateApp(final Activity activity) throws Exception {
        final ReviewManager reviewManager = ReviewManagerFactory.create(activity);
        //reviewManager = new FakeReviewManager(this);
        com.google.android.play.core.tasks.Task<ReviewInfo> request = reviewManager.requestReviewFlow();

        request.addOnCompleteListener(new com.google.android.play.core.tasks.OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(com.google.android.play.core.tasks.Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    ReviewInfo reviewInfo = task.getResult();
                    com.google.android.play.core.tasks.Task<Void> flow = reviewManager.launchReviewFlow(activity, reviewInfo);
                    flow.addOnCompleteListener(new com.google.android.play.core.tasks.OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(com.google.android.play.core.tasks.Task<Void> task) {
                            Log.e("Rate Flow", "Complete");
                        }
                    });

                    flow.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            activity.getSharedPreferences("rated", MODE_PRIVATE).edit().putInt("time", 0).commit();
                            Log.e("Rate Flow", "Fail");
                            e.printStackTrace();
                        }
                    });

                } else {
                    activity.getSharedPreferences("rated", MODE_PRIVATE).edit().putInt("time", 0).commit();
                    Log.e("Rate Task", "Fail");
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                activity.getSharedPreferences("rated", MODE_PRIVATE).edit().putInt("time", 0).commit();
                e.printStackTrace();
                Log.e("Rate Request", "Fail");
            }
        });

    }

    public void onResume() {
        super.onResume();
        purchases = new Purchases(MainActivity.this);
        purchases.checkPurchases();
        showAd = getSharedPreferences("NoAd",MODE_PRIVATE).getBoolean("showAd",true);

        if(showAd) {
            loadAdInter(getApplicationContext());
            loadAd();
        }

    }

    public void onBackPressed(){
     // super.onBackPressed();
      confirmExit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void about(){
        String version = "v";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
             version += pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        final SpannableString s = new SpannableString(getString(R.string.app_name) +" "+version+'\n'+"Developer: Ezequiel A. Ribeiro"+'\n'+
                "Contact: http://is.gd/supportapp");
        Linkify.addLinks(s, Linkify.ALL);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(s)
                .setTitle("About");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        ((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

    }

    public void confirmExit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setMessage("Close the application ?")
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(1);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
        switch(item.getItemId()){
            case R.id.menuAbout:
                about();
                break;

            case R.id.menuRate:
                try {
                    rateApp(MainActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.menuPrivacy:
                intent = new Intent(getBaseContext(), PrivacyPolicyHelp.class);
                startActivity(intent);
                break;

            case R.id.menuRemoveAd:
                purchases.billingFlow();
                break;

            case R.id.menuHelp:
                intent = new Intent(getBaseContext(), HelpActivity.class);
                startActivity(intent);
                break;

        }
        return true;
    }


}
