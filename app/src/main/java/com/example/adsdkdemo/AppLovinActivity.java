package com.example.adsdkdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
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
 * Demonstrates AppLovin MAX integration with Banner, Interstitial, and Rewarded ads.
 * Uses AppLovin test ad unit IDs.
 */
public class AppLovinActivity extends AppCompatActivity {
    private static final String TAG = "AppLovinActivity";

    // AppLovin MAX demo ad unit IDs
    private static final String BANNER_AD_UNIT_ID = "YOUR_BANNER_AD_UNIT_ID";
    private static final String INTERSTITIAL_AD_UNIT_ID = "YOUR_INTERSTITIAL_AD_UNIT_ID";
    private static final String REWARDED_AD_UNIT_ID = "YOUR_REWARDED_AD_UNIT_ID";

    private TextView tvStatus;
    private TextView tvLog;
    private Button btnLoadBanner;
    private Button btnLoadInterstitial;
    private Button btnShowInterstitial;
    private Button btnLoadRewarded;
    private Button btnShowRewarded;
    private FrameLayout bannerContainer;

    private MaxAdView maxAdView;
    private MaxInterstitialAd maxInterstitialAd;
    private MaxRewardedAd maxRewardedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("AppLovin MAX");
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
        tvTitle.setText("AppLovin MAX");

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
        updateStatus("Initializing AppLovin SDK...");
        appendLog("Initializing AppLovin MAX SDK...");

        AppLovinSdk.getInstance(this).setMediationProvider("max");
        AppLovinSdk.getInstance(this).initializeSdk(configuration -> {
            appendLog("AppLovin MAX SDK INITIALIZED successfully");
            appendLog("Country code: " + configuration.getCountryCode());
            updateStatus("AppLovin MAX SDK Initialized");
        });
    }

    private void loadBannerAd() {
        appendLog("Loading AppLovin Banner Ad...");

        if (maxAdView != null) {
            maxAdView.destroy();
        }

        maxAdView = new MaxAdView(BANNER_AD_UNIT_ID, this);
        maxAdView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                appendLog("AppLovin Banner LOADED successfully");
                updateStatus("Banner Loaded");
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                appendLog("AppLovin Banner Displayed");
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                appendLog("AppLovin Banner Hidden");
            }

            @Override
            public void onAdClicked(MaxAd ad) {
                appendLog("AppLovin Banner Clicked");
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                appendLog("AppLovin Banner FAILED: " + error.getMessage());
                updateStatus("Banner Failed: " + error.getMessage());
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                appendLog("AppLovin Banner Display Failed: " + error.getMessage());
            }

            @Override
            public void onAdExpanded(MaxAd ad) {
                appendLog("AppLovin Banner Expanded");
            }

            @Override
            public void onAdCollapsed(MaxAd ad) {
                appendLog("AppLovin Banner Collapsed");
            }
        });

        int heightDp = 50;
        int heightPx = (int) (heightDp * getResources().getDisplayMetrics().density);
        maxAdView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, heightPx));

        bannerContainer.removeAllViews();
        bannerContainer.addView(maxAdView);
        maxAdView.loadAd();
    }

    private void loadInterstitialAd() {
        appendLog("Loading AppLovin Interstitial Ad...");

        maxInterstitialAd = new MaxInterstitialAd(INTERSTITIAL_AD_UNIT_ID, this);
        maxInterstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                appendLog("AppLovin Interstitial LOADED");
                updateStatus("Interstitial Loaded - Ready to Show");
                runOnUiThread(() -> btnShowInterstitial.setEnabled(true));
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                appendLog("AppLovin Interstitial DISPLAYED");
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                appendLog("AppLovin Interstitial Hidden");
                runOnUiThread(() -> btnShowInterstitial.setEnabled(false));
            }

            @Override
            public void onAdClicked(MaxAd ad) {
                appendLog("AppLovin Interstitial Clicked");
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                appendLog("AppLovin Interstitial FAILED: " + error.getMessage());
                updateStatus("Interstitial Failed: " + error.getMessage());
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                appendLog("AppLovin Interstitial Display Failed: " + error.getMessage());
            }
        });

        maxInterstitialAd.loadAd();
    }

    private void showInterstitialAd() {
        if (maxInterstitialAd != null && maxInterstitialAd.isReady()) {
            maxInterstitialAd.showAd();
        } else {
            appendLog("AppLovin Interstitial not ready");
        }
    }

    private void loadRewardedAd() {
        appendLog("Loading AppLovin Rewarded Ad...");

        maxRewardedAd = MaxRewardedAd.getInstance(REWARDED_AD_UNIT_ID, this);
        maxRewardedAd.setListener(new MaxRewardedAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                appendLog("AppLovin Rewarded LOADED");
                updateStatus("Rewarded Loaded - Ready to Show");
                runOnUiThread(() -> btnShowRewarded.setEnabled(true));
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                appendLog("AppLovin Rewarded DISPLAYED");
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                appendLog("AppLovin Rewarded Hidden");
                runOnUiThread(() -> btnShowRewarded.setEnabled(false));
            }

            @Override
            public void onAdClicked(MaxAd ad) {
                appendLog("AppLovin Rewarded Clicked");
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                appendLog("AppLovin Rewarded FAILED: " + error.getMessage());
                updateStatus("Rewarded Failed: " + error.getMessage());
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                appendLog("AppLovin Rewarded Display Failed: " + error.getMessage());
            }

            @Override
            public void onUserRewarded(MaxAd ad, MaxReward reward) {
                appendLog("AppLovin Reward EARNED! Label: " + reward.getLabel()
                        + ", Amount: " + reward.getAmount());
            }
        });

        maxRewardedAd.loadAd();
    }

    private void showRewardedAd() {
        if (maxRewardedAd != null && maxRewardedAd.isReady()) {
            maxRewardedAd.showAd();
        } else {
            appendLog("AppLovin Rewarded not ready");
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
        if (maxAdView != null) {
            maxAdView.destroy();
        }
        if (maxInterstitialAd != null) {
            maxInterstitialAd.destroy();
        }
        if (maxRewardedAd != null) {
            maxRewardedAd.destroy();
        }
        super.onDestroy();
    }
}
