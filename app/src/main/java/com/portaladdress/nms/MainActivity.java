package com.portaladdress.nms;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsResult;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.common.collect.ImmutableList;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.portaladdress.nms.ui.main.SectionsPagerAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    public static InterstitialAd mInterstitialAd;
    private AdView mAdView;
    private boolean showAd = true;
    private BillingClient billingClient;
    private ProductDetails productDetails;
    private SharedPreferences sharedPreferences;
    final public static int PERMISSION_REQUEST_CODE = 42;

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
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        sharedPreferences = getSharedPreferences("noad", Context.MODE_PRIVATE);
        
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

      /*  int rated = getSharedPreferences("rated", MODE_PRIVATE).getInt("time", 0);
        getSharedPreferences("rated", MODE_PRIVATE).edit().putInt("time", rated + 1).commit();

        if (rated == 5) {
            showRequestRateApp(MainActivity.this);
        } else if (rated == 30) {
            getSharedPreferences("rated", MODE_PRIVATE).edit().putInt("time", 0).commit();
        }*/

        /*RequestConfiguration requestConfiguration = new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("CA2976559C9489A7C3F8367D5C73ABE6")).build();
        MobileAds.setRequestConfiguration(requestConfiguration);*/
        try {
            rateApp(MainActivity.this);
        } catch (Exception e) {
            Log.e("NMS","error rateApp()");
        }
        checkUpdate(MainActivity.this);

    }

    private void checkUpdate(Context context) {

        Activity activity = (Activity) context;

        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("checkUpdate" + e.getMessage());

            }
        });

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                appUpdateManager.startUpdateFlow(appUpdateInfo, activity, new AppUpdateOptions() {
                    @Override
                    public int appUpdateType() {
                        return AppUpdateType.IMMEDIATE;
                    }

                    @Override
                    public boolean allowAssetPackDeletion() {
                        return false;
                    }
                });
            } else {
                System.out.println("App is updated");

            }
        });
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {


        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                verifySubPurchase(purchase);
            }
        }
    }

    void establishConnection() {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                // Handle disconnection if necessary
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Query existing purchases and update premium status
                    showProducts();
                    billingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                            (billingResult1, list) -> {
                                if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    if (!list.isEmpty()) {
                                        // Activate premium feature
                                        sharedPreferences.edit().putBoolean("enableAd", false).apply();
                                        // Process each purchase if needed
                                    } else {
                                        // De-activate premium feature
                                        sharedPreferences.edit().putBoolean("enableAd", true).apply();
                                    }
                                }
                            }
                    );
                }
            }
        });


    }

    void verifySubPurchase(Purchase purchase) {
        // Acknowledge the purchase
        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                // Subscription activated, update UI or perform necessary actions
                Toast.makeText(MainActivity.this, "Subscription activated, Enjoy!", Toast.LENGTH_SHORT).show();
                sharedPreferences.edit().putBoolean("enableAd", false).apply(); // Set premium status to 1
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

    }

    void showProducts() {
        // Define your product details
        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(
                // Product 1
              /*  QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("one_week")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                // Product 2
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("one_month")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),*/
                // Product 3
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("removead1")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        // Query product details asynchronously
        billingClient.queryProductDetailsAsync(params, (billingResult, prodDetailsList) -> {

            productDetails = prodDetailsList.getProductDetailsList().get(0);


        });
    }


    void launchPurchaseFlow() {
        BillingFlowParams billingFlowParams =
                BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(
                                ImmutableList.of(
                                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                                .setProductDetails(productDetails)
                                                .build()
                                )
                        )
                        .build();

        billingClient.launchBillingFlow(this, billingFlowParams);
    }

    void checkSubscription() {
        billingClient  = BillingClient.newBuilder(getApplicationContext())
                .setListener(this)
                .enablePendingPurchases(PendingPurchasesParams.newBuilder()
                        .enableOneTimeProducts() // Enable pending purchases for one-time products
                        // .enablePrepaidPlans() // Optionally, enable pending purchases for prepaid plans
                        .build())
                .build();

        establishConnection();


    }


    private void loadAd() {
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
                        Log.i("InterstitialAd", "loaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                        Log.i("InterstitialAd", "Failed: " + loadAdError.toString());
                    }
                });

    }

    private static void interstitialAdListner() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
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

    public static void showInterstitial(Context context) {

        Activity activity = (Activity) context;
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
        }

    }

  /*  private void showRequestRateApp(final Activity activity) {
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
    }*/

    private void rateApp(final Activity activity) throws Exception {
        final ReviewManager reviewManager = ReviewManagerFactory.create(MainActivity.this);
        //reviewManager = new FakeReviewManager(this);
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();

        request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {

                if (task.isSuccessful()) {
                    ReviewInfo reviewInfo = task.getResult();
                    Task<Void> flow = reviewManager.launchReviewFlow(MainActivity.this, reviewInfo);

                    flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i("Rate Flow", "Result: "+task.isComplete());
                        }
                    });

                    flow.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.i("Rate Flow", "Fail");
                            System.err.println(e);
                        }
                    });

                } else {
                    try {
                        String reviewErrorCode = Objects.requireNonNull(task.getException()).getMessage();
                        Log.d("Rate Task Fail", "cause: " + reviewErrorCode);

                    } catch (NullPointerException | ClassCastException e) {
                        System.err.println(e.getMessage());

                    }
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                System.err.println(e.getMessage());
                Log.d("Rate Request", "Fail");

            }
        });

    }

    public void onResume() {
        super.onResume();
        checkSubscription();
        showAd = getSharedPreferences("noad", MODE_PRIVATE).getBoolean("enableAd", true);
        if (showAd) {
            loadAdInter(getApplicationContext());
            loadAd();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 2 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                } else {
                        AlertDialog alertDialog = null;
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        //builder.setTitle("Confirm exit");
                        builder.setTitle(R.string.app_name);
                        builder.setIcon(R.mipmap.ic_launcher_round);
                        builder.setMessage("The app needs you to grant permission to create an image.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);

                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        alertDialog = builder.create();
                        alertDialog.getWindow().setGravity(Gravity.CENTER);
                        alertDialog.show();

                }
                return;
        }

    }



    @Override
    public void onBackPressed(){
     // super.onBackPressed();
        super.onBackPressed();
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
                    rateAppOnPlayStore();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.menuPrivacy:
                intent = new Intent(getBaseContext(), PrivacyPolicyHelp.class);
                startActivity(intent);
                break;

            case R.id.menuRemoveAd:
                try {
                    launchPurchaseFlow();
                }catch (NullPointerException e){e.printStackTrace();}
                break;

            case R.id.menuHelp:
                intent = new Intent(getBaseContext(), HelpActivity.class);
                startActivity(intent);
                break;

        }
        return true;
    }


    private void rateAppOnPlayStore(){
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

   
}
