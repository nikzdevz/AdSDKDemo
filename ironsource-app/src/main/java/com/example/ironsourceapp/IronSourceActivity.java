package com.example.ironsourceapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class IronSourceActivity extends AppCompatActivity implements IronSourceAdManager.AdEventListener {
    private TextView tvStatus, tvLog;
    private Button btnShowInterstitial, btnShowRewarded;
    private IronSourceAdManager adManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("ironSource LevelPlay");
        initViews();
        adManager = new IronSourceAdManager(this);
        setupButtons();
        adManager.initialize(this);
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvLog = findViewById(R.id.tv_log);
        btnShowInterstitial = findViewById(R.id.btn_show_interstitial);
        btnShowRewarded = findViewById(R.id.btn_show_rewarded);
        ((TextView) findViewById(R.id.tv_sdk_title)).setText("ironSource (Unity LevelPlay)");
        findViewById(R.id.btn_load_interstitial).setVisibility(View.VISIBLE);
        btnShowInterstitial.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_load_rewarded).setVisibility(View.VISIBLE);
        btnShowRewarded.setVisibility(View.VISIBLE);
    }

    private void setupButtons() {
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

    @Override protected void onResume() { super.onResume(); adManager.onResume(this); }
    @Override protected void onPause() { super.onPause(); adManager.onPause(this); }
}
