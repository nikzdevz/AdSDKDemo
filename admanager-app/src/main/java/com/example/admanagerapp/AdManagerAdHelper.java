package com.example.admanagerapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

public class AdManagerAdHelper {
    private static final String TAG = "AdManagerAdHelper";
    private static final String BANNER_AD_UNIT_ID = "/6499/example/banner";
    private static final String INTERSTITIAL_AD_UNIT_ID = "/6499/example/interstitial";
    private static final String NATIVE_AD_UNIT_ID = "/6499/example/native";
    private static final String REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final String APP_OPEN_AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921";
    private static final String REWARDED_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/5354046379";

    private final Context context;
    private final AdEventListener listener;
    private AdManagerAdView adManagerAdView;
    private AdManagerInterstitialAd adManagerInterstitialAd;
    private RewardedAd rewardedAd;
    private NativeAd nativeAd;
    private AppOpenAd appOpenAd;
    private RewardedInterstitialAd rewardedInterstitialAd;

    public interface AdEventListener {
        void onAdEvent(String message);
        void onStatusChanged(String status);
        void onInterstitialReady(boolean ready);
        void onRewardedReady(boolean ready);
        void onAppOpenReady(boolean ready);
        void onRewardedInterstitialReady(boolean ready);
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
        adManagerAdView.loadAd(new AdManagerAdRequest.Builder().build());
        adManagerAdView.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override public void onAdLoaded() { notifyEvent("Banner LOADED"); notifyStatus("Banner Loaded"); }
            @Override public void onAdFailedToLoad(@NonNull LoadAdError e) { notifyEvent("Banner FAILED: " + e.getMessage()); }
            @Override public void onAdOpened() { notifyEvent("Banner Opened"); }
            @Override public void onAdClicked() { notifyEvent("Banner Clicked"); }
            @Override public void onAdClosed() { notifyEvent("Banner Closed"); }
            @Override public void onAdImpression() { notifyEvent("Banner Impression"); }
        });
    }

    public void loadInterstitial() {
        notifyEvent("Loading Ad Manager Interstitial...");
        AdManagerInterstitialAd.load(context, INTERSTITIAL_AD_UNIT_ID, new AdManagerAdRequest.Builder().build(),
            new AdManagerInterstitialAdLoadCallback() {
                @Override public void onAdLoaded(@NonNull AdManagerInterstitialAd ad) {
                    adManagerInterstitialAd = ad;
                    listener.onInterstitialReady(true);
                    notifyEvent("Interstitial LOADED"); notifyStatus("Interstitial Ready");
                    ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override public void onAdDismissedFullScreenContent() { notifyEvent("Interstitial Dismissed"); adManagerInterstitialAd = null; listener.onInterstitialReady(false); }
                        @Override public void onAdFailedToShowFullScreenContent(@NonNull AdError e) { notifyEvent("Interstitial Show Failed"); adManagerInterstitialAd = null; listener.onInterstitialReady(false); }
                        @Override public void onAdShowedFullScreenContent() { notifyEvent("Interstitial SHOWN"); }
                    });
                }
                @Override public void onAdFailedToLoad(@NonNull LoadAdError e) { notifyEvent("Interstitial FAILED: " + e.getMessage()); adManagerInterstitialAd = null; }
            });
    }

    public void showInterstitial(Activity activity) {
        if (adManagerInterstitialAd != null) adManagerInterstitialAd.show(activity);
        else notifyEvent("Interstitial not ready");
    }

    public void loadRewarded() {
        notifyEvent("Loading Ad Manager Rewarded...");
        RewardedAd.load(context, REWARDED_AD_UNIT_ID, new AdManagerAdRequest.Builder().build(),
            new RewardedAdLoadCallback() {
                @Override public void onAdLoaded(@NonNull RewardedAd ad) {
                    rewardedAd = ad;
                    listener.onRewardedReady(true);
                    notifyEvent("Rewarded LOADED"); notifyStatus("Rewarded Ready");
                    ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override public void onAdDismissedFullScreenContent() { notifyEvent("Rewarded Dismissed"); rewardedAd = null; listener.onRewardedReady(false); }
                        @Override public void onAdFailedToShowFullScreenContent(@NonNull AdError e) { notifyEvent("Rewarded Show Failed"); rewardedAd = null; listener.onRewardedReady(false); }
                        @Override public void onAdShowedFullScreenContent() { notifyEvent("Rewarded SHOWN"); }
                    });
                }
                @Override public void onAdFailedToLoad(@NonNull LoadAdError e) { notifyEvent("Rewarded FAILED: " + e.getMessage()); rewardedAd = null; }
            });
    }

    public void showRewarded(Activity activity) {
        if (rewardedAd != null) rewardedAd.show(activity, r -> notifyEvent("Reward! Type:" + r.getType() + " Amount:" + r.getAmount()));
        else notifyEvent("Rewarded not ready");
    }

    public void loadNative(Activity activity, ViewGroup container) {
        notifyEvent("Loading Ad Manager Native...");
        AdLoader adLoader = new AdLoader.Builder(context, NATIVE_AD_UNIT_ID)
            .forNativeAd(ad -> {
                if (nativeAd != null) nativeAd.destroy();
                nativeAd = ad;
                notifyEvent("Native LOADED"); notifyStatus("Native Loaded");
                displayNativeAd(activity, container, ad);
            })
            .withAdListener(new com.google.android.gms.ads.AdListener() {
                @Override public void onAdFailedToLoad(@NonNull LoadAdError e) { notifyEvent("Native FAILED: " + e.getMessage()); }
                @Override public void onAdClicked() { notifyEvent("Native Clicked"); }
                @Override public void onAdImpression() { notifyEvent("Native Impression"); }
            }).build();
        adLoader.loadAd(new AdManagerAdRequest.Builder().build());
    }

    private void displayNativeAd(Activity activity, ViewGroup container, NativeAd ad) {
        activity.runOnUiThread(() -> {
            NativeAdView nv = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.native_ad_layout, null);
            TextView hl = nv.findViewById(R.id.ad_headline); hl.setText(ad.getHeadline()); nv.setHeadlineView(hl);
            TextView bd = nv.findViewById(R.id.ad_body);
            if (ad.getBody() != null) { bd.setText(ad.getBody()); bd.setVisibility(View.VISIBLE); } else bd.setVisibility(View.GONE);
            nv.setBodyView(bd);
            Button cta = nv.findViewById(R.id.ad_call_to_action);
            if (ad.getCallToAction() != null) { cta.setText(ad.getCallToAction()); cta.setVisibility(View.VISIBLE); } else cta.setVisibility(View.GONE);
            nv.setCallToActionView(cta);
            ImageView icon = nv.findViewById(R.id.ad_app_icon);
            if (ad.getIcon() != null) { icon.setImageDrawable(ad.getIcon().getDrawable()); icon.setVisibility(View.VISIBLE); } else icon.setVisibility(View.GONE);
            nv.setIconView(icon);
            TextView adv = nv.findViewById(R.id.ad_advertiser);
            if (ad.getAdvertiser() != null) { adv.setText(ad.getAdvertiser()); adv.setVisibility(View.VISIBLE); } else adv.setVisibility(View.GONE);
            nv.setAdvertiserView(adv);
            MediaView mv = nv.findViewById(R.id.ad_media); nv.setMediaView(mv);
            RatingBar rb = nv.findViewById(R.id.ad_stars);
            if (ad.getStarRating() != null) { rb.setRating(ad.getStarRating().floatValue()); rb.setVisibility(View.VISIBLE); } else rb.setVisibility(View.GONE);
            nv.setStarRatingView(rb);
            nv.setNativeAd(ad);
            container.removeAllViews(); container.addView(nv); container.setVisibility(View.VISIBLE);
        });
    }

    public void loadAppOpen() {
        notifyEvent("Loading Ad Manager App Open...");
        AppOpenAd.load(context, APP_OPEN_AD_UNIT_ID, new AdManagerAdRequest.Builder().build(),
            new AppOpenAd.AppOpenAdLoadCallback() {
                @Override public void onAdLoaded(@NonNull AppOpenAd ad) {
                    appOpenAd = ad;
                    listener.onAppOpenReady(true);
                    notifyEvent("App Open LOADED"); notifyStatus("App Open Ready");
                    ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override public void onAdDismissedFullScreenContent() { notifyEvent("App Open Dismissed"); appOpenAd = null; listener.onAppOpenReady(false); }
                        @Override public void onAdFailedToShowFullScreenContent(@NonNull AdError e) { notifyEvent("App Open Show Failed"); appOpenAd = null; listener.onAppOpenReady(false); }
                        @Override public void onAdShowedFullScreenContent() { notifyEvent("App Open SHOWN"); }
                    });
                }
                @Override public void onAdFailedToLoad(@NonNull LoadAdError e) { notifyEvent("App Open FAILED: " + e.getMessage()); appOpenAd = null; }
            });
    }

    public void showAppOpen(Activity activity) {
        if (appOpenAd != null) appOpenAd.show(activity);
        else notifyEvent("App Open not ready");
    }

    public void loadRewardedInterstitial() {
        notifyEvent("Loading Ad Manager Rewarded Interstitial...");
        RewardedInterstitialAd.load(context, REWARDED_INTERSTITIAL_AD_UNIT_ID, new AdManagerAdRequest.Builder().build(),
            new RewardedInterstitialAdLoadCallback() {
                @Override public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                    rewardedInterstitialAd = ad;
                    listener.onRewardedInterstitialReady(true);
                    notifyEvent("Rewarded Interstitial LOADED"); notifyStatus("Rewarded Interstitial Ready");
                    ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override public void onAdDismissedFullScreenContent() { notifyEvent("Rewarded Interstitial Dismissed"); rewardedInterstitialAd = null; listener.onRewardedInterstitialReady(false); }
                        @Override public void onAdFailedToShowFullScreenContent(@NonNull AdError e) { notifyEvent("Rewarded Interstitial Show Failed"); rewardedInterstitialAd = null; listener.onRewardedInterstitialReady(false); }
                        @Override public void onAdShowedFullScreenContent() { notifyEvent("Rewarded Interstitial SHOWN"); }
                    });
                }
                @Override public void onAdFailedToLoad(@NonNull LoadAdError e) { notifyEvent("Rewarded Interstitial FAILED: " + e.getMessage()); rewardedInterstitialAd = null; }
            });
    }

    public void showRewardedInterstitial(Activity activity) {
        if (rewardedInterstitialAd != null) rewardedInterstitialAd.show(activity, r -> notifyEvent("Rewarded Interstitial Reward! Type:" + r.getType() + " Amount:" + r.getAmount()));
        else notifyEvent("Rewarded Interstitial not ready");
    }

    public void destroy() {
        if (adManagerAdView != null) adManagerAdView.destroy();
        if (nativeAd != null) nativeAd.destroy();
    }

    private void notifyEvent(String msg) { Log.d(TAG, msg); listener.onAdEvent(msg); }
    private void notifyStatus(String s) { listener.onStatusChanged(s); }
}
