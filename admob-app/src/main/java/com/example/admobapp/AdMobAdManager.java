package com.example.admobapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

/**
 * Manages Google AdMob ad loading, showing, and lifecycle events.
 * Supports Banner, Interstitial, and Rewarded ad formats.
 */
public class AdMobAdManager {
    private static final String TAG = "AdMobAdManager";

    // Google-provided test ad unit IDs
    private static final String BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
    private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";

    private final Context context;
    private final AdEventListener listener;

    private AdView adView;
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;

    public interface AdEventListener {
        void onAdEvent(String message);
        void onStatusChanged(String status);
        void onInterstitialReady(boolean ready);
        void onRewardedReady(boolean ready);
    }

    public AdMobAdManager(Context context, AdEventListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void loadBanner(ViewGroup container) {
        notifyEvent("Loading AdMob Banner...");
        adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(BANNER_AD_UNIT_ID);

        container.removeAllViews();
        container.addView(adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdLoaded() {
                notifyEvent("AdMob Banner LOADED successfully");
                notifyStatus("Banner Ad Loaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                notifyEvent("AdMob Banner FAILED: " + loadAdError.getMessage());
                notifyStatus("Banner Ad Failed: " + loadAdError.getMessage());
            }

            @Override
            public void onAdOpened() {
                notifyEvent("AdMob Banner Opened");
            }

            @Override
            public void onAdClicked() {
                notifyEvent("AdMob Banner Clicked");
            }

            @Override
            public void onAdClosed() {
                notifyEvent("AdMob Banner Closed");
            }

            @Override
            public void onAdImpression() {
                notifyEvent("AdMob Banner Impression recorded");
            }
        });
    }

    public void loadInterstitial() {
        notifyEvent("Loading AdMob Interstitial...");
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context, INTERSTITIAL_AD_UNIT_ID, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        interstitialAd = ad;
                        listener.onInterstitialReady(true);
                        notifyEvent("AdMob Interstitial LOADED successfully");
                        notifyStatus("Interstitial Ad Loaded - Ready to Show");

                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                notifyEvent("AdMob Interstitial Dismissed");
                                interstitialAd = null;
                                listener.onInterstitialReady(false);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                notifyEvent("AdMob Interstitial Show Failed: " + adError.getMessage());
                                interstitialAd = null;
                                listener.onInterstitialReady(false);
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                notifyEvent("AdMob Interstitial SHOWN");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        notifyEvent("AdMob Interstitial FAILED: " + loadAdError.getMessage());
                        notifyStatus("Interstitial Failed: " + loadAdError.getMessage());
                        interstitialAd = null;
                    }
                });
    }

    public void showInterstitial(Activity activity) {
        if (interstitialAd != null) {
            interstitialAd.show(activity);
        } else {
            notifyEvent("Interstitial not ready");
            notifyStatus("Interstitial not loaded yet");
        }
    }

    public void loadRewarded() {
        notifyEvent("Loading AdMob Rewarded...");
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(context, REWARDED_AD_UNIT_ID, adRequest,
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        listener.onRewardedReady(true);
                        notifyEvent("AdMob Rewarded LOADED successfully");
                        notifyStatus("Rewarded Ad Loaded - Ready to Show");

                        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                notifyEvent("AdMob Rewarded Dismissed");
                                rewardedAd = null;
                                listener.onRewardedReady(false);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                notifyEvent("AdMob Rewarded Show Failed: " + adError.getMessage());
                                rewardedAd = null;
                                listener.onRewardedReady(false);
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                notifyEvent("AdMob Rewarded SHOWN");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        notifyEvent("AdMob Rewarded FAILED: " + loadAdError.getMessage());
                        notifyStatus("Rewarded Failed: " + loadAdError.getMessage());
                        rewardedAd = null;
                    }
                });
    }

    public void showRewarded(Activity activity) {
        if (rewardedAd != null) {
            rewardedAd.show(activity, rewardItem -> {
                int rewardAmount = rewardItem.getAmount();
                String rewardType = rewardItem.getType();
                notifyEvent("AdMob Reward Earned! Type: " + rewardType + ", Amount: " + rewardAmount);
            });
        } else {
            notifyEvent("Rewarded Ad not ready");
            notifyStatus("Rewarded not loaded yet");
        }
    }

    public void destroy() {
        if (adView != null) {
            adView.destroy();
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
