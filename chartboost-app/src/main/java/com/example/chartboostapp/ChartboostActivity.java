package com.example.chartboostapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChartboostActivity extends AppCompatActivity implements ChartboostAdManager.AdEventListener {
    private TextView tvStatus, tvLog;
    private Button btnShowInterstitial, btnShowRewarded;
    private FrameLayout bannerContainer;
    private ChartboostAdManager adManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Chartboost");
        initViews();
        adManager = new ChartboostAdManager(getApplicationContext(), this);
        setupButtons();
        adManager.initialize();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvLog = findViewById(R.id.tv_log);
        btnShowInterstitial = findViewById(R.id.btn_show_interstitial);
        btnShowRewarded = findViewById(R.id.btn_show_rewarded);
        bannerContainer = findViewById(R.id.banner_container);
        ((TextView) findViewById(R.id.tv_sdk_title)).setText("Chartboost");
        findViewById(R.id.btn_load_banner).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_load_interstitial).setVisibility(View.VISIBLE);
        btnShowInterstitial.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_load_rewarded).setVisibility(View.VISIBLE);
        btnShowRewarded.setVisibility(View.VISIBLE);
    }

    private void setupButtons() {
        findViewById(R.id.btn_load_banner).setOnClickListener(v -> adManager.loadBanner(this, bannerContainer));
        findViewById(R.id.btn_load_interstitial).setOnClickListener(v -> adManager.loadInterstitial());
        btnShowInterstitial.setOnClickListener(v -> adManager.showInterstitial());
        findViewById(R.id.btn_load_rewarded).setOnClickListener(v -> adManager.loadRewarded());
        btnShowRewarded.setOnClickListener(v -> adManager.showRewarded());
    }

    @Override
    public void onAdEvent(String message) {
        runOnUiThread(() -> tvLog.setText(tvLog.getText() + "\n> " + message));
    }

    @Override
    public void onStatusChanged(String status) {
        runOnUiThread(() -> tvStatus.setText("Status: " + status));
    }

    @Override
    public void onInterstitialReady(boolean ready) {
        runOnUiThread(() -> btnShowInterstitial.setEnabled(ready));
    }

    @Override
    public void onRewardedReady(boolean ready) {
        runOnUiThread(() -> btnShowRewarded.setEnabled(ready));
    }
}
