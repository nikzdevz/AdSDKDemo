package com.example.adsdkdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

/**
 * Demonstrates Google AdMob integration with Banner, Interstitial, and Rewarded ads.
 * Uses official Google test ad unit IDs.
 */
public class AdMobActivity extends AppCompatActivity {
    private static final String TAG = "AdMobActivity";

    // Google-provided test ad unit IDs
    private static final String BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
    private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";

    private TextView tvStatus;
    private TextView tvLog;
    private Button btnLoadBanner;
    private Button btnLoadInterstitial;
    private Button btnShowInterstitial;
    private Button btnLoadRewarded;
    private Button btnShowRewarded;
    private FrameLayout bannerContainer;

    private AdView adView;
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Google AdMob");
        }

        initViews();
        setupButtons();
        updateStatus("Google AdMob SDK Ready (initialized in Application)");
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvLog = findViewById(R.id.tv_log);
        btnLoadBanner = findViewById(R.id.btn_load_banner);
        btnLoadInterstitial = findViewById(R.id.btn_load_interstitial);
        btnShowInterstitial = findViewById(R.id.btn_show_interstitial);
        btnLoadRewarded = findViewById(R.id.btn_load_rewarded);
        btnShowRewarded = findViewById(R.id.btn_show_rewarded);
        bannerContainer = findViewById(R.id.banner_container);

        TextView tvTitle = findViewById(R.id.tv_sdk_title);
        tvTitle.setText("Google AdMob");

        btnLoadBanner.setVisibility(View.VISIBLE);
        btnLoadInterstitial.setVisibility(View.VISIBLE);
        btnShowInterstitial.setVisibility(View.VISIBLE);
        btnLoadRewarded.setVisibility(View.VISIBLE);
        btnShowRewarded.setVisibility(View.VISIBLE);
    }

    private void setupButtons() {
        btnLoadBanner.setOnClickListener(v -> loadBannerAd());
        btnLoadInterstitial.setOnClickListener(v -> loadInterstitialAd());
        btnShowInterstitial.setOnClickListener(v -> showInterstitialAd());
        btnLoadRewarded.setOnClickListener(v -> loadRewardedAd());
        btnShowRewarded.setOnClickListener(v -> showRewardedAd());
    }

    private void loadBannerAd() {
        appendLog("Loading AdMob Banner...");
        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(BANNER_AD_UNIT_ID);

        bannerContainer.removeAllViews();
        bannerContainer.addView(adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdLoaded() {
                appendLog("AdMob Banner LOADED successfully");
                updateStatus("Banner Ad Loaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                appendLog("AdMob Banner FAILED: " + loadAdError.getMessage());
                updateStatus("Banner Ad Failed: " + loadAdError.getMessage());
            }

            @Override
            public void onAdOpened() {
                appendLog("AdMob Banner Opened");
            }

            @Override
            public void onAdClicked() {
                appendLog("AdMob Banner Clicked");
            }

            @Override
            public void onAdClosed() {
                appendLog("AdMob Banner Closed");
            }

            @Override
            public void onAdImpression() {
                appendLog("AdMob Banner Impression recorded");
            }
        });
    }

    private void loadInterstitialAd() {
        appendLog("Loading AdMob Interstitial...");
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, INTERSTITIAL_AD_UNIT_ID, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        interstitialAd = ad;
                        btnShowInterstitial.setEnabled(true);
                        appendLog("AdMob Interstitial LOADED successfully");
                        updateStatus("Interstitial Ad Loaded - Ready to Show");

                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                appendLog("AdMob Interstitial Dismissed");
                                interstitialAd = null;
                                btnShowInterstitial.setEnabled(false);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                appendLog("AdMob Interstitial Show Failed: " + adError.getMessage());
                                interstitialAd = null;
                                btnShowInterstitial.setEnabled(false);
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                appendLog("AdMob Interstitial SHOWN");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        appendLog("AdMob Interstitial FAILED: " + loadAdError.getMessage());
                        updateStatus("Interstitial Failed: " + loadAdError.getMessage());
                        interstitialAd = null;
                    }
                });
    }

    private void showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            appendLog("Interstitial not ready");
            updateStatus("Interstitial not loaded yet");
        }
    }

    private void loadRewardedAd() {
        appendLog("Loading AdMob Rewarded...");
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, REWARDED_AD_UNIT_ID, adRequest,
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        btnShowRewarded.setEnabled(true);
                        appendLog("AdMob Rewarded LOADED successfully");
                        updateStatus("Rewarded Ad Loaded - Ready to Show");

                        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                appendLog("AdMob Rewarded Dismissed");
                                rewardedAd = null;
                                btnShowRewarded.setEnabled(false);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                appendLog("AdMob Rewarded Show Failed: " + adError.getMessage());
                                rewardedAd = null;
                                btnShowRewarded.setEnabled(false);
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                appendLog("AdMob Rewarded SHOWN");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        appendLog("AdMob Rewarded FAILED: " + loadAdError.getMessage());
                        updateStatus("Rewarded Failed: " + loadAdError.getMessage());
                        rewardedAd = null;
                    }
                });
    }

    private void showRewardedAd() {
        if (rewardedAd != null) {
            rewardedAd.show(this, rewardItem -> {
                int rewardAmount = rewardItem.getAmount();
                String rewardType = rewardItem.getType();
                appendLog("AdMob Reward Earned! Type: " + rewardType + ", Amount: " + rewardAmount);
            });
        } else {
            appendLog("Rewarded Ad not ready");
            updateStatus("Rewarded not loaded yet");
        }
    }

    private void updateStatus(String status) {
        runOnUiThread(() -> tvStatus.setText("Status: " + status));
    }

    private void appendLog(String message) {
        Log.d(TAG, message);
        runOnUiThread(() -> {
            String current = tvLog.getText().toString();
            tvLog.setText(current + "\n> " + message);
        });
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
