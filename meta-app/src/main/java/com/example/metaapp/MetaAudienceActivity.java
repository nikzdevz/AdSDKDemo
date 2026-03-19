package com.example.metaapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

/**
 * Demonstrates Meta Audience Network (Facebook) integration with Banner and Interstitial ads.
 * Uses test mode for safe testing.
 */
public class MetaAudienceActivity extends AppCompatActivity {
    private static final String TAG = "MetaAudienceActivity";

    // Meta test placement IDs
    private static final String BANNER_PLACEMENT_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
    private static final String INTERSTITIAL_PLACEMENT_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";

    private TextView tvStatus;
    private TextView tvLog;
    private Button btnLoadBanner;
    private Button btnLoadInterstitial;
    private Button btnShowInterstitial;
    private FrameLayout bannerContainer;

    private AdView adView;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Meta Audience Network");
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
        bannerContainer = findViewById(R.id.banner_container);

        TextView tvTitle = findViewById(R.id.tv_sdk_title);
        tvTitle.setText("Meta Audience Network (Facebook)");

        btnLoadBanner.setVisibility(View.VISIBLE);
        btnLoadInterstitial.setVisibility(View.VISIBLE);
        btnShowInterstitial.setVisibility(View.VISIBLE);
    }

    private void setupButtons() {
        btnLoadBanner.setOnClickListener(v -> loadBannerAd());
        btnLoadInterstitial.setOnClickListener(v -> loadInterstitialAd());
        btnShowInterstitial.setOnClickListener(v -> showInterstitialAd());
    }

    private void initializeSDK() {
        updateStatus("Initializing Meta Audience Network...");
        appendLog("Initializing Meta Audience Network SDK...");

        // Enable test mode for development
        AdSettings.setTestMode(true);
        appendLog("Test mode enabled");

        AudienceNetworkAds.initialize(this);
        updateStatus("Meta Audience Network SDK Initialized");
        appendLog("Meta Audience Network SDK initialized successfully");
    }

    private void loadBannerAd() {
        appendLog("Loading Meta Banner Ad...");

        if (adView != null) {
            adView.destroy();
        }

        adView = new AdView(this, BANNER_PLACEMENT_ID, AdSize.BANNER_HEIGHT_50);

        bannerContainer.removeAllViews();
        bannerContainer.addView(adView);

        com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                appendLog("Meta Banner FAILED: " + adError.getErrorMessage()
                        + " (Code: " + adError.getErrorCode() + ")");
                updateStatus("Banner Failed: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                appendLog("Meta Banner LOADED successfully");
                updateStatus("Banner Ad Loaded");
            }

            @Override
            public void onAdClicked(Ad ad) {
                appendLog("Meta Banner Clicked");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                appendLog("Meta Banner Impression logged");
            }
        };

        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
    }

    private void loadInterstitialAd() {
        appendLog("Loading Meta Interstitial Ad...");

        if (interstitialAd != null) {
            interstitialAd.destroy();
        }

        interstitialAd = new InterstitialAd(this, INTERSTITIAL_PLACEMENT_ID);

        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                appendLog("Meta Interstitial FAILED: " + adError.getErrorMessage()
                        + " (Code: " + adError.getErrorCode() + ")");
                updateStatus("Interstitial Failed: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                appendLog("Meta Interstitial LOADED successfully");
                updateStatus("Interstitial Loaded - Ready to Show");
                btnShowInterstitial.setEnabled(true);
            }

            @Override
            public void onAdClicked(Ad ad) {
                appendLog("Meta Interstitial Clicked");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                appendLog("Meta Interstitial Impression logged");
            }

            @Override
            public void onInterstitialDisplayed(Ad ad) {
                appendLog("Meta Interstitial DISPLAYED");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                appendLog("Meta Interstitial Dismissed");
                btnShowInterstitial.setEnabled(false);
            }
        };

        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    private void showInterstitialAd() {
        if (interstitialAd != null && interstitialAd.isAdLoaded()) {
            interstitialAd.show();
        } else {
            appendLog("Interstitial not ready");
            updateStatus("Interstitial not loaded yet");
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
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }
}
