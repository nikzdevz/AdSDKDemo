package com.example.inmobiapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InMobiActivity extends AppCompatActivity implements InMobiAdManager.AdEventListener {
    private TextView tvStatus, tvLog;
    private Button btnShowInterstitial;
    private FrameLayout bannerContainer;
    private InMobiAdManager adManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("InMobi");
        initViews();
        adManager = new InMobiAdManager(this, this);
        setupButtons();
        adManager.initialize();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvLog = findViewById(R.id.tv_log);
        btnShowInterstitial = findViewById(R.id.btn_show_interstitial);
        bannerContainer = findViewById(R.id.banner_container);
        ((TextView) findViewById(R.id.tv_sdk_title)).setText("InMobi");
        findViewById(R.id.btn_load_banner).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_load_interstitial).setVisibility(View.VISIBLE);
        btnShowInterstitial.setVisibility(View.VISIBLE);
    }

    private void setupButtons() {
        findViewById(R.id.btn_load_banner).setOnClickListener(v -> adManager.loadBanner(this, bannerContainer));
        findViewById(R.id.btn_load_interstitial).setOnClickListener(v -> adManager.loadInterstitial(this));
        btnShowInterstitial.setOnClickListener(v -> adManager.showInterstitial());
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
    protected void onDestroy() { adManager.destroy(); super.onDestroy(); }
}
