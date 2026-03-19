package com.example.metaapp;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

/**
 * Manages Meta Audience Network ad loading, showing, and lifecycle events.
 * Supports Banner and Interstitial ad formats.
 */
public class MetaAdManager {
    private static final String TAG = "MetaAdManager";

    private static final String BANNER_PLACEMENT_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
    private static final String INTERSTITIAL_PLACEMENT_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";

    private final Context context;
    private final AdEventListener listener;

    private AdView adView;
    private InterstitialAd interstitialAd;

    public interface AdEventListener {
        void onAdEvent(String message);
        void onStatusChanged(String status);
        void onInterstitialReady(boolean ready);
    }

    public MetaAdManager(Context context, AdEventListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void initialize() {
        notifyStatus("Initializing Meta Audience Network...");
        notifyEvent("Initializing Meta Audience Network SDK...");
        AdSettings.setTestMode(true);
        notifyEvent("Test mode enabled");
        AudienceNetworkAds.initialize(context);
        notifyStatus("Meta Audience Network SDK Initialized");
        notifyEvent("Meta Audience Network SDK initialized successfully");
    }

    public void loadBanner(ViewGroup container) {
        notifyEvent("Loading Meta Banner Ad...");

        if (adView != null) {
            adView.destroy();
        }

        adView = new AdView(context, BANNER_PLACEMENT_ID, AdSize.BANNER_HEIGHT_50);
        container.removeAllViews();
        container.addView(adView);

        com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                notifyEvent("Meta Banner FAILED: " + adError.getErrorMessage()
                        + " (Code: " + adError.getErrorCode() + ")");
                notifyStatus("Banner Failed: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                notifyEvent("Meta Banner LOADED successfully");
                notifyStatus("Banner Ad Loaded");
            }

            @Override
            public void onAdClicked(Ad ad) {
                notifyEvent("Meta Banner Clicked");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                notifyEvent("Meta Banner Impression logged");
            }
        };

        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
    }

    public void loadInterstitial() {
        notifyEvent("Loading Meta Interstitial Ad...");

        if (interstitialAd != null) {
            interstitialAd.destroy();
        }

        interstitialAd = new InterstitialAd(context, INTERSTITIAL_PLACEMENT_ID);

        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                notifyEvent("Meta Interstitial FAILED: " + adError.getErrorMessage()
                        + " (Code: " + adError.getErrorCode() + ")");
                notifyStatus("Interstitial Failed: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                notifyEvent("Meta Interstitial LOADED successfully");
                notifyStatus("Interstitial Loaded - Ready to Show");
                listener.onInterstitialReady(true);
            }

            @Override
            public void onAdClicked(Ad ad) {
                notifyEvent("Meta Interstitial Clicked");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                notifyEvent("Meta Interstitial Impression logged");
            }

            @Override
            public void onInterstitialDisplayed(Ad ad) {
                notifyEvent("Meta Interstitial DISPLAYED");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                notifyEvent("Meta Interstitial Dismissed");
                listener.onInterstitialReady(false);
            }
        };

        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    public void showInterstitial() {
        if (interstitialAd != null && interstitialAd.isAdLoaded()) {
            interstitialAd.show();
        } else {
            notifyEvent("Interstitial not ready");
            notifyStatus("Interstitial not loaded yet");
        }
    }

    public void destroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
    }

    private void notifyEvent(String message) {
        Log.d(TAG, message);
        listener.onAdEvent(message);
    }

    private void notifyStatus(String status) {
        listener.onStatusChanged(status);
    }
}
