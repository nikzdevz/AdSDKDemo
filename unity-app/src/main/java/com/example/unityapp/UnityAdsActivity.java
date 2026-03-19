package com.example.unityapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

/**
 * Demonstrates Unity Ads integration with Banner, Interstitial, and Rewarded ads.
 * Uses Unity test Game ID with test mode enabled.
 */
public class UnityAdsActivity extends AppCompatActivity implements IUnityAdsInitializationListener {
    private static final String TAG = "UnityAdsActivity";

    // Unity Ads test Game ID (Android)
    private static final String UNITY_GAME_ID = "14851";
    private static final boolean TEST_MODE = true;
    private static final String INTERSTITIAL_PLACEMENT_ID = "video";
    private static final String REWARDED_PLACEMENT_ID = "rewardedVideo";
    private static final String BANNER_PLACEMENT_ID = "banner";

    private TextView tvStatus;
    private TextView tvLog;
    private Button btnLoadBanner;
    private Button btnLoadInterstitial;
    private Button btnShowInterstitial;
    private Button btnLoadRewarded;
    private Button btnShowRewarded;
    private FrameLayout bannerContainer;

    private boolean interstitialLoaded = false;
    private boolean rewardedLoaded = false;
    private BannerView bannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Unity Ads");
        }

        initViews();
        setupButtons();
        initializeSDK();
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
        tvTitle.setText("Unity Ads");

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

    private void initializeSDK() {
        updateStatus("Initializing Unity Ads...");
        appendLog("Initializing Unity Ads SDK with Game ID: " + UNITY_GAME_ID);
        UnityAds.initialize(getApplicationContext(), UNITY_GAME_ID, TEST_MODE, this);
    }

    @Override
    public void onInitializationComplete() {
        appendLog("Unity Ads SDK INITIALIZED successfully");
        updateStatus("Unity Ads SDK Initialized");
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
        appendLog("Unity Ads Init FAILED: " + error.toString() + " - " + message);
        updateStatus("Init Failed: " + message);
    }

    private void loadBannerAd() {
        appendLog("Loading Unity Banner Ad...");

        if (bannerView != null) {
            bannerView.destroy();
        }

        bannerView = new BannerView(this, BANNER_PLACEMENT_ID, new UnityBannerSize(320, 50));
        bannerView.setListener(new BannerView.IListener() {
            @Override
            public void onBannerLoaded(BannerView bannerAdView) {
                appendLog("Unity Banner LOADED successfully");
                updateStatus("Banner Loaded");
            }

            @Override
            public void onBannerShown(BannerView bannerAdView) {
                appendLog("Unity Banner SHOWN");
            }

            @Override
            public void onBannerClick(BannerView bannerAdView) {
                appendLog("Unity Banner Clicked");
            }

            @Override
            public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
                appendLog("Unity Banner FAILED: " + errorInfo.errorMessage);
                updateStatus("Banner Failed: " + errorInfo.errorMessage);
            }

            @Override
            public void onBannerLeftApplication(BannerView bannerAdView) {
                appendLog("Unity Banner Left Application");
            }
        });

        bannerContainer.removeAllViews();
        bannerContainer.addView(bannerView);
        bannerView.load();
    }

    private void loadInterstitialAd() {
        appendLog("Loading Unity Interstitial Ad...");
        UnityAds.load(INTERSTITIAL_PLACEMENT_ID, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                interstitialLoaded = true;
                runOnUiThread(() -> btnShowInterstitial.setEnabled(true));
                appendLog("Unity Interstitial LOADED: " + placementId);
                updateStatus("Interstitial Loaded - Ready to Show");
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId,
                                                UnityAds.UnityAdsLoadError error, String message) {
                appendLog("Unity Interstitial FAILED: " + error.toString() + " - " + message);
                updateStatus("Interstitial Failed: " + message);
            }
        });
    }

    private void showInterstitialAd() {
        if (interstitialLoaded) {
            UnityAds.show(this, INTERSTITIAL_PLACEMENT_ID, new UnityAdsShowOptions(),
                    new IUnityAdsShowListener() {
                        @Override
                        public void onUnityAdsShowFailure(String placementId,
                                                          UnityAds.UnityAdsShowError error, String message) {
                            appendLog("Unity Interstitial Show FAILED: " + message);
                        }

                        @Override
                        public void onUnityAdsShowStart(String placementId) {
                            appendLog("Unity Interstitial Show Started");
                        }

                        @Override
                        public void onUnityAdsShowClick(String placementId) {
                            appendLog("Unity Interstitial Clicked");
                        }

                        @Override
                        public void onUnityAdsShowComplete(String placementId,
                                                           UnityAds.UnityAdsShowCompletionState state) {
                            appendLog("Unity Interstitial Completed: " + state.toString());
                            interstitialLoaded = false;
                            runOnUiThread(() -> btnShowInterstitial.setEnabled(false));
                        }
                    });
        } else {
            appendLog("Unity Interstitial not ready");
        }
    }

    private void loadRewardedAd() {
        appendLog("Loading Unity Rewarded Ad...");
        UnityAds.load(REWARDED_PLACEMENT_ID, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                rewardedLoaded = true;
                runOnUiThread(() -> btnShowRewarded.setEnabled(true));
                appendLog("Unity Rewarded LOADED: " + placementId);
                updateStatus("Rewarded Loaded - Ready to Show");
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId,
                                                UnityAds.UnityAdsLoadError error, String message) {
                appendLog("Unity Rewarded FAILED: " + error.toString() + " - " + message);
                updateStatus("Rewarded Failed: " + message);
            }
        });
    }

    private void showRewardedAd() {
        if (rewardedLoaded) {
            UnityAds.show(this, REWARDED_PLACEMENT_ID, new UnityAdsShowOptions(),
                    new IUnityAdsShowListener() {
                        @Override
                        public void onUnityAdsShowFailure(String placementId,
                                                          UnityAds.UnityAdsShowError error, String message) {
                            appendLog("Unity Rewarded Show FAILED: " + message);
                        }

                        @Override
                        public void onUnityAdsShowStart(String placementId) {
                            appendLog("Unity Rewarded Show Started");
                        }

                        @Override
                        public void onUnityAdsShowClick(String placementId) {
                            appendLog("Unity Rewarded Clicked");
                        }

                        @Override
                        public void onUnityAdsShowComplete(String placementId,
                                                           UnityAds.UnityAdsShowCompletionState state) {
                            appendLog("Unity Rewarded Completed: " + state.toString());
                            if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                                appendLog("Unity Reward EARNED!");
                            }
                            rewardedLoaded = false;
                            runOnUiThread(() -> btnShowRewarded.setEnabled(false));
                        }
                    });
        } else {
            appendLog("Unity Rewarded not ready");
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
        if (bannerView != null) {
            bannerView.destroy();
        }
        super.onDestroy();
    }
}
