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
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Purchases {

    private PurchasesUpdatedListener purchasesUpdatedListener;
    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    private BillingClient billingClient;
    private boolean isConnected = false;
    private Context context;
    private  QueryProductDetailsParams queryProductDetailsParams;
    private BillingFlowParams billingFlowParams;
    SharedPreferences sharedPreferences;

    public Purchases(Context context) throws NullPointerException {

        this.context = context;
        sharedPreferences = context.getSharedPreferences("NoAd", Context.MODE_PRIVATE);

        if(!sharedPreferences.contains("enableAd")){
            sharedPreferences.edit().putBoolean("enableAd",true).apply();
        }

        purchasesUpdatedListener = new PurchasesUpdatedListener() {

            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, List<Purchase> purchases) {
               //  Log.e("code",""+billingResult.getResponseCode());
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && purchases != null) {
                    for (Purchase purchase : purchases) {
                        handlePurchase(purchase);
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                  //  Log.e("billingResultCan", "canceled");
                    sharedPreferences.edit().putBoolean("enableAd", true).apply();
                } else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                    sharedPreferences.edit().putBoolean("enableAd", false).apply();
                     //   Log.e("Item Purchases", "ITEM_ALREADY_OWNED");
                }
                else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_NOT_OWNED){
                    sharedPreferences.edit().putBoolean("enableAd", true).apply();
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
                    Log.e("billingResult","Code: "+ billingResult.getResponseCode() + ". Msg: " + billingResult.getDebugMessage());

                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                isConnected = false;
            }
        });

    }

    private void getProducts() {

       queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId("noadnms")
                                                .setProductType(BillingClient.ProductType.INAPP)
                                                .build()))
                        .build();

        if (isConnected()) {

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult,
                                                         List<ProductDetails> productDetailsList) {
                        try {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                                for(ProductDetails p: productDetailsList){
                                     checkPurchases(p);
                                }
                            } else
                                Log.e("DetailsResponse", billingResult.getResponseCode()
                                        + " " + billingResult.getDebugMessage());
                        } catch (IndexOutOfBoundsException exception) {
                            Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
        } else {
            Toast.makeText(context, "Could not connect to Google Play", Toast.LENGTH_LONG).show();
        }

    }


    public void checkPurchases(ProductDetails productDetails) throws NullPointerException {

        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                .setProductDetails(productDetails)
                                // to get an offer token, call ProductDetails.getSubscriptionOfferDetails()
                                // for a list of offers that are available to the user
                                //.setOfferToken()
                                .build()
                );

        billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

    }

    private boolean isConnected() {

        if (!isConnected) {
            startConnection();
        }
      //  Log.e("connected", Boolean.toString(isConnected));
        return isConnected;

    }

    public void billingFlow() throws NullPointerException {

            Activity activity = (Activity) context;
            BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);


    }


}
