package com.example.admobapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdMobActivity extends AppCompatActivity implements AdMobAdManager.AdEventListener {
    private TextView tvStatus, tvLog;
    private Button btnShowInterstitial, btnShowRewarded, btnShowAppOpen, btnShowRewardedInterstitial;
    private FrameLayout bannerContainer, nativeAdContainer;
    private AdMobAdManager adManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Google AdMob");
        initViews();
        adManager = new AdMobAdManager(this, this);
        setupButtons();
        onStatusChanged("AdMob SDK Ready");
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvLog = findViewById(R.id.tv_log);
        btnShowInterstitial = findViewById(R.id.btn_show_interstitial);
        btnShowRewarded = findViewById(R.id.btn_show_rewarded);
        btnShowAppOpen = findViewById(R.id.btn_show_app_open);
        btnShowRewardedInterstitial = findViewById(R.id.btn_show_rewarded_interstitial);
        bannerContainer = findViewById(R.id.banner_container);
        nativeAdContainer = findViewById(R.id.native_ad_container);
        ((TextView) findViewById(R.id.tv_sdk_title)).setText("Google AdMob");
        findViewById(R.id.btn_load_banner).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_load_interstitial).setVisibility(View.VISIBLE);
        btnShowInterstitial.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_load_rewarded).setVisibility(View.VISIBLE);
        btnShowRewarded.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_load_native).setVisibility(View.VISIBLE);
        nativeAdContainer.setVisibility(View.GONE);
        findViewById(R.id.btn_load_app_open).setVisibility(View.VISIBLE);
        btnShowAppOpen.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_load_rewarded_interstitial).setVisibility(View.VISIBLE);
        btnShowRewardedInterstitial.setVisibility(View.VISIBLE);
    }

    private void setupButtons() {
        findViewById(R.id.btn_load_banner).setOnClickListener(v -> adManager.loadBanner(bannerContainer));
        findViewById(R.id.btn_load_interstitial).setOnClickListener(v -> adManager.loadInterstitial());
        btnShowInterstitial.setOnClickListener(v -> adManager.showInterstitial(this));
        findViewById(R.id.btn_load_rewarded).setOnClickListener(v -> adManager.loadRewarded());
        btnShowRewarded.setOnClickListener(v -> adManager.showRewarded(this));
        findViewById(R.id.btn_load_native).setOnClickListener(v -> adManager.loadNative(this, nativeAdContainer));
        findViewById(R.id.btn_load_app_open).setOnClickListener(v -> adManager.loadAppOpen());
        btnShowAppOpen.setOnClickListener(v -> adManager.showAppOpen(this));
        findViewById(R.id.btn_load_rewarded_interstitial).setOnClickListener(v -> adManager.loadRewardedInterstitial());
        btnShowRewardedInterstitial.setOnClickListener(v -> adManager.showRewardedInterstitial(this));
    }

    @Override public void onAdEvent(String message) {
        runOnUiThread(() -> tvLog.setText(tvLog.getText() + "\n> " + message));
    }
    @Override public void onStatusChanged(String status) {
        runOnUiThread(() -> tvStatus.setText("Status: " + status));
    }
    @Override public void onInterstitialReady(boolean ready) {
        runOnUiThread(() -> btnShowInterstitial.setEnabled(ready));
    }
    @Override public void onRewardedReady(boolean ready) {
        runOnUiThread(() -> btnShowRewarded.setEnabled(ready));
    }
    @Override public void onAppOpenReady(boolean ready) {
        runOnUiThread(() -> btnShowAppOpen.setEnabled(ready));
    }
    @Override public void onRewardedInterstitialReady(boolean ready) {
        runOnUiThread(() -> btnShowRewardedInterstitial.setEnabled(ready));
    }

    @Override
    protected void onDestroy() { adManager.destroy(); super.onDestroy(); }
}
