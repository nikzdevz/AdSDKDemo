package com.example.applovinapp;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.sdk.AppLovinSdk;

public class AppLovinAdManager {
    private static final String TAG = "AppLovinAdManager";
    private static final String BANNER_AD_UNIT_ID = "YOUR_BANNER_AD_UNIT_ID";
    private static final String MREC_AD_UNIT_ID = "YOUR_MREC_AD_UNIT_ID";
    private static final String INTERSTITIAL_AD_UNIT_ID = "YOUR_INTERSTITIAL_AD_UNIT_ID";
    private static final String REWARDED_AD_UNIT_ID = "YOUR_REWARDED_AD_UNIT_ID";
    private static final String NATIVE_AD_UNIT_ID = "YOUR_NATIVE_AD_UNIT_ID";

    private final AdEventListener listener;
    private MaxAdView bannerAdView;
    private MaxAdView mrecAdView;
    private MaxInterstitialAd interstitialAd;
    private MaxRewardedAd rewardedAd;
    private MaxNativeAdLoader nativeAdLoader;
    private MaxAd loadedNativeAd;

    public interface AdEventListener {
        void onAdEvent(String message);
        void onStatusChanged(String status);
        void onInterstitialReady(boolean ready);
        void onRewardedReady(boolean ready);
    }

    public AppLovinAdManager(AdEventListener listener) {
        this.listener = listener;
    }

    public void initialize(Activity activity) {
        notifyStatus("Initializing AppLovin MAX SDK...");
        notifyEvent("Initializing AppLovin MAX SDK...");
        AppLovinSdk.getInstance(activity).setMediationProvider("max");
        AppLovinSdk.getInstance(activity).initializeSdk(config -> {
            notifyEvent("AppLovin MAX SDK INITIALIZED");
            notifyStatus("AppLovin MAX SDK Initialized");
        });
    }

    public void loadBanner(Activity activity, ViewGroup container) {
        notifyEvent("Loading AppLovin Banner...");
        if (bannerAdView != null) { bannerAdView.destroy(); }
        bannerAdView = new MaxAdView(BANNER_AD_UNIT_ID, activity);
        bannerAdView.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            (int)(50 * activity.getResources().getDisplayMetrics().density)));
        bannerAdView.setListener(new MaxAdViewAdListener() {
            @Override public void onAdLoaded(MaxAd ad) { notifyEvent("Banner LOADED"); notifyStatus("Banner Loaded"); }
            @Override public void onAdLoadFailed(String id, MaxError e) { notifyEvent("Banner FAILED: " + e.getMessage()); }
            @Override public void onAdDisplayed(MaxAd ad) { notifyEvent("Banner Displayed"); }
            @Override public void onAdHidden(MaxAd ad) { notifyEvent("Banner Hidden"); }
            @Override public void onAdClicked(MaxAd ad) { notifyEvent("Banner Clicked"); }
            @Override public void onAdDisplayFailed(MaxAd ad, MaxError e) { notifyEvent("Banner Display Failed"); }
            @Override public void onAdExpanded(MaxAd ad) { notifyEvent("Banner Expanded"); }
            @Override public void onAdCollapsed(MaxAd ad) { notifyEvent("Banner Collapsed"); }
        });
        container.removeAllViews();
        container.addView(bannerAdView);
        bannerAdView.loadAd();
    }

    public void loadMREC(Activity activity, ViewGroup container) {
        notifyEvent("Loading AppLovin MREC...");
        if (mrecAdView != null) { mrecAdView.destroy(); }
        mrecAdView = new MaxAdView(MREC_AD_UNIT_ID, MaxAdFormat.MREC, activity);
        mrecAdView.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            (int)(250 * activity.getResources().getDisplayMetrics().density)));
        mrecAdView.setListener(new MaxAdViewAdListener() {
            @Override public void onAdLoaded(MaxAd ad) { notifyEvent("MREC LOADED"); notifyStatus("MREC Loaded"); }
            @Override public void onAdLoadFailed(String id, MaxError e) { notifyEvent("MREC FAILED: " + e.getMessage()); }
            @Override public void onAdDisplayed(MaxAd ad) { notifyEvent("MREC Displayed"); }
            @Override public void onAdHidden(MaxAd ad) { notifyEvent("MREC Hidden"); }
            @Override public void onAdClicked(MaxAd ad) { notifyEvent("MREC Clicked"); }
            @Override public void onAdDisplayFailed(MaxAd ad, MaxError e) { notifyEvent("MREC Display Failed"); }
            @Override public void onAdExpanded(MaxAd ad) { notifyEvent("MREC Expanded"); }
            @Override public void onAdCollapsed(MaxAd ad) { notifyEvent("MREC Collapsed"); }
        });
        container.removeAllViews();
        container.addView(mrecAdView);
        container.setVisibility(android.view.View.VISIBLE);
        mrecAdView.loadAd();
    }

    public void loadInterstitial(Activity activity) {
        notifyEvent("Loading AppLovin Interstitial...");
        interstitialAd = new MaxInterstitialAd(INTERSTITIAL_AD_UNIT_ID, activity);
        interstitialAd.setListener(new MaxAdListener() {
            @Override public void onAdLoaded(MaxAd ad) { notifyEvent("Interstitial LOADED"); notifyStatus("Interstitial Ready"); listener.onInterstitialReady(true); }
            @Override public void onAdLoadFailed(String id, MaxError e) { notifyEvent("Interstitial FAILED: " + e.getMessage()); }
            @Override public void onAdDisplayed(MaxAd ad) { notifyEvent("Interstitial Displayed"); }
            @Override public void onAdHidden(MaxAd ad) { notifyEvent("Interstitial Hidden"); listener.onInterstitialReady(false); }
            @Override public void onAdClicked(MaxAd ad) { notifyEvent("Interstitial Clicked"); }
            @Override public void onAdDisplayFailed(MaxAd ad, MaxError e) { notifyEvent("Interstitial Display Failed"); listener.onInterstitialReady(false); }
        });
        interstitialAd.loadAd();
    }

    public void showInterstitial() {
        if (interstitialAd != null && interstitialAd.isReady()) interstitialAd.showAd();
        else notifyEvent("Interstitial not ready");
    }

    public void loadRewarded(Activity activity) {
        notifyEvent("Loading AppLovin Rewarded...");
        rewardedAd = MaxRewardedAd.getInstance(REWARDED_AD_UNIT_ID, activity);
        rewardedAd.setListener(new MaxRewardedAdListener() {
            @Override public void onAdLoaded(MaxAd ad) { notifyEvent("Rewarded LOADED"); notifyStatus("Rewarded Ready"); listener.onRewardedReady(true); }
            @Override public void onAdLoadFailed(String id, MaxError e) { notifyEvent("Rewarded FAILED: " + e.getMessage()); }
            @Override public void onAdDisplayed(MaxAd ad) { notifyEvent("Rewarded Displayed"); }
            @Override public void onAdHidden(MaxAd ad) { notifyEvent("Rewarded Hidden"); listener.onRewardedReady(false); }
            @Override public void onAdClicked(MaxAd ad) { notifyEvent("Rewarded Clicked"); }
            @Override public void onAdDisplayFailed(MaxAd ad, MaxError e) { notifyEvent("Rewarded Display Failed"); listener.onRewardedReady(false); }
            @Override public void onUserRewarded(MaxAd ad, MaxReward reward) { notifyEvent("Reward Earned! " + reward.getLabel() + " x" + reward.getAmount()); }
        });
        rewardedAd.loadAd();
    }

    public void showRewarded() {
        if (rewardedAd != null && rewardedAd.isReady()) rewardedAd.showAd();
        else notifyEvent("Rewarded not ready");
    }

    public void loadNative(Activity activity, ViewGroup container) {
        notifyEvent("Loading AppLovin Native...");
        nativeAdLoader = new MaxNativeAdLoader(NATIVE_AD_UNIT_ID, activity);
        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
            @Override
            public void onNativeAdLoaded(MaxNativeAdView nativeAdView, MaxAd ad) {
                notifyEvent("Native LOADED"); notifyStatus("Native Loaded");
                if (loadedNativeAd != null) nativeAdLoader.destroy(loadedNativeAd);
                loadedNativeAd = ad;
                activity.runOnUiThread(() -> {
                    container.removeAllViews();
                    container.addView(nativeAdView);
                    container.setVisibility(android.view.View.VISIBLE);
                });
            }
            @Override
            public void onNativeAdLoadFailed(String id, MaxError e) {
                notifyEvent("Native FAILED: " + e.getMessage());
            }
            @Override
            public void onNativeAdClicked(MaxAd ad) { notifyEvent("Native Clicked"); }
        });
        nativeAdLoader.loadAd();
    }

    public void destroy() {
        if (bannerAdView != null) bannerAdView.destroy();
        if (mrecAdView != null) mrecAdView.destroy();
        if (interstitialAd != null) interstitialAd.destroy();
        if (rewardedAd != null) rewardedAd.destroy();
        if (nativeAdLoader != null) {
            if (loadedNativeAd != null) nativeAdLoader.destroy(loadedNativeAd);
            nativeAdLoader.destroy();
        }
    }

    private void notifyEvent(String msg) { Log.d(TAG, msg); listener.onAdEvent(msg); }
    private void notifyStatus(String s) { listener.onStatusChanged(s); }
}
