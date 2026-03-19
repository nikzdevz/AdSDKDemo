package com.example.ironsourceapp;

import android.app.Activity;
import android.util.Log;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoListener;

/**
 * Manages ironSource (Unity LevelPlay) ad loading, showing, and lifecycle events.
 * Supports Interstitial and Rewarded ad formats.
 */
public class IronSourceAdManager {
    private static final String TAG = "IronSourceAdManager";
    private static final String APP_KEY = "85460dcd";

    private final AdEventListener listener;

    public interface AdEventListener {
        void onAdEvent(String message);
        void onStatusChanged(String status);
        void onInterstitialReady(boolean ready);
        void onRewardedReady(boolean ready);
    }

    public IronSourceAdManager(AdEventListener listener) {
        this.listener = listener;
    }

    public void initialize(Activity activity) {
        notifyStatus("Initializing ironSource SDK...");
        notifyEvent("Initializing ironSource SDK with App Key: " + APP_KEY);
        setupRewardedListener();
        setupInterstitialListener();
        IronSource.setAdaptersDebug(true);
        IronSource.init(activity, APP_KEY, () -> {
            notifyEvent("ironSource SDK INITIALIZED successfully");
            notifyStatus("ironSource SDK Initialized");
        });
    }

    private void setupRewardedListener() {
        IronSource.setLevelPlayRewardedVideoListener(new LevelPlayRewardedVideoListener() {
            @Override
            public void onAdAvailable(AdInfo adInfo) {
                notifyEvent("ironSource Rewarded Ad Available");
                notifyStatus("Rewarded Ad Available");
                listener.onRewardedReady(true);
            }

            @Override
            public void onAdUnavailable() {
                notifyEvent("ironSource Rewarded Ad Unavailable");
                listener.onRewardedReady(false);
            }

            @Override
            public void onAdOpened(AdInfo adInfo) {
                notifyEvent("ironSource Rewarded Ad Opened");
            }

            @Override
            public void onAdClosed(AdInfo adInfo) {
                notifyEvent("ironSource Rewarded Ad Closed");
                listener.onRewardedReady(false);
            }

            @Override
            public void onAdRewarded(Placement placement, AdInfo adInfo) {
                notifyEvent("ironSource Reward EARNED! " + placement.getRewardName()
                        + " x" + placement.getRewardAmount());
            }

            @Override
            public void onAdShowFailed(IronSourceError error, AdInfo adInfo) {
                notifyEvent("ironSource Rewarded Show FAILED: " + error.getErrorMessage());
            }

            @Override
            public void onAdClicked(Placement placement, AdInfo adInfo) {
                notifyEvent("ironSource Rewarded Clicked");
            }
        });
    }

    private void setupInterstitialListener() {
        IronSource.setLevelPlayInterstitialListener(new LevelPlayInterstitialListener() {
            @Override
            public void onAdReady(AdInfo adInfo) {
                notifyEvent("ironSource Interstitial READY");
                notifyStatus("Interstitial Ready - Can Show");
                listener.onInterstitialReady(true);
            }

            @Override
            public void onAdLoadFailed(IronSourceError error) {
                notifyEvent("ironSource Interstitial FAILED: " + error.getErrorMessage());
                notifyStatus("Interstitial Failed: " + error.getErrorMessage());
            }

            @Override
            public void onAdOpened(AdInfo adInfo) {
                notifyEvent("ironSource Interstitial Opened");
            }

            @Override
            public void onAdClosed(AdInfo adInfo) {
                notifyEvent("ironSource Interstitial Closed");
                listener.onInterstitialReady(false);
            }

            @Override
            public void onAdShowFailed(IronSourceError error, AdInfo adInfo) {
                notifyEvent("ironSource Interstitial Show FAILED: " + error.getErrorMessage());
            }

            @Override
            public void onAdClicked(AdInfo adInfo) {
                notifyEvent("ironSource Interstitial Clicked");
            }

            @Override
            public void onAdShowSucceeded(AdInfo adInfo) {
                notifyEvent("ironSource Interstitial SHOWN successfully");
            }
        });
    }

    public void loadInterstitial() {
        notifyEvent("Loading ironSource Interstitial...");
        IronSource.loadInterstitial();
    }

    public void showInterstitial() {
        if (IronSource.isInterstitialReady()) {
            IronSource.showInterstitial();
        } else {
            notifyEvent("ironSource Interstitial not ready");
            notifyStatus("Interstitial not loaded yet");
        }
    }

    public void loadRewarded() {
        notifyEvent("Loading ironSource Rewarded Video...");
        notifyEvent("Note: ironSource Rewarded Videos auto-load. Checking availability...");
        if (IronSource.isRewardedVideoAvailable()) {
            notifyEvent("ironSource Rewarded Video is AVAILABLE");
            listener.onRewardedReady(true);
        } else {
            notifyEvent("ironSource Rewarded Video not available yet");
        }
    }

    public void showRewarded() {
        if (IronSource.isRewardedVideoAvailable()) {
            IronSource.showRewardedVideo();
        } else {
            notifyEvent("ironSource Rewarded Video not available");
            notifyStatus("Rewarded not available");
        }
    }

    public void onResume(Activity activity) {
        IronSource.onResume(activity);
    }

    public void onPause(Activity activity) {
        IronSource.onPause(activity);
    }

    private void notifyEvent(String message) {
        Log.d(TAG, message);
        listener.onAdEvent(message);
    }

    private void notifyStatus(String status) {
        listener.onStatusChanged(status);
    }
}
