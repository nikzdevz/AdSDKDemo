package com.example.unityapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

/**
 * Manages Unity Ads loading, showing, and lifecycle events.
 * Supports Banner, Interstitial, and Rewarded ad formats.
 */
public class UnityAdManager implements IUnityAdsInitializationListener {
    private static final String TAG = "UnityAdManager";

    private static final String UNITY_GAME_ID = "14851";
    private static final boolean TEST_MODE = true;
    private static final String INTERSTITIAL_PLACEMENT_ID = "video";
    private static final String REWARDED_PLACEMENT_ID = "rewardedVideo";
    private static final String BANNER_PLACEMENT_ID = "banner";

    private final Context context;
    private final AdEventListener listener;
    private boolean interstitialLoaded = false;
    private boolean rewardedLoaded = false;
    private BannerView bannerView;

    public interface AdEventListener {
        void onAdEvent(String message);
        void onStatusChanged(String status);
        void onInterstitialReady(boolean ready);
        void onRewardedReady(boolean ready);
    }

    public UnityAdManager(Context context, AdEventListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void initialize() {
        notifyStatus("Initializing Unity Ads...");
        notifyEvent("Initializing Unity Ads SDK with Game ID: " + UNITY_GAME_ID);
        UnityAds.initialize(context, UNITY_GAME_ID, TEST_MODE, this);
    }

    @Override
    public void onInitializationComplete() {
        notifyEvent("Unity Ads SDK INITIALIZED successfully");
        notifyStatus("Unity Ads SDK Initialized");
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
        notifyEvent("Unity Ads Init FAILED: " + error.toString() + " - " + message);
        notifyStatus("Init Failed: " + message);
    }

    public void loadBanner(Activity activity, ViewGroup container) {
        notifyEvent("Loading Unity Banner Ad...");
        if (bannerView != null) { bannerView.destroy(); }
        bannerView = new BannerView(activity, BANNER_PLACEMENT_ID, new UnityBannerSize(320, 50));
        bannerView.setListener(new BannerView.IListener() {
            @Override public void onBannerLoaded(BannerView v) { notifyEvent("Unity Banner LOADED successfully"); notifyStatus("Banner Loaded"); }
            @Override public void onBannerShown(BannerView v) { notifyEvent("Unity Banner SHOWN"); }
            @Override public void onBannerClick(BannerView v) { notifyEvent("Unity Banner Clicked"); }
            @Override public void onBannerFailedToLoad(BannerView v, BannerErrorInfo e) { notifyEvent("Unity Banner FAILED: " + e.errorMessage); notifyStatus("Banner Failed: " + e.errorMessage); }
            @Override public void onBannerLeftApplication(BannerView v) { notifyEvent("Unity Banner Left Application"); }
        });
        container.removeAllViews();
        container.addView(bannerView);
        bannerView.load();
    }

    public void loadInterstitial() {
        notifyEvent("Loading Unity Interstitial Ad...");
        UnityAds.load(INTERSTITIAL_PLACEMENT_ID, new IUnityAdsLoadListener() {
            @Override public void onUnityAdsAdLoaded(String placementId) {
                interstitialLoaded = true;
                listener.onInterstitialReady(true);
                notifyEvent("Unity Interstitial LOADED: " + placementId);
                notifyStatus("Interstitial Loaded - Ready to Show");
            }
            @Override public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                notifyEvent("Unity Interstitial FAILED: " + error.toString() + " - " + message);
                notifyStatus("Interstitial Failed: " + message);
            }
        });
    }

    public void showInterstitial(Activity activity) {
        if (interstitialLoaded) {
            UnityAds.show(activity, INTERSTITIAL_PLACEMENT_ID, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                @Override public void onUnityAdsShowFailure(String p, UnityAds.UnityAdsShowError e, String m) { notifyEvent("Unity Interstitial Show FAILED: " + m); }
                @Override public void onUnityAdsShowStart(String p) { notifyEvent("Unity Interstitial Show Started"); }
                @Override public void onUnityAdsShowClick(String p) { notifyEvent("Unity Interstitial Clicked"); }
                @Override public void onUnityAdsShowComplete(String p, UnityAds.UnityAdsShowCompletionState s) {
                    notifyEvent("Unity Interstitial Completed: " + s.toString());
                    interstitialLoaded = false;
                    listener.onInterstitialReady(false);
                }
            });
        } else { notifyEvent("Unity Interstitial not ready"); }
    }

    public void loadRewarded() {
        notifyEvent("Loading Unity Rewarded Ad...");
        UnityAds.load(REWARDED_PLACEMENT_ID, new IUnityAdsLoadListener() {
            @Override public void onUnityAdsAdLoaded(String placementId) {
                rewardedLoaded = true;
                listener.onRewardedReady(true);
                notifyEvent("Unity Rewarded LOADED: " + placementId);
                notifyStatus("Rewarded Loaded - Ready to Show");
            }
            @Override public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                notifyEvent("Unity Rewarded FAILED: " + error.toString() + " - " + message);
                notifyStatus("Rewarded Failed: " + message);
            }
        });
    }

    public void showRewarded(Activity activity) {
        if (rewardedLoaded) {
            UnityAds.show(activity, REWARDED_PLACEMENT_ID, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                @Override public void onUnityAdsShowFailure(String p, UnityAds.UnityAdsShowError e, String m) { notifyEvent("Unity Rewarded Show FAILED: " + m); }
                @Override public void onUnityAdsShowStart(String p) { notifyEvent("Unity Rewarded Show Started"); }
                @Override public void onUnityAdsShowClick(String p) { notifyEvent("Unity Rewarded Clicked"); }
                @Override public void onUnityAdsShowComplete(String p, UnityAds.UnityAdsShowCompletionState s) {
                    notifyEvent("Unity Rewarded Completed: " + s.toString());
                    if (s == UnityAds.UnityAdsShowCompletionState.COMPLETED) { notifyEvent("Unity Reward EARNED!"); }
                    rewardedLoaded = false;
                    listener.onRewardedReady(false);
                }
            });
        } else { notifyEvent("Unity Rewarded not ready"); }
    }

    public void destroy() { if (bannerView != null) { bannerView.destroy(); } }

    private void notifyEvent(String message) { Log.d(TAG, message); listener.onAdEvent(message); }
    private void notifyStatus(String status) { listener.onStatusChanged(status); }
}
