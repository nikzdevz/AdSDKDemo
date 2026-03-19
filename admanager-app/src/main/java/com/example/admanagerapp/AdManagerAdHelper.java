package com.example.admanagerapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;

/**
 * Manages Google Ad Manager ad loading, showing, and lifecycle events.
 * Supports Banner and Interstitial ad formats.
 */
public class AdManagerAdHelper {
    private static final String TAG = "AdManagerAdHelper";

    private static final String BANNER_AD_UNIT_ID = "/6499/example/banner";
    private static final String INTERSTITIAL_AD_UNIT_ID = "/6499/example/interstitial";

    private final Context context;
    private final AdEventListener listener;

    private AdManagerAdView adManagerAdView;
    private AdManagerInterstitialAd adManagerInterstitialAd;

    public interface AdEventListener {
        void onAdEvent(String message);
        void onStatusChanged(String status);
        void onInterstitialReady(boolean ready);
    }

    public AdManagerAdHelper(Context context, AdEventListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void loadBanner(ViewGroup container) {
        notifyEvent("Loading Ad Manager Banner...");
        adManagerAdView = new AdManagerAdView(context);
        adManagerAdView.setAdSizes(AdSize.BANNER);
        adManagerAdView.setAdUnitId(BANNER_AD_UNIT_ID);

        container.removeAllViews();
        container.addView(adManagerAdView);

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        adManagerAdView.loadAd(adRequest);

        adManagerAdView.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdLoaded() {
                notifyEvent("Ad Manager Banner LOADED successfully");
                notifyStatus("Banner Ad Loaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                notifyEvent("Ad Manager Banner FAILED: " + loadAdError.getMessage());
                notifyStatus("Banner Failed: " + loadAdError.getMessage());
            }

            @Override
            public void onAdOpened() { notifyEvent("Ad Manager Banner Opened"); }

            @Override
            public void onAdClicked() { notifyEvent("Ad Manager Banner Clicked"); }

            @Override
            public void onAdClosed() { notifyEvent("Ad Manager Banner Closed"); }

            @Override
            public void onAdImpression() { notifyEvent("Ad Manager Banner Impression recorded"); }
        });
    }

    public void loadInterstitial() {
        notifyEvent("Loading Ad Manager Interstitial...");
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();

        AdManagerInterstitialAd.load(context, INTERSTITIAL_AD_UNIT_ID, adRequest,
                new AdManagerInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AdManagerInterstitialAd ad) {
                        adManagerInterstitialAd = ad;
                        listener.onInterstitialReady(true);
                        notifyEvent("Ad Manager Interstitial LOADED successfully");
                        notifyStatus("Interstitial Ad Loaded - Ready to Show");

                        adManagerInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                notifyEvent("Ad Manager Interstitial Dismissed");
                                adManagerInterstitialAd = null;
                                listener.onInterstitialReady(false);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                notifyEvent("Ad Manager Interstitial Show Failed: " + adError.getMessage());
                                adManagerInterstitialAd = null;
                                listener.onInterstitialReady(false);
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                notifyEvent("Ad Manager Interstitial SHOWN");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        notifyEvent("Ad Manager Interstitial FAILED: " + loadAdError.getMessage());
                        notifyStatus("Interstitial Failed: " + loadAdError.getMessage());
                        adManagerInterstitialAd = null;
                    }
                });
    }

    public void showInterstitial(Activity activity) {
        if (adManagerInterstitialAd != null) {
            adManagerInterstitialAd.show(activity);
        } else {
            notifyEvent("Interstitial not ready");
            notifyStatus("Interstitial not loaded yet");
        }
    }

    public void destroy() {
        if (adManagerAdView != null) {
            adManagerAdView.destroy();
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
