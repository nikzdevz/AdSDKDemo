package com.example.chartboostapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ads.Banner;
import com.chartboost.sdk.ads.Interstitial;
import com.chartboost.sdk.ads.Rewarded;
import com.chartboost.sdk.callbacks.BannerCallback;
import com.chartboost.sdk.callbacks.InterstitialCallback;
import com.chartboost.sdk.callbacks.RewardedCallback;
import com.chartboost.sdk.events.CacheError;
import com.chartboost.sdk.events.CacheEvent;
import com.chartboost.sdk.events.ClickError;
import com.chartboost.sdk.events.ClickEvent;
import com.chartboost.sdk.events.DismissEvent;
import com.chartboost.sdk.events.ImpressionEvent;
import com.chartboost.sdk.events.RewardEvent;
import com.chartboost.sdk.events.ShowError;
import com.chartboost.sdk.events.ShowEvent;

/**
 * Manages Chartboost ad loading, showing, and lifecycle events.
 * Supports Banner, Interstitial, and Rewarded ad formats.
 */
public class ChartboostAdManager {
    private static final String TAG = "ChartboostAdManager";
    private static final String APP_ID = "4f21c409cd1cb2fb7000001b";
    private static final String APP_SIGNATURE = "92e2de2fd7070327d881571f904c275107e0d2c5";

    private final Context context;
    private final AdEventListener listener;
    private Banner chartboostBanner;
    private Interstitial chartboostInterstitial;
    private Rewarded chartboostRewarded;

    public interface AdEventListener {
        void onAdEvent(String message);
        void onStatusChanged(String status);
        void onInterstitialReady(boolean ready);
        void onRewardedReady(boolean ready);
    }

    public ChartboostAdManager(Context context, AdEventListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void initialize() {
        notifyStatus("Initializing Chartboost SDK...");
        notifyEvent("Initializing Chartboost SDK with App ID: " + APP_ID);
        Chartboost.startWithAppId(context, APP_ID, APP_SIGNATURE, startError -> {
            if (startError == null) {
                notifyEvent("Chartboost SDK INITIALIZED successfully");
                notifyStatus("Chartboost SDK Initialized");
            } else {
                notifyEvent("Chartboost SDK Init FAILED: " + startError.getCode().name());
                notifyStatus("Init Failed: " + startError.getCode().name());
            }
        });
    }

    public void loadBanner(Activity activity, ViewGroup container) {
        notifyEvent("Loading Chartboost Banner Ad...");
        if (chartboostBanner != null) {
            chartboostBanner.detach();
        }
        chartboostBanner = new Banner(activity, "default", Banner.BannerSize.STANDARD,
                new BannerCallback() {
                    @Override
                    public void onAdLoaded(@NonNull CacheEvent cacheEvent,
                                           @Nullable CacheError cacheError) {
                        if (cacheError != null) {
                            notifyEvent("Chartboost Banner FAILED: " + cacheError.toString());
                            notifyStatus("Banner Failed");
                        } else {
                            notifyEvent("Chartboost Banner LOADED successfully");
                            notifyStatus("Banner Loaded");
                            activity.runOnUiThread(() -> chartboostBanner.show());
                        }
                    }

                    @Override
                    public void onAdRequestedToShow(@NonNull ShowEvent showEvent) {
                        notifyEvent("Chartboost Banner Requested to Show");
                    }

                    @Override
                    public void onAdShown(@NonNull ShowEvent showEvent,
                                          @Nullable ShowError showError) {
                        if (showError != null) {
                            notifyEvent("Chartboost Banner Show FAILED: " + showError.toString());
                        } else {
                            notifyEvent("Chartboost Banner SHOWN");
                        }
                    }

                    @Override
                    public void onAdClicked(@NonNull ClickEvent clickEvent,
                                            @Nullable ClickError clickError) {
                        notifyEvent("Chartboost Banner Clicked");
                    }

                    @Override
                    public void onImpressionRecorded(@NonNull ImpressionEvent impressionEvent) {
                        notifyEvent("Chartboost Banner Impression recorded");
                    }
                }, null);
        container.removeAllViews();
        container.addView(chartboostBanner);
        chartboostBanner.cache();
    }

    public void loadInterstitial() {
        notifyEvent("Loading Chartboost Interstitial Ad...");
        chartboostInterstitial = new Interstitial("default", new InterstitialCallback() {
            @Override
            public void onAdLoaded(@NonNull CacheEvent cacheEvent,
                                   @Nullable CacheError cacheError) {
                if (cacheError != null) {
                    notifyEvent("Chartboost Interstitial FAILED: " + cacheError.toString());
                    notifyStatus("Interstitial Failed");
                } else {
                    notifyEvent("Chartboost Interstitial LOADED successfully");
                    notifyStatus("Interstitial Loaded - Ready to Show");
                    listener.onInterstitialReady(true);
                }
            }

            @Override
            public void onAdRequestedToShow(@NonNull ShowEvent showEvent) {
                notifyEvent("Chartboost Interstitial Requested to Show");
            }

            @Override
            public void onAdShown(@NonNull ShowEvent showEvent,
                                  @Nullable ShowError showError) {
                if (showError != null) {
                    notifyEvent("Chartboost Interstitial Show FAILED: " + showError.toString());
                } else {
                    notifyEvent("Chartboost Interstitial SHOWN");
                }
            }

            @Override
            public void onAdClicked(@NonNull ClickEvent clickEvent,
                                    @Nullable ClickError clickError) {
                notifyEvent("Chartboost Interstitial Clicked");
            }

            @Override
            public void onAdDismiss(@NonNull DismissEvent dismissEvent) {
                notifyEvent("Chartboost Interstitial Dismissed");
                listener.onInterstitialReady(false);
            }

            @Override
            public void onImpressionRecorded(@NonNull ImpressionEvent impressionEvent) {
                notifyEvent("Chartboost Interstitial Impression recorded");
            }
        }, null);
        chartboostInterstitial.cache();
    }

    public void showInterstitial() {
        if (chartboostInterstitial != null && chartboostInterstitial.isCached()) {
            chartboostInterstitial.show();
        } else {
            notifyEvent("Chartboost Interstitial not ready");
            notifyStatus("Interstitial not cached yet");
        }
    }

    public void loadRewarded() {
        notifyEvent("Loading Chartboost Rewarded Ad...");
        chartboostRewarded = new Rewarded("default", new RewardedCallback() {
            @Override
            public void onAdLoaded(@NonNull CacheEvent cacheEvent,
                                   @Nullable CacheError cacheError) {
                if (cacheError != null) {
                    notifyEvent("Chartboost Rewarded FAILED: " + cacheError.toString());
                    notifyStatus("Rewarded Failed");
                } else {
                    notifyEvent("Chartboost Rewarded LOADED successfully");
                    notifyStatus("Rewarded Loaded - Ready to Show");
                    listener.onRewardedReady(true);
                }
            }

            @Override
            public void onAdRequestedToShow(@NonNull ShowEvent showEvent) {
                notifyEvent("Chartboost Rewarded Requested to Show");
            }

            @Override
            public void onAdShown(@NonNull ShowEvent showEvent,
                                  @Nullable ShowError showError) {
                if (showError != null) {
                    notifyEvent("Chartboost Rewarded Show FAILED: " + showError.toString());
                } else {
                    notifyEvent("Chartboost Rewarded SHOWN");
                }
            }

            @Override
            public void onAdClicked(@NonNull ClickEvent clickEvent,
                                    @Nullable ClickError clickError) {
                notifyEvent("Chartboost Rewarded Clicked");
            }

            @Override
            public void onAdDismiss(@NonNull DismissEvent dismissEvent) {
                notifyEvent("Chartboost Rewarded Dismissed");
                listener.onRewardedReady(false);
            }

            @Override
            public void onImpressionRecorded(@NonNull ImpressionEvent impressionEvent) {
                notifyEvent("Chartboost Rewarded Impression recorded");
            }

            @Override
            public void onRewardEarned(@NonNull RewardEvent rewardEvent) {
                notifyEvent("Chartboost Reward EARNED!");
            }
        }, null);
        chartboostRewarded.cache();
    }

    public void showRewarded() {
        if (chartboostRewarded != null && chartboostRewarded.isCached()) {
            chartboostRewarded.show();
        } else {
            notifyEvent("Chartboost Rewarded not ready");
            notifyStatus("Rewarded not cached yet");
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
