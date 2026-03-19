package com.example.inmobiapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inmobi.ads.AdMetaInfo;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.ads.InMobiNative;
import com.inmobi.ads.listeners.BannerAdEventListener;
import com.inmobi.ads.listeners.InterstitialAdEventListener;
import com.inmobi.ads.listeners.NativeAdEventListener;
import com.inmobi.sdk.InMobiSdk;
import com.inmobi.sdk.SdkInitializationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class InMobiAdManager {
    private static final String TAG = "InMobiAdManager";
    private static final String ACCOUNT_ID = "4028cb8b2c3a0b45012c406824e800ba";
    private static final long BANNER_PLACEMENT_ID = 1685041492873L;
    private static final long INTERSTITIAL_PLACEMENT_ID = 1685049498974L;
    private static final long NATIVE_PLACEMENT_ID = 1685041492873L;
    private static final long REWARDED_PLACEMENT_ID = 1685049498974L;

    private final Context context;
    private final AdEventListener listener;
    private InMobiBanner inMobiBanner;
    private InMobiInterstitial inMobiInterstitial;
    private InMobiNative inMobiNative;
    private InMobiInterstitial inMobiRewarded;

    public interface AdEventListener {
        void onAdEvent(String message);
        void onStatusChanged(String status);
        void onInterstitialReady(boolean ready);
        void onRewardedReady(boolean ready);
    }

    public InMobiAdManager(Context context, AdEventListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void initialize() {
        notifyStatus("Initializing InMobi SDK...");
        notifyEvent("Initializing InMobi SDK with Account ID: " + ACCOUNT_ID);
        JSONObject consentObject = new JSONObject();
        try {
            consentObject.put("gdpr_consent_available", true);
            consentObject.put("gdpr", "0");
        } catch (JSONException e) {
            Log.e(TAG, "Error creating consent JSON", e);
        }
        InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);
        InMobiSdk.init(context, ACCOUNT_ID, consentObject, new SdkInitializationListener() {
            @Override
            public void onInitializationComplete(Error error) {
                if (error == null) {
                    notifyEvent("InMobi SDK INITIALIZED successfully");
                    notifyStatus("InMobi SDK Initialized");
                } else {
                    notifyEvent("InMobi SDK Init FAILED: " + error.getMessage());
                    notifyStatus("Init Failed: " + error.getMessage());
                }
            }
        });
    }

    public void loadBanner(Activity activity, ViewGroup container) {
        notifyEvent("Loading InMobi Banner Ad...");
        if (inMobiBanner != null) container.removeAllViews();
        inMobiBanner = new InMobiBanner(activity, BANNER_PLACEMENT_ID);
        inMobiBanner.setAnimationType(InMobiBanner.AnimationType.ROTATE_HORIZONTAL_AXIS);
        inMobiBanner.setEnableAutoRefresh(false);
        inMobiBanner.setListener(new BannerAdEventListener() {
            @Override public void onAdLoadSucceeded(InMobiBanner ad, AdMetaInfo info) { notifyEvent("Banner LOADED"); notifyStatus("Banner Loaded"); }
            @Override public void onAdLoadFailed(InMobiBanner ad, InMobiAdRequestStatus s) { notifyEvent("Banner FAILED: " + s.getMessage()); }
            @Override public void onAdDisplayed(InMobiBanner ad) { notifyEvent("Banner Displayed"); }
            @Override public void onAdDismissed(InMobiBanner ad) { notifyEvent("Banner Dismissed"); }
            @Override public void onAdClicked(InMobiBanner ad, Map<Object, Object> p) { notifyEvent("Banner Clicked"); }
            @Override public void onUserLeftApplication(InMobiBanner ad) { notifyEvent("Banner Left App"); }
            @Override public void onAdImpression(InMobiBanner ad) { notifyEvent("Banner Impression"); }
            @Override public void onRewardsUnlocked(InMobiBanner ad, Map<Object, Object> r) { notifyEvent("Banner Rewards Unlocked"); }
        });
        int heightPx = (int) (50 * activity.getResources().getDisplayMetrics().density);
        inMobiBanner.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, heightPx));
        container.removeAllViews();
        container.addView(inMobiBanner);
        inMobiBanner.load();
    }

    public void loadInterstitial(Activity activity) {
        notifyEvent("Loading InMobi Interstitial...");
        inMobiInterstitial = new InMobiInterstitial(activity, INTERSTITIAL_PLACEMENT_ID,
            new InterstitialAdEventListener() {
                @Override public void onAdLoadSucceeded(InMobiInterstitial ad, AdMetaInfo info) {
                    notifyEvent("Interstitial LOADED"); notifyStatus("Interstitial Ready");
                    listener.onInterstitialReady(true);
                }
                @Override public void onAdLoadFailed(InMobiInterstitial ad, InMobiAdRequestStatus s) { notifyEvent("Interstitial FAILED: " + s.getMessage()); }
                @Override public void onAdWillDisplay(InMobiInterstitial ad) { notifyEvent("Interstitial Will Display"); }
                @Override public void onAdDisplayed(InMobiInterstitial ad, AdMetaInfo info) { notifyEvent("Interstitial DISPLAYED"); }
                @Override public void onAdDisplayFailed(InMobiInterstitial ad) { notifyEvent("Interstitial Display Failed"); }
                @Override public void onAdDismissed(InMobiInterstitial ad) { notifyEvent("Interstitial Dismissed"); listener.onInterstitialReady(false); }
                @Override public void onAdClicked(InMobiInterstitial ad, Map<Object, Object> p) { notifyEvent("Interstitial Clicked"); }
                @Override public void onUserLeftApplication(InMobiInterstitial ad) { notifyEvent("Interstitial Left App"); }
                @Override public void onAdImpression(InMobiInterstitial ad) { notifyEvent("Interstitial Impression"); }
                @Override public void onRewardsUnlocked(InMobiInterstitial ad, Map<Object, Object> r) { notifyEvent("Interstitial Rewards Unlocked"); }
            });
        inMobiInterstitial.load();
    }

    public void showInterstitial() {
        if (inMobiInterstitial != null && inMobiInterstitial.isReady()) inMobiInterstitial.show();
        else notifyEvent("Interstitial not ready");
    }

    public void loadNative(Activity activity, ViewGroup container) {
        notifyEvent("Loading InMobi Native...");
        inMobiNative = new InMobiNative(activity, NATIVE_PLACEMENT_ID, new NativeAdEventListener() {
            @Override public void onAdLoadSucceeded(InMobiNative ad, AdMetaInfo info) {
                notifyEvent("Native LOADED"); notifyStatus("Native Loaded");
                displayNativeAd(activity, container, ad);
            }
            @Override public void onAdLoadFailed(InMobiNative ad, InMobiAdRequestStatus s) { notifyEvent("Native FAILED: " + s.getMessage()); }
            @Override public void onAdFullScreenDismissed(InMobiNative ad) { notifyEvent("Native Fullscreen Dismissed"); }
            @Override public void onAdFullScreenDisplayed(InMobiNative ad) { notifyEvent("Native Fullscreen Displayed"); }
            @Override public void onAdFullScreenWillDisplay(InMobiNative ad) { notifyEvent("Native Fullscreen Will Display"); }
            @Override public void onAdClicked(InMobiNative ad) { notifyEvent("Native Clicked"); }
            @Override public void onAdImpression(InMobiNative ad) { notifyEvent("Native Impression"); }
            @Override public void onAdStatusChanged(InMobiNative ad) { notifyEvent("Native Status Changed"); }
            @Override public void onUserWillLeaveApplication(InMobiNative ad) { notifyEvent("Native Left App"); }
        });
        inMobiNative.load();
    }

    private void displayNativeAd(Activity activity, ViewGroup container, InMobiNative ad) {
        activity.runOnUiThread(() -> {
            LinearLayout nativeLayout = new LinearLayout(context);
            nativeLayout.setOrientation(LinearLayout.VERTICAL);
            nativeLayout.setPadding(16, 16, 16, 16);

            TextView titleView = new TextView(context);
            titleView.setText("InMobi Native Ad");
            titleView.setTextSize(18);
            nativeLayout.addView(titleView);

            TextView bodyView = new TextView(context);
            bodyView.setText("Native ad loaded successfully. Content rendered by InMobi SDK.");
            bodyView.setTextSize(14);
            nativeLayout.addView(bodyView);

            View primaryView = ad.getPrimaryViewOfWidth(activity, null, container, (int)(container.getWidth()));
            if (primaryView != null) {
                nativeLayout.addView(primaryView);
            }

            container.removeAllViews();
            container.addView(nativeLayout);
            container.setVisibility(View.VISIBLE);
        });
    }

    public void loadRewarded(Activity activity) {
        notifyEvent("Loading InMobi Rewarded...");
        inMobiRewarded = new InMobiInterstitial(activity, REWARDED_PLACEMENT_ID,
            new InterstitialAdEventListener() {
                @Override public void onAdLoadSucceeded(InMobiInterstitial ad, AdMetaInfo info) {
                    notifyEvent("Rewarded LOADED"); notifyStatus("Rewarded Ready");
                    listener.onRewardedReady(true);
                }
                @Override public void onAdLoadFailed(InMobiInterstitial ad, InMobiAdRequestStatus s) { notifyEvent("Rewarded FAILED: " + s.getMessage()); }
                @Override public void onAdWillDisplay(InMobiInterstitial ad) { notifyEvent("Rewarded Will Display"); }
                @Override public void onAdDisplayed(InMobiInterstitial ad, AdMetaInfo info) { notifyEvent("Rewarded DISPLAYED"); }
                @Override public void onAdDisplayFailed(InMobiInterstitial ad) { notifyEvent("Rewarded Display Failed"); }
                @Override public void onAdDismissed(InMobiInterstitial ad) { notifyEvent("Rewarded Dismissed"); listener.onRewardedReady(false); }
                @Override public void onAdClicked(InMobiInterstitial ad, Map<Object, Object> p) { notifyEvent("Rewarded Clicked"); }
                @Override public void onUserLeftApplication(InMobiInterstitial ad) { notifyEvent("Rewarded Left App"); }
                @Override public void onAdImpression(InMobiInterstitial ad) { notifyEvent("Rewarded Impression"); }
                @Override public void onRewardsUnlocked(InMobiInterstitial ad, Map<Object, Object> rewards) {
                    notifyEvent("Rewarded - REWARDS UNLOCKED!");
                    if (rewards != null) {
                        for (Map.Entry<Object, Object> entry : rewards.entrySet()) {
                            notifyEvent("Reward: " + entry.getKey() + " = " + entry.getValue());
                        }
                    }
                }
            });
        inMobiRewarded.load();
    }

    public void showRewarded() {
        if (inMobiRewarded != null && inMobiRewarded.isReady()) inMobiRewarded.show();
        else notifyEvent("Rewarded not ready");
    }

    public void destroy() {
        if (inMobiBanner != null) inMobiBanner.destroy();
        if (inMobiNative != null) inMobiNative.destroy();
    }

    private void notifyEvent(String msg) { Log.d(TAG, msg); listener.onAdEvent(msg); }
    private void notifyStatus(String s) { listener.onStatusChanged(s); }
}
