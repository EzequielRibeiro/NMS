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
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.google.common.collect.ImmutableList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Purchases{

    private PurchasesUpdatedListener purchasesUpdatedListener;
    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    private BillingClient billingClient;
    private Context context;
    SharedPreferences sharedPreferences;
    private ProductDetails productDetails;
    private String token = "";

    public Purchases(Context context) throws NullPointerException {

        this.context = context;
        sharedPreferences = context.getSharedPreferences("noad", Context.MODE_PRIVATE);

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

                //    Log.e("billingResultOther", billingResult.getResponseCode() + " " + billingResult.getDebugMessage());

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
        token = purchase.getPurchaseToken();
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
                           // Log.e("Purchase", "ITEM_NOT_OWNED");
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
                     getProducts();

                } else {
                    Log.e("billingResult", billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                startConnection();
            }
        });

    }

    private void getProducts() {

        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(QueryProductDetailsParams.Product.newBuilder()
                .setProductId("removead1")
                .setProductType(BillingClient.ProductType.INAPP)
                .build());

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(
                params,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult, List<ProductDetails> productDetailsList) {
                        try {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                for(ProductDetails s : productDetailsList){
                                    if(s.getProductId().equals("removead1"))
                                        productDetails = s;
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


    }


    public void checkPurchases() throws NullPointerException {
        List<String> skuList = new ArrayList<>();
        skuList.add("noad");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

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


    public void billingFlow() throws NullPointerException {
Log.e("token",":"+token);
// Set the parameters for the offer that will be presented
// in the billing flow creating separate productDetailsParamsList variable
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                              //  .setOfferToken(token)
                                .build()
                );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();


        BillingResult billingResult = billingClient.launchBillingFlow((Activity) context, billingFlowParams);

    }


}
