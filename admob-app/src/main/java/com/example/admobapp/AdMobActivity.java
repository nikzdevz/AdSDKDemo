package com.example.admobapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity that demonstrates Google AdMob ads using AdMobAdManager.
 */
public class AdMobActivity extends AppCompatActivity implements AdMobAdManager.AdEventListener {

    private TextView tvStatus;
    private TextView tvLog;
    private Button btnShowInterstitial;
    private Button btnShowRewarded;
    private FrameLayout bannerContainer;

    private AdMobAdManager adManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Google AdMob");
        }

        initViews();
        adManager = new AdMobAdManager(this, this);
        setupButtons();
        onStatusChanged("Google AdMob SDK Ready (initialized in Application)");
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvLog = findViewById(R.id.tv_log);
        btnShowInterstitial = findViewById(R.id.btn_show_interstitial);
        btnShowRewarded = findViewById(R.id.btn_show_rewarded);
        bannerContainer = findViewById(R.id.banner_container);

        TextView tvTitle = findViewById(R.id.tv_sdk_title);
        tvTitle.setText("Google AdMob");

        findViewById(R.id.btn_load_banner).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_load_interstitial).setVisibility(View.VISIBLE);
        btnShowInterstitial.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_load_rewarded).setVisibility(View.VISIBLE);
        btnShowRewarded.setVisibility(View.VISIBLE);
    }

    private void setupButtons() {
        findViewById(R.id.btn_load_banner).setOnClickListener(v -> adManager.loadBanner(bannerContainer));
        findViewById(R.id.btn_load_interstitial).setOnClickListener(v -> adManager.loadInterstitial());
        btnShowInterstitial.setOnClickListener(v -> adManager.showInterstitial(this));
        findViewById(R.id.btn_load_rewarded).setOnClickListener(v -> adManager.loadRewarded());
        btnShowRewarded.setOnClickListener(v -> adManager.showRewarded(this));
    }

    @Override
    public void onAdEvent(String message) {
        runOnUiThread(() -> {
            String current = tvLog.getText().toString();
            tvLog.setText(current + "\n> " + message);
        });
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

    @Override
    protected void onDestroy() {
        adManager.destroy();
        super.onDestroy();
    }
}
