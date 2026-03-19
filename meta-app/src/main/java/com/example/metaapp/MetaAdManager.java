package com.example.metaapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.facebook.ads.NativeBannerAdView;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.facebook.ads.RewardedInterstitialAd;
import com.facebook.ads.RewardedInterstitialAdListener;

import java.util.ArrayList;
import java.util.List;

public class MetaAdManager {
    private static final String TAG = "MetaAdManager";
    private static final String BANNER_PLACEMENT_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
    private static final String INTERSTITIAL_PLACEMENT_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
    private static final String NATIVE_PLACEMENT_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
    private static final String NATIVE_BANNER_PLACEMENT_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
    private static final String REWARDED_PLACEMENT_ID = "VID_HD_16_9_46S_APP_INSTALL#YOUR_PLACEMENT_ID";
    private static final String REWARDED_INTERSTITIAL_PLACEMENT_ID = "VID_HD_16_9_46S_APP_INSTALL#YOUR_PLACEMENT_ID";

    private final Context context;
    private final AdEventListener listener;
    private AdView adView;
    private InterstitialAd interstitialAd;
    private NativeAd nativeAd;
    private NativeBannerAd nativeBannerAd;
    private RewardedVideoAd rewardedVideoAd;
    private RewardedInterstitialAd rewardedInterstitialAd;

    public interface AdEventListener {
        void onAdEvent(String message);
        void onStatusChanged(String status);
        void onInterstitialReady(boolean ready);
        void onRewardedReady(boolean ready);
        void onRewardedInterstitialReady(boolean ready);
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
        notifyEvent("Meta SDK initialized successfully");
    }

    public void loadBanner(ViewGroup container) {
        notifyEvent("Loading Meta Banner...");
        if (adView != null) adView.destroy();
        adView = new AdView(context, BANNER_PLACEMENT_ID, AdSize.BANNER_HEIGHT_50);
        container.removeAllViews();
        container.addView(adView);
        com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
            @Override public void onError(Ad ad, AdError e) { notifyEvent("Banner FAILED: " + e.getErrorMessage()); }
            @Override public void onAdLoaded(Ad ad) { notifyEvent("Banner LOADED"); notifyStatus("Banner Loaded"); }
            @Override public void onAdClicked(Ad ad) { notifyEvent("Banner Clicked"); }
            @Override public void onLoggingImpression(Ad ad) { notifyEvent("Banner Impression"); }
        };
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
    }

    public void loadInterstitial() {
        notifyEvent("Loading Meta Interstitial...");
        if (interstitialAd != null) interstitialAd.destroy();
        interstitialAd = new InterstitialAd(context, INTERSTITIAL_PLACEMENT_ID);
        InterstitialAdListener il = new InterstitialAdListener() {
            @Override public void onError(Ad ad, AdError e) { notifyEvent("Interstitial FAILED: " + e.getErrorMessage()); }
            @Override public void onAdLoaded(Ad ad) { notifyEvent("Interstitial LOADED"); notifyStatus("Interstitial Ready"); listener.onInterstitialReady(true); }
            @Override public void onAdClicked(Ad ad) { notifyEvent("Interstitial Clicked"); }
            @Override public void onLoggingImpression(Ad ad) { notifyEvent("Interstitial Impression"); }
            @Override public void onInterstitialDisplayed(Ad ad) { notifyEvent("Interstitial DISPLAYED"); }
            @Override public void onInterstitialDismissed(Ad ad) { notifyEvent("Interstitial Dismissed"); listener.onInterstitialReady(false); }
        };
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(il).build());
    }

    public void showInterstitial() {
        if (interstitialAd != null && interstitialAd.isAdLoaded()) interstitialAd.show();
        else notifyEvent("Interstitial not ready");
    }

    public void loadNative(Activity activity, ViewGroup container) {
        notifyEvent("Loading Meta Native...");
        if (nativeAd != null) nativeAd.destroy();
        nativeAd = new NativeAd(context, NATIVE_PLACEMENT_ID);
        NativeAdListener nl = new NativeAdListener() {
            @Override public void onError(Ad ad, AdError e) { notifyEvent("Native FAILED: " + e.getErrorMessage()); }
            @Override public void onAdLoaded(Ad ad) {
                notifyEvent("Native LOADED"); notifyStatus("Native Loaded");
                displayNativeAd(activity, container);
            }
            @Override public void onAdClicked(Ad ad) { notifyEvent("Native Clicked"); }
            @Override public void onLoggingImpression(Ad ad) { notifyEvent("Native Impression"); }
            @Override public void onMediaDownloaded(Ad ad) { notifyEvent("Native Media Downloaded"); }
        };
        nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nl).build());
    }

    private void displayNativeAd(Activity activity, ViewGroup container) {
        activity.runOnUiThread(() -> {
            if (nativeAd == null || !nativeAd.isAdLoaded()) return;
            nativeAd.unregisterView();
            NativeAdLayout nativeAdLayout = new NativeAdLayout(context);
            View adView = LayoutInflater.from(context).inflate(R.layout.meta_native_ad_layout, nativeAdLayout, false);
            nativeAdLayout.addView(adView);
            TextView title = adView.findViewById(R.id.native_ad_title);
            TextView body = adView.findViewById(R.id.native_ad_body);
            TextView socialContext = adView.findViewById(R.id.native_ad_social_context);
            Button callToAction = adView.findViewById(R.id.native_ad_call_to_action);
            com.facebook.ads.MediaView mediaView = adView.findViewById(R.id.native_ad_media);
            com.facebook.ads.MediaView iconView = adView.findViewById(R.id.native_ad_icon);
            title.setText(nativeAd.getAdvertiserName());
            body.setText(nativeAd.getAdBodyText());
            socialContext.setText(nativeAd.getAdSocialContext());
            callToAction.setText(nativeAd.getAdCallToAction());
            callToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(title);
            clickableViews.add(callToAction);
            nativeAd.registerViewForInteraction(nativeAdLayout, mediaView, iconView, clickableViews);
            container.removeAllViews();
            container.addView(nativeAdLayout);
            container.setVisibility(View.VISIBLE);
        });
    }

    public void loadRewardedVideo() {
        notifyEvent("Loading Meta Rewarded Video...");
        if (rewardedVideoAd != null) rewardedVideoAd.destroy();
        rewardedVideoAd = new RewardedVideoAd(context, REWARDED_PLACEMENT_ID);
        RewardedVideoAdListener rl = new RewardedVideoAdListener() {
            @Override public void onError(Ad ad, AdError e) { notifyEvent("Rewarded FAILED: " + e.getErrorMessage()); }
            @Override public void onAdLoaded(Ad ad) { notifyEvent("Rewarded LOADED"); notifyStatus("Rewarded Ready"); listener.onRewardedReady(true); }
            @Override public void onAdClicked(Ad ad) { notifyEvent("Rewarded Clicked"); }
            @Override public void onLoggingImpression(Ad ad) { notifyEvent("Rewarded Impression"); }
            @Override public void onRewardedVideoCompleted() { notifyEvent("Rewarded Video COMPLETED - Reward Earned!"); }
            @Override public void onRewardedVideoClosed() { notifyEvent("Rewarded Closed"); listener.onRewardedReady(false); }
        };
        rewardedVideoAd.loadAd(rewardedVideoAd.buildLoadAdConfig().withAdListener(rl).build());
    }

    public void showRewardedVideo() {
        if (rewardedVideoAd != null && rewardedVideoAd.isAdLoaded()) rewardedVideoAd.show();
        else notifyEvent("Rewarded not ready");
    }

    public void loadRewardedInterstitial() {
        notifyEvent("Loading Meta Rewarded Interstitial...");
        if (rewardedInterstitialAd != null) rewardedInterstitialAd.destroy();
        rewardedInterstitialAd = new RewardedInterstitialAd(context, REWARDED_INTERSTITIAL_PLACEMENT_ID);
        RewardedInterstitialAdListener ril = new RewardedInterstitialAdListener() {
            @Override public void onError(Ad ad, AdError e) { notifyEvent("Rewarded Interstitial FAILED: " + e.getErrorMessage()); }
            @Override public void onAdLoaded(Ad ad) { notifyEvent("Rewarded Interstitial LOADED"); notifyStatus("Rewarded Interstitial Ready"); listener.onRewardedInterstitialReady(true); }
            @Override public void onAdClicked(Ad ad) { notifyEvent("Rewarded Interstitial Clicked"); }
            @Override public void onLoggingImpression(Ad ad) { notifyEvent("Rewarded Interstitial Impression"); }
            @Override public void onRewardedInterstitialCompleted() { notifyEvent("Rewarded Interstitial COMPLETED - Reward Earned!"); }
            @Override public void onRewardedInterstitialClosed() { notifyEvent("Rewarded Interstitial Closed"); listener.onRewardedInterstitialReady(false); }
        };
        rewardedInterstitialAd.loadAd(rewardedInterstitialAd.buildLoadAdConfig().withAdListener(ril).build());
    }

    public void showRewardedInterstitial() {
        if (rewardedInterstitialAd != null && rewardedInterstitialAd.isAdLoaded()) rewardedInterstitialAd.show();
        else notifyEvent("Rewarded Interstitial not ready");
    }

    public void destroy() {
        if (adView != null) adView.destroy();
        if (interstitialAd != null) interstitialAd.destroy();
        if (nativeAd != null) nativeAd.destroy();
        if (rewardedVideoAd != null) rewardedVideoAd.destroy();
        if (rewardedInterstitialAd != null) rewardedInterstitialAd.destroy();
    }

    private void notifyEvent(String msg) { Log.d(TAG, msg); listener.onAdEvent(msg); }
    private void notifyStatus(String s) { listener.onStatusChanged(s); }
}
