package com.example.admobapp;

import android.app.Application;
import com.google.android.gms.ads.MobileAds;

public class AdMobApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this);
    }
}
