package com.example.admanagerapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;

/**
 * Demonstrates Google Ad Manager integration with Banner and Interstitial ads.
 * Uses official Google test ad unit IDs for Ad Manager.
 */
public class AdManagerActivity extends AppCompatActivity {
    private static final String TAG = "AdManagerActivity";

    // Google Ad Manager test ad unit IDs
    private static final String BANNER_AD_UNIT_ID = "/6499/example/banner";
    private static final String INTERSTITIAL_AD_UNIT_ID = "/6499/example/interstitial";

    private TextView tvStatus;
    private TextView tvLog;
    private Button btnLoadBanner;
    private Button btnLoadInterstitial;
    private Button btnShowInterstitial;
    private FrameLayout bannerContainer;

    private AdManagerAdView adManagerAdView;
    private AdManagerInterstitialAd adManagerInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Google Ad Manager");
        }

        initViews();
        setupButtons();
        updateStatus("Google Ad Manager SDK Ready");
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvLog = findViewById(R.id.tv_log);
        btnLoadBanner = findViewById(R.id.btn_load_banner);
        btnLoadInterstitial = findViewById(R.id.btn_load_interstitial);
        btnShowInterstitial = findViewById(R.id.btn_show_interstitial);
        bannerContainer = findViewById(R.id.banner_container);

        TextView tvTitle = findViewById(R.id.tv_sdk_title);
        tvTitle.setText("Google Ad Manager");

        btnLoadBanner.setVisibility(View.VISIBLE);
        btnLoadInterstitial.setVisibility(View.VISIBLE);
        btnShowInterstitial.setVisibility(View.VISIBLE);
    }

    private void setupButtons() {
        btnLoadBanner.setOnClickListener(v -> loadBannerAd());
        btnLoadInterstitial.setOnClickListener(v -> loadInterstitialAd());
        btnShowInterstitial.setOnClickListener(v -> showInterstitialAd());
    }

    private void loadBannerAd() {
        appendLog("Loading Ad Manager Banner...");
        adManagerAdView = new AdManagerAdView(this);
        adManagerAdView.setAdSizes(AdSize.BANNER);
        adManagerAdView.setAdUnitId(BANNER_AD_UNIT_ID);

        bannerContainer.removeAllViews();
        bannerContainer.addView(adManagerAdView);

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        adManagerAdView.loadAd(adRequest);

        adManagerAdView.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdLoaded() {
                appendLog("Ad Manager Banner LOADED successfully");
                updateStatus("Banner Ad Loaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                appendLog("Ad Manager Banner FAILED: " + loadAdError.getMessage());
                updateStatus("Banner Failed: " + loadAdError.getMessage());
            }

            @Override
            public void onAdOpened() {
                appendLog("Ad Manager Banner Opened");
            }

            @Override
            public void onAdClicked() {
                appendLog("Ad Manager Banner Clicked");
            }

            @Override
            public void onAdClosed() {
                appendLog("Ad Manager Banner Closed");
            }

            @Override
            public void onAdImpression() {
                appendLog("Ad Manager Banner Impression recorded");
            }
        });
    }

    private void loadInterstitialAd() {
        appendLog("Loading Ad Manager Interstitial...");
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();

        AdManagerInterstitialAd.load(this, INTERSTITIAL_AD_UNIT_ID, adRequest,
                new AdManagerInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AdManagerInterstitialAd ad) {
                        adManagerInterstitialAd = ad;
                        btnShowInterstitial.setEnabled(true);
                        appendLog("Ad Manager Interstitial LOADED successfully");
                        updateStatus("Interstitial Ad Loaded - Ready to Show");

                        adManagerInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                appendLog("Ad Manager Interstitial Dismissed");
                                adManagerInterstitialAd = null;
                                btnShowInterstitial.setEnabled(false);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                appendLog("Ad Manager Interstitial Show Failed: " + adError.getMessage());
                                adManagerInterstitialAd = null;
                                btnShowInterstitial.setEnabled(false);
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                appendLog("Ad Manager Interstitial SHOWN");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        appendLog("Ad Manager Interstitial FAILED: " + loadAdError.getMessage());
                        updateStatus("Interstitial Failed: " + loadAdError.getMessage());
                        adManagerInterstitialAd = null;
                    }
                });
    }

    private void showInterstitialAd() {
        if (adManagerInterstitialAd != null) {
            adManagerInterstitialAd.show(this);
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
        if (adManagerAdView != null) {
            adManagerAdView.destroy();
        }
        super.onDestroy();
    }
}
