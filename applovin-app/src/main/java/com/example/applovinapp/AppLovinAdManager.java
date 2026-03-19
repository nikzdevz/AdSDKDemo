package com.example.applovinapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;

/**
 * Manages AppLovin MAX ad loading, showing, and lifecycle events.
 * Supports Banner, Interstitial, and Rewarded ad formats.
 */
public class AppLovinAdManager {
    private static final String TAG = "AppLovinAdManager";

    private static final String BANNER_AD_UNIT_ID = "YOUR_BANNER_AD_UNIT_ID";
    private static final String INTERSTITIAL_AD_UNIT_ID = "YOUR_INTERSTITIAL_AD_UNIT_ID";
    private static final String REWARDED_AD_UNIT_ID = "YOUR_REWARDED_AD_UNIT_ID";

    private final Context context;
    private final AdEventListener listener;
    private MaxAdView maxAdView;
    private MaxInterstitialAd maxInterstitialAd;
    private MaxRewardedAd maxRewardedAd;

    public interface AdEventListener {
        void onAdEvent(String message);
        void onStatusChanged(String status);
        void onInterstitialReady(boolean ready);
        void onRewardedReady(boolean ready);
    }

    public AppLovinAdManager(Context context, AdEventListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void initialize() {
        notifyStatus("Initializing AppLovin SDK...");
        notifyEvent("Initializing AppLovin MAX SDK...");
        AppLovinSdk.getInstance(context).setMediationProvider("max");
        AppLovinSdk.getInstance(context).initializeSdk(configuration -> {
            notifyEvent("AppLovin MAX SDK INITIALIZED successfully");
            notifyEvent("Country code: " + configuration.getCountryCode());
            notifyStatus("AppLovin MAX SDK Initialized");
        });
    }

    public void loadBanner(Activity activity, ViewGroup container) {
        notifyEvent("Loading AppLovin Banner Ad...");
        if (maxAdView != null) {
            maxAdView.destroy();
        }
        maxAdView = new MaxAdView(BANNER_AD_UNIT_ID, activity);
        maxAdView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                notifyEvent("AppLovin Banner LOADED successfully");
                notifyStatus("Banner Loaded");
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                notifyEvent("AppLovin Banner Displayed");
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                notifyEvent("AppLovin Banner Hidden");
            }

            @Override
            public void onAdClicked(MaxAd ad) {
                notifyEvent("AppLovin Banner Clicked");
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                notifyEvent("AppLovin Banner FAILED: " + error.getMessage());
                notifyStatus("Banner Failed: " + error.getMessage());
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                notifyEvent("AppLovin Banner Display Failed: " + error.getMessage());
            }

            @Override
            public void onAdExpanded(MaxAd ad) {
                notifyEvent("AppLovin Banner Expanded");
            }

            @Override
            public void onAdCollapsed(MaxAd ad) {
                notifyEvent("AppLovin Banner Collapsed");
            }
        });
        int heightPx = (int) (50 * activity.getResources().getDisplayMetrics().density);
        maxAdView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, heightPx));
        container.removeAllViews();
        container.addView(maxAdView);
        maxAdView.loadAd();
    }

    public void loadInterstitial(Activity activity) {
        notifyEvent("Loading AppLovin Interstitial Ad...");
        maxInterstitialAd = new MaxInterstitialAd(INTERSTITIAL_AD_UNIT_ID, activity);
        maxInterstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                notifyEvent("AppLovin Interstitial LOADED");
                notifyStatus("Interstitial Loaded - Ready to Show");
                listener.onInterstitialReady(true);
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                notifyEvent("AppLovin Interstitial DISPLAYED");
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                notifyEvent("AppLovin Interstitial Hidden");
                listener.onInterstitialReady(false);
            }

            @Override
            public void onAdClicked(MaxAd ad) {
                notifyEvent("AppLovin Interstitial Clicked");
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                notifyEvent("AppLovin Interstitial FAILED: " + error.getMessage());
                notifyStatus("Interstitial Failed: " + error.getMessage());
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                notifyEvent("AppLovin Interstitial Display Failed: " + error.getMessage());
            }
        });
        maxInterstitialAd.loadAd();
    }

    public void showInterstitial() {
        if (maxInterstitialAd != null && maxInterstitialAd.isReady()) {
            maxInterstitialAd.showAd();
        } else {
            notifyEvent("AppLovin Interstitial not ready");
        }
    }

    public void loadRewarded(Activity activity) {
        notifyEvent("Loading AppLovin Rewarded Ad...");
        maxRewardedAd = MaxRewardedAd.getInstance(REWARDED_AD_UNIT_ID, activity);
        maxRewardedAd.setListener(new MaxRewardedAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                notifyEvent("AppLovin Rewarded LOADED");
                notifyStatus("Rewarded Loaded - Ready to Show");
                listener.onRewardedReady(true);
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                notifyEvent("AppLovin Rewarded DISPLAYED");
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                notifyEvent("AppLovin Rewarded Hidden");
                listener.onRewardedReady(false);
            }

            @Override
            public void onAdClicked(MaxAd ad) {
                notifyEvent("AppLovin Rewarded Clicked");
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                notifyEvent("AppLovin Rewarded FAILED: " + error.getMessage());
                notifyStatus("Rewarded Failed: " + error.getMessage());
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                notifyEvent("AppLovin Rewarded Display Failed: " + error.getMessage());
            }

            @Override
            public void onUserRewarded(MaxAd ad, MaxReward reward) {
                notifyEvent("AppLovin Reward EARNED! Label: " + reward.getLabel()
                        + ", Amount: " + reward.getAmount());
            }
        });
        maxRewardedAd.loadAd();
    }

    public void showRewarded() {
        if (maxRewardedAd != null && maxRewardedAd.isReady()) {
            maxRewardedAd.showAd();
        } else {
            notifyEvent("AppLovin Rewarded not ready");
        }
    }

    public void destroy() {
        if (maxAdView != null) {
            maxAdView.destroy();
        }
        if (maxInterstitialAd != null) {
            maxInterstitialAd.destroy();
        }
        if (maxRewardedAd != null) {
            maxRewardedAd.destroy();
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
