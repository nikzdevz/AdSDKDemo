package com.example.metaapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MetaAudienceActivity extends AppCompatActivity implements MetaAdManager.AdEventListener {
    private TextView tvStatus, tvLog;
    private Button btnShowInterstitial, btnShowRewarded, btnShowRewardedInterstitial;
    private FrameLayout bannerContainer, nativeAdContainer;
    private MetaAdManager adManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Meta Audience Network");
        initViews();
        adManager = new MetaAdManager(this, this);
        setupButtons();
        adManager.initialize();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvLog = findViewById(R.id.tv_log);
        btnShowInterstitial = findViewById(R.id.btn_show_interstitial);
        btnShowRewarded = findViewById(R.id.btn_show_rewarded);
        btnShowRewardedInterstitial = findViewById(R.id.btn_show_rewarded_interstitial);
        bannerContainer = findViewById(R.id.banner_container);
        nativeAdContainer = findViewById(R.id.native_ad_container);
        ((TextView) findViewById(R.id.tv_sdk_title)).setText("Meta Audience Network (Facebook)");
        findViewById(R.id.btn_load_banner).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_load_interstitial).setVisibility(View.VISIBLE);
        btnShowInterstitial.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_load_native).setVisibility(View.VISIBLE);
        nativeAdContainer.setVisibility(View.GONE);
        findViewById(R.id.btn_load_rewarded).setVisibility(View.VISIBLE);
        btnShowRewarded.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_load_rewarded_interstitial).setVisibility(View.VISIBLE);
        btnShowRewardedInterstitial.setVisibility(View.VISIBLE);
        // Relabel rewarded button for Meta
        ((Button) findViewById(R.id.btn_load_rewarded)).setText("Load Rewarded Video");
        btnShowRewarded.setText("Show Rewarded Video");
    }

    private void setupButtons() {
        findViewById(R.id.btn_load_banner).setOnClickListener(v -> adManager.loadBanner(bannerContainer));
        findViewById(R.id.btn_load_interstitial).setOnClickListener(v -> adManager.loadInterstitial());
        btnShowInterstitial.setOnClickListener(v -> adManager.showInterstitial());
        findViewById(R.id.btn_load_native).setOnClickListener(v -> adManager.loadNative(this, nativeAdContainer));
        findViewById(R.id.btn_load_rewarded).setOnClickListener(v -> adManager.loadRewardedVideo());
        btnShowRewarded.setOnClickListener(v -> adManager.showRewardedVideo());
        findViewById(R.id.btn_load_rewarded_interstitial).setOnClickListener(v -> adManager.loadRewardedInterstitial());
        btnShowRewardedInterstitial.setOnClickListener(v -> adManager.showRewardedInterstitial());
    }

    @Override public void onAdEvent(String message) { runOnUiThread(() -> tvLog.setText(tvLog.getText() + "\n> " + message)); }
    @Override public void onStatusChanged(String status) { runOnUiThread(() -> tvStatus.setText("Status: " + status)); }
    @Override public void onInterstitialReady(boolean ready) { runOnUiThread(() -> btnShowInterstitial.setEnabled(ready)); }
    @Override public void onRewardedReady(boolean ready) { runOnUiThread(() -> btnShowRewarded.setEnabled(ready)); }
    @Override public void onRewardedInterstitialReady(boolean ready) { runOnUiThread(() -> btnShowRewardedInterstitial.setEnabled(ready)); }

    @Override
    protected void onDestroy() { adManager.destroy(); super.onDestroy(); }
}
