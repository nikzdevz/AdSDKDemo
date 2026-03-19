package com.example.chartboostapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ads.Banner;
import com.chartboost.sdk.ads.Interstitial;
import com.chartboost.sdk.ads.Rewarded;
import com.chartboost.sdk.callbacks.BannerCallback;
import com.chartboost.sdk.callbacks.InterstitialCallback;
import com.chartboost.sdk.callbacks.RewardedCallback;
import com.chartboost.sdk.callbacks.StartCallback;
import com.chartboost.sdk.events.CacheError;
import com.chartboost.sdk.events.CacheEvent;
import com.chartboost.sdk.events.ClickError;
import com.chartboost.sdk.events.ClickEvent;
import com.chartboost.sdk.events.DismissEvent;
import com.chartboost.sdk.events.ImpressionEvent;
import com.chartboost.sdk.events.RewardEvent;
import com.chartboost.sdk.events.ShowError;
import com.chartboost.sdk.events.ShowEvent;
import com.chartboost.sdk.events.StartError;

/**
 * Demonstrates Chartboost integration with Banner, Interstitial, and Rewarded ads.
 * Uses Chartboost test App ID and App Signature.
 */
public class ChartboostActivity extends AppCompatActivity {
    private static final String TAG = "ChartboostActivity";

    // Chartboost test App ID and Signature
    private static final String APP_ID = "4f21c409cd1cb2fb7000001b";
    private static final String APP_SIGNATURE = "92e2de2fd7070327d881571f904c275107e0d2c5";

    private TextView tvStatus;
    private TextView tvLog;
    private Button btnLoadBanner;
    private Button btnLoadInterstitial;
    private Button btnShowInterstitial;
    private Button btnLoadRewarded;
    private Button btnShowRewarded;
    private FrameLayout bannerContainer;

    private Banner chartboostBanner;
    private Interstitial chartboostInterstitial;
    private Rewarded chartboostRewarded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chartboost");
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
        tvTitle.setText("Chartboost");

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
        updateStatus("Initializing Chartboost SDK...");
        appendLog("Initializing Chartboost SDK with App ID: " + APP_ID);

        Chartboost.startWithAppId(getApplicationContext(), APP_ID, APP_SIGNATURE, startError -> {
            if (startError == null) {
                appendLog("Chartboost SDK INITIALIZED successfully");
                updateStatus("Chartboost SDK Initialized");
            } else {
                appendLog("Chartboost SDK Init FAILED: " + startError.getCode().name());
                updateStatus("Init Failed: " + startError.getCode().name());
            }
        });
    }

    private void loadBannerAd() {
        appendLog("Loading Chartboost Banner Ad...");

        if (chartboostBanner != null) {
            chartboostBanner.detach();
        }

        chartboostBanner = new Banner(this, "default", Banner.BannerSize.STANDARD,
                new BannerCallback() {
                    @Override
                    public void onAdLoaded(@NonNull CacheEvent cacheEvent, @Nullable CacheError cacheError) {
                        if (cacheError != null) {
                            appendLog("Chartboost Banner FAILED: " + cacheError.toString());
                            updateStatus("Banner Failed");
                        } else {
                            appendLog("Chartboost Banner LOADED successfully");
                            updateStatus("Banner Loaded");
                            runOnUiThread(() -> chartboostBanner.show());
                        }
                    }

                    @Override
                    public void onAdRequestedToShow(@NonNull ShowEvent showEvent) {
                        appendLog("Chartboost Banner Requested to Show");
                    }

                    @Override
                    public void onAdShown(@NonNull ShowEvent showEvent, @Nullable ShowError showError) {
                        if (showError != null) {
                            appendLog("Chartboost Banner Show FAILED: " + showError.toString());
                        } else {
                            appendLog("Chartboost Banner SHOWN");
                        }
                    }

                    @Override
                    public void onAdClicked(@NonNull ClickEvent clickEvent, @Nullable ClickError clickError) {
                        appendLog("Chartboost Banner Clicked");
                    }

                    @Override
                    public void onImpressionRecorded(@NonNull ImpressionEvent impressionEvent) {
                        appendLog("Chartboost Banner Impression recorded");
                    }
                }, null);

        bannerContainer.removeAllViews();
        bannerContainer.addView(chartboostBanner);
        chartboostBanner.cache();
    }

    private void loadInterstitialAd() {
        appendLog("Loading Chartboost Interstitial Ad...");

        chartboostInterstitial = new Interstitial("default",
                new InterstitialCallback() {
                    @Override
                    public void onAdLoaded(@NonNull CacheEvent cacheEvent, @Nullable CacheError cacheError) {
                        if (cacheError != null) {
                            appendLog("Chartboost Interstitial FAILED: " + cacheError.toString());
                            updateStatus("Interstitial Failed");
                        } else {
                            appendLog("Chartboost Interstitial LOADED successfully");
                            updateStatus("Interstitial Loaded - Ready to Show");
                            runOnUiThread(() -> btnShowInterstitial.setEnabled(true));
                        }
                    }

                    @Override
                    public void onAdRequestedToShow(@NonNull ShowEvent showEvent) {
                        appendLog("Chartboost Interstitial Requested to Show");
                    }

                    @Override
                    public void onAdShown(@NonNull ShowEvent showEvent, @Nullable ShowError showError) {
                        if (showError != null) {
                            appendLog("Chartboost Interstitial Show FAILED: " + showError.toString());
                        } else {
                            appendLog("Chartboost Interstitial SHOWN");
                        }
                    }

                    @Override
                    public void onAdClicked(@NonNull ClickEvent clickEvent, @Nullable ClickError clickError) {
                        appendLog("Chartboost Interstitial Clicked");
                    }

                    @Override
                    public void onAdDismiss(@NonNull DismissEvent dismissEvent) {
                        appendLog("Chartboost Interstitial Dismissed");
                        runOnUiThread(() -> btnShowInterstitial.setEnabled(false));
                    }

                    @Override
                    public void onImpressionRecorded(@NonNull ImpressionEvent impressionEvent) {
                        appendLog("Chartboost Interstitial Impression recorded");
                    }
                }, null);

        chartboostInterstitial.cache();
    }

    private void showInterstitialAd() {
        if (chartboostInterstitial != null && chartboostInterstitial.isCached()) {
            chartboostInterstitial.show();
        } else {
            appendLog("Chartboost Interstitial not ready");
            updateStatus("Interstitial not cached yet");
        }
    }

    private void loadRewardedAd() {
        appendLog("Loading Chartboost Rewarded Ad...");

        chartboostRewarded = new Rewarded("default",
                new RewardedCallback() {
                    @Override
                    public void onAdLoaded(@NonNull CacheEvent cacheEvent, @Nullable CacheError cacheError) {
                        if (cacheError != null) {
                            appendLog("Chartboost Rewarded FAILED: " + cacheError.toString());
                            updateStatus("Rewarded Failed");
                        } else {
                            appendLog("Chartboost Rewarded LOADED successfully");
                            updateStatus("Rewarded Loaded - Ready to Show");
                            runOnUiThread(() -> btnShowRewarded.setEnabled(true));
                        }
                    }

                    @Override
                    public void onAdRequestedToShow(@NonNull ShowEvent showEvent) {
                        appendLog("Chartboost Rewarded Requested to Show");
                    }

                    @Override
                    public void onAdShown(@NonNull ShowEvent showEvent, @Nullable ShowError showError) {
                        if (showError != null) {
                            appendLog("Chartboost Rewarded Show FAILED: " + showError.toString());
                        } else {
                            appendLog("Chartboost Rewarded SHOWN");
                        }
                    }

                    @Override
                    public void onAdClicked(@NonNull ClickEvent clickEvent, @Nullable ClickError clickError) {
                        appendLog("Chartboost Rewarded Clicked");
                    }

                    @Override
                    public void onAdDismiss(@NonNull DismissEvent dismissEvent) {
                        appendLog("Chartboost Rewarded Dismissed");
                        runOnUiThread(() -> btnShowRewarded.setEnabled(false));
                    }

                    @Override
                    public void onImpressionRecorded(@NonNull ImpressionEvent impressionEvent) {
                        appendLog("Chartboost Rewarded Impression recorded");
                    }

                    @Override
                    public void onRewardEarned(@NonNull RewardEvent rewardEvent) {
                        appendLog("Chartboost Reward EARNED!");
                    }
                }, null);

        chartboostRewarded.cache();
    }

    private void showRewardedAd() {
        if (chartboostRewarded != null && chartboostRewarded.isCached()) {
            chartboostRewarded.show();
        } else {
            appendLog("Chartboost Rewarded not ready");
            updateStatus("Rewarded not cached yet");
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
}
