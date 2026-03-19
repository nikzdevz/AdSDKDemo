package com.example.adsdkdemo;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;

/**
 * Application class that initializes SDKs that need early initialization.
 */
public class AdSDKDemoApp extends Application {
    private static final String TAG = "AdSDKDemoApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate - Initializing Google Mobile Ads SDK");
        MobileAds.initialize(this, initializationStatus -> {
            Log.d(TAG, "Google Mobile Ads SDK initialized: " + initializationStatus.toString());
        });
    }
}
