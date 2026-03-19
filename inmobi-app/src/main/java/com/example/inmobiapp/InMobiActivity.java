package com.example.inmobiapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.inmobi.ads.AdMetaInfo;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.ads.listeners.BannerAdEventListener;
import com.inmobi.ads.listeners.InterstitialAdEventListener;
import com.inmobi.sdk.InMobiSdk;
import com.inmobi.sdk.SdkInitializationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Demonstrates InMobi integration with Banner and Interstitial ads.
 * Uses InMobi test account ID and test placement IDs.
 */
public class InMobiActivity extends AppCompatActivity {
    private static final String TAG = "InMobiActivity";

    // InMobi test account ID
    private static final String ACCOUNT_ID = "4028cb8b2c3a0b45012c406824e800ba";
    // InMobi test placement IDs
    private static final long BANNER_PLACEMENT_ID = 1685041492873L;
    private static final long INTERSTITIAL_PLACEMENT_ID = 1685049498974L;

    private TextView tvStatus;
    private TextView tvLog;
    private Button btnLoadBanner;
    private Button btnLoadInterstitial;
    private Button btnShowInterstitial;
    private FrameLayout bannerContainer;

    private InMobiBanner inMobiBanner;
    private InMobiInterstitial inMobiInterstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("InMobi");
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
        tvTitle.setText("InMobi");

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
        updateStatus("Initializing InMobi SDK...");
        appendLog("Initializing InMobi SDK with Account ID: " + ACCOUNT_ID);

        JSONObject consentObject = new JSONObject();
        try {
            consentObject.put("gdpr_consent_available", true);
            consentObject.put("gdpr", "0");
        } catch (JSONException e) {
            Log.e(TAG, "Error creating consent JSON", e);
        }

        InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);
        InMobiSdk.init(this, ACCOUNT_ID, consentObject, new SdkInitializationListener() {
            @Override
            public void onInitializationComplete(Error error) {
                if (error == null) {
                    appendLog("InMobi SDK INITIALIZED successfully");
                    updateStatus("InMobi SDK Initialized");
                } else {
                    appendLog("InMobi SDK Init FAILED: " + error.getMessage());
                    updateStatus("Init Failed: " + error.getMessage());
                }
            }
        });
    }

    private void loadBannerAd() {
        appendLog("Loading InMobi Banner Ad...");

        if (inMobiBanner != null) {
            bannerContainer.removeAllViews();
        }

        inMobiBanner = new InMobiBanner(this, BANNER_PLACEMENT_ID);
        inMobiBanner.setAnimationType(InMobiBanner.AnimationType.ROTATE_HORIZONTAL_AXIS);
        inMobiBanner.setEnableAutoRefresh(false);

        inMobiBanner.setListener(new BannerAdEventListener() {
            @Override
            public void onAdLoadSucceeded(InMobiBanner ad, AdMetaInfo info) {
                appendLog("InMobi Banner LOADED successfully");
                updateStatus("Banner Loaded");
            }

            @Override
            public void onAdLoadFailed(InMobiBanner ad, InMobiAdRequestStatus status) {
                appendLog("InMobi Banner FAILED: " + status.getMessage()
                        + " (Code: " + status.getStatusCode() + ")");
                updateStatus("Banner Failed: " + status.getMessage());
            }

            @Override
            public void onAdDisplayed(InMobiBanner ad) {
                appendLog("InMobi Banner Displayed");
            }

            @Override
            public void onAdDismissed(InMobiBanner ad) {
                appendLog("InMobi Banner Dismissed");
            }

            @Override
            public void onAdClicked(InMobiBanner ad, Map<Object, Object> params) {
                appendLog("InMobi Banner Clicked");
            }

            @Override
            public void onUserLeftApplication(InMobiBanner ad) {
                appendLog("InMobi Banner - User Left Application");
            }

            @Override
            public void onAdImpression(InMobiBanner ad) {
                appendLog("InMobi Banner Impression recorded");
            }

            @Override
            public void onRewardsUnlocked(InMobiBanner ad, Map<Object, Object> rewards) {
                appendLog("InMobi Banner Rewards Unlocked");
            }
        });

        int heightPx = (int) (50 * getResources().getDisplayMetrics().density);
        inMobiBanner.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, heightPx));

        bannerContainer.removeAllViews();
        bannerContainer.addView(inMobiBanner);
        inMobiBanner.load();
    }

    private void loadInterstitialAd() {
        appendLog("Loading InMobi Interstitial Ad...");

        inMobiInterstitial = new InMobiInterstitial(this, INTERSTITIAL_PLACEMENT_ID,
                new InterstitialAdEventListener() {
                    @Override
                    public void onAdLoadSucceeded(InMobiInterstitial ad, AdMetaInfo info) {
                        appendLog("InMobi Interstitial LOADED successfully");
                        updateStatus("Interstitial Loaded - Ready to Show");
                        runOnUiThread(() -> btnShowInterstitial.setEnabled(true));
                    }

                    @Override
                    public void onAdLoadFailed(InMobiInterstitial ad, InMobiAdRequestStatus status) {
                        appendLog("InMobi Interstitial FAILED: " + status.getMessage()
                                + " (Code: " + status.getStatusCode() + ")");
                        updateStatus("Interstitial Failed: " + status.getMessage());
                    }

                    @Override
                    public void onAdWillDisplay(InMobiInterstitial ad) {
                        appendLog("InMobi Interstitial Will Display");
                    }

                    @Override
                    public void onAdDisplayed(InMobiInterstitial ad, AdMetaInfo info) {
                        appendLog("InMobi Interstitial DISPLAYED");
                    }

                    @Override
                    public void onAdDisplayFailed(InMobiInterstitial ad) {
                        appendLog("InMobi Interstitial Display Failed");
                    }

                    @Override
                    public void onAdDismissed(InMobiInterstitial ad) {
                        appendLog("InMobi Interstitial Dismissed");
                        runOnUiThread(() -> btnShowInterstitial.setEnabled(false));
                    }

                    @Override
                    public void onAdClicked(InMobiInterstitial ad, Map<Object, Object> params) {
                        appendLog("InMobi Interstitial Clicked");
                    }

                    @Override
                    public void onUserLeftApplication(InMobiInterstitial ad) {
                        appendLog("InMobi Interstitial - User Left Application");
                    }

                    @Override
                    public void onAdImpression(InMobiInterstitial ad) {
                        appendLog("InMobi Interstitial Impression recorded");
                    }

                    @Override
                    public void onRewardsUnlocked(InMobiInterstitial ad, Map<Object, Object> rewards) {
                        appendLog("InMobi Interstitial Rewards Unlocked");
                    }
                });

        inMobiInterstitial.load();
    }

    private void showInterstitialAd() {
        if (inMobiInterstitial != null && inMobiInterstitial.isReady()) {
            inMobiInterstitial.show();
        } else {
            appendLog("InMobi Interstitial not ready");
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
        if (inMobiBanner != null) {
            inMobiBanner.destroy();
        }
        super.onDestroy();
    }
}
