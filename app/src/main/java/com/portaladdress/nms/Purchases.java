package com.portaladdress.nms;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

public class Purchases {

    private PurchasesUpdatedListener purchasesUpdatedListener;
    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    private BillingClient billingClient;
    private SkuDetails skuDetails = null;
    private boolean isConnected = false;
    private Context context;
    SharedPreferences sharedPreferences;

    public Purchases(Context context) throws NullPointerException {

        this.context = context;
        sharedPreferences = context.getSharedPreferences("NoAd", Context.MODE_PRIVATE);

        if(!sharedPreferences.contains("showAd")){
            sharedPreferences.edit().putBoolean("showAd",true).apply();
        }

        purchasesUpdatedListener = new PurchasesUpdatedListener() {

            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, List<Purchase> purchases) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && purchases != null) {
                    for (Purchase purchase : purchases) {
                        handlePurchase(purchase);
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                  //  Log.e("billingResultCan", "canceled");
                    sharedPreferences.edit().putBoolean("enableAd", true).apply();
                } else {

                   // Log.e("billingResultOther", billingResult.getResponseCode() + " " + billingResult.getDebugMessage());

                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                        sharedPreferences.edit().putBoolean("enableAd", false).apply();
                     //   Log.e("Item Purchases", "ITEM_ALREADY_OWNED");
                    } else {
                        sharedPreferences.edit().putBoolean("enableAd", true).apply();
                       // Log.e("Item Purchases", "ITEM_NOT_OWNED");
                    }

                }
            }
        };

        billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();


        startConnection();
    }

    private void handlePurchase(Purchase purchase) {

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {

            if (!purchase.isAcknowledged()) {

                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();

                acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                            sharedPreferences.edit().putBoolean("enableAd", false).apply();
                           // Log.e("Purchase", "ITEM_ALREADY_OWNED");
                        } else {
                            sharedPreferences.edit().putBoolean("enableAd", true).apply();
                          //  Log.e("Purchase", "ITEM_NOT_OWNED");
                        }
                    }
                };
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            } else {

              //  Log.e("Purchase", "Acknowledged");
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    sharedPreferences.edit().putBoolean("enableAd", false).apply();
                 //   Log.e("Purchase", "ITEM_ALREADY_OWNED");
                } else {
                //    Log.e("Purchase", "ITEM_NOT_OWNED");
                    sharedPreferences.edit().putBoolean("enableAd", true).apply();
                }
            }
        } else {
            sharedPreferences.edit().putBoolean("enableAd", true).apply();
          //  Log.e("Not Purchase", "ITEM_NOT_OWNED");
        }
    }


    private void startConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    isConnected = true;
                    getProducts();

                } else {
                    Log.e("billingResult", billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                isConnected = false;
            }
        });

    }

    private void getProducts() {
        List<String> skuList = new ArrayList<>();
        skuList.add("noad");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

        if (isConnected()) {

            billingClient.querySkuDetailsAsync(params.build(),
                    new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(BillingResult billingResult,
                                                         List<SkuDetails> skuDetailsList) {

                          //  Log.e("DetailsResponse", billingResult.getResponseCode()
                              //      + " " + billingResult.getDebugMessage());
                            try {
                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    skuDetails = skuDetailsList.get(0);

                                 //   Log.e("Products", "count " + skuDetailsList.size() + " " + skuDetails.getTitle()
                                        //    + " " + skuDetails.getDescription() + " " + skuDetails.getPrice());

                                    if (skuDetailsList.size() > 0)
                                        checkPurchases();

                                } else
                                    Log.e("DetailsResponse", billingResult.getResponseCode()
                                            + " " + billingResult.getDebugMessage());
                            } catch (IndexOutOfBoundsException exception) {
                                Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(context, "Could not connect to Google Play", Toast.LENGTH_LONG).show();
        }

    }


    public void checkPurchases() throws NullPointerException {
        List<String> skuList = new ArrayList<>();
        skuList.add("noad");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

        if (isConnected())
            billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, new PurchasesResponseListener() {
                @Override
                public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {

                    if (list.size() > 0) {
                        for (Purchase p : list)
                            handlePurchase(p);
                    } else {
                        sharedPreferences.edit().putBoolean("enableAd", true).apply();
                       // Log.e("Purchase", "Not item purchase");
                    }
                }
            });
    }

    private boolean isConnected() {

        if (!isConnected) {
            startConnection();
        }
      //  Log.e("connected", Boolean.toString(isConnected));
        return isConnected;

    }

    public void billingFlow() throws NullPointerException {

        int responseCode = -1;

        if (skuDetails != null) {

            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build();
            responseCode = billingClient.launchBillingFlow((Activity) context, billingFlowParams).getResponseCode();


        }
      //  Log.e("ResponseCode",Integer.toString(responseCode));

    }


}
