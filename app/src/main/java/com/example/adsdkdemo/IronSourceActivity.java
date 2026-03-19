package com.example.adsdkdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoListener;

/**
 * Demonstrates ironSource (Unity LevelPlay) integration with Interstitial and Rewarded ads.
 * Uses ironSource test App Key.
 */
public class IronSourceActivity extends AppCompatActivity {
    private static final String TAG = "IronSourceActivity";

    // ironSource demo/test App Key
    private static final String APP_KEY = "85460dcd";

    private TextView tvStatus;
    private TextView tvLog;
    private Button btnLoadInterstitial;
    private Button btnShowInterstitial;
    private Button btnLoadRewarded;
    private Button btnShowRewarded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("ironSource LevelPlay");
        }

        initViews();
        setupButtons();
        initializeSDK();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvLog = findViewById(R.id.tv_log);
        btnLoadInterstitial = findViewById(R.id.btn_load_interstitial);
        btnShowInterstitial = findViewById(R.id.btn_show_interstitial);
        btnLoadRewarded = findViewById(R.id.btn_load_rewarded);
        btnShowRewarded = findViewById(R.id.btn_show_rewarded);

        TextView tvTitle = findViewById(R.id.tv_sdk_title);
        tvTitle.setText("ironSource (Unity LevelPlay)");

        btnLoadInterstitial.setVisibility(View.VISIBLE);
        btnShowInterstitial.setVisibility(View.VISIBLE);
        btnLoadRewarded.setVisibility(View.VISIBLE);
        btnShowRewarded.setVisibility(View.VISIBLE);
    }

    private void setupButtons() {
        btnLoadInterstitial.setOnClickListener(v -> loadInterstitialAd());
        btnShowInterstitial.setOnClickListener(v -> showInterstitialAd());
        btnLoadRewarded.setOnClickListener(v -> loadRewardedAd());
        btnShowRewarded.setOnClickListener(v -> showRewardedAd());
    }

    private void initializeSDK() {
        updateStatus("Initializing ironSource SDK...");
        appendLog("Initializing ironSource SDK with App Key: " + APP_KEY);

        setupRewardedListener();
        setupInterstitialListener();

        IronSource.setAdaptersDebug(true);
        IronSource.init(this, APP_KEY, () -> {
            appendLog("ironSource SDK INITIALIZED successfully");
            updateStatus("ironSource SDK Initialized");
        });
    }

    private void setupRewardedListener() {
        IronSource.setLevelPlayRewardedVideoListener(new LevelPlayRewardedVideoListener() {
            @Override
            public void onAdAvailable(AdInfo adInfo) {
                appendLog("ironSource Rewarded Ad Available");
                updateStatus("Rewarded Ad Available");
                runOnUiThread(() -> btnShowRewarded.setEnabled(true));
            }

            @Override
            public void onAdUnavailable() {
                appendLog("ironSource Rewarded Ad Unavailable");
                runOnUiThread(() -> btnShowRewarded.setEnabled(false));
            }

            @Override
            public void onAdOpened(AdInfo adInfo) {
                appendLog("ironSource Rewarded Ad Opened");
            }

            @Override
            public void onAdClosed(AdInfo adInfo) {
                appendLog("ironSource Rewarded Ad Closed");
                runOnUiThread(() -> btnShowRewarded.setEnabled(false));
            }

            @Override
            public void onAdRewarded(Placement placement, AdInfo adInfo) {
                appendLog("ironSource Reward EARNED! " + placement.getRewardName()
                        + " x" + placement.getRewardAmount());
            }

            @Override
            public void onAdShowFailed(IronSourceError error, AdInfo adInfo) {
                appendLog("ironSource Rewarded Show FAILED: " + error.getErrorMessage());
            }

            @Override
            public void onAdClicked(Placement placement, AdInfo adInfo) {
                appendLog("ironSource Rewarded Clicked");
            }
        });
    }

    private void setupInterstitialListener() {
        IronSource.setLevelPlayInterstitialListener(new LevelPlayInterstitialListener() {
            @Override
            public void onAdReady(AdInfo adInfo) {
                appendLog("ironSource Interstitial READY");
                updateStatus("Interstitial Ready - Can Show");
                runOnUiThread(() -> btnShowInterstitial.setEnabled(true));
            }

            @Override
            public void onAdLoadFailed(IronSourceError error) {
                appendLog("ironSource Interstitial FAILED: " + error.getErrorMessage());
                updateStatus("Interstitial Failed: " + error.getErrorMessage());
            }

            @Override
            public void onAdOpened(AdInfo adInfo) {
                appendLog("ironSource Interstitial Opened");
            }

            @Override
            public void onAdClosed(AdInfo adInfo) {
                appendLog("ironSource Interstitial Closed");
                runOnUiThread(() -> btnShowInterstitial.setEnabled(false));
            }

            @Override
            public void onAdShowFailed(IronSourceError error, AdInfo adInfo) {
                appendLog("ironSource Interstitial Show FAILED: " + error.getErrorMessage());
            }

            @Override
            public void onAdClicked(AdInfo adInfo) {
                appendLog("ironSource Interstitial Clicked");
            }

            @Override
            public void onAdShowSucceeded(AdInfo adInfo) {
                appendLog("ironSource Interstitial SHOWN successfully");
            }
        });
    }

    private void loadInterstitialAd() {
        appendLog("Loading ironSource Interstitial...");
        IronSource.loadInterstitial();
    }

    private void showInterstitialAd() {
        if (IronSource.isInterstitialReady()) {
            IronSource.showInterstitial();
        } else {
            appendLog("ironSource Interstitial not ready");
            updateStatus("Interstitial not loaded yet");
        }
    }

    private void loadRewardedAd() {
        appendLog("Loading ironSource Rewarded Video...");
        appendLog("Note: ironSource Rewarded Videos auto-load. Checking availability...");
        if (IronSource.isRewardedVideoAvailable()) {
            appendLog("ironSource Rewarded Video is AVAILABLE");
            runOnUiThread(() -> btnShowRewarded.setEnabled(true));
        } else {
            appendLog("ironSource Rewarded Video not available yet");
        }
    }

    private void showRewardedAd() {
        if (IronSource.isRewardedVideoAvailable()) {
            IronSource.showRewardedVideo();
        } else {
            appendLog("ironSource Rewarded Video not available");
            updateStatus("Rewarded not available");
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
    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }
}
