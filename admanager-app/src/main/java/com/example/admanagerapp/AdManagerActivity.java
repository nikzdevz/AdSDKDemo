package com.example.admanagerapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity that demonstrates Google Ad Manager ads using AdManagerAdHelper.
 */
public class AdManagerActivity extends AppCompatActivity implements AdManagerAdHelper.AdEventListener {

    private TextView tvStatus;
    private TextView tvLog;
    private Button btnShowInterstitial;
    private FrameLayout bannerContainer;

    private AdManagerAdHelper adHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Google Ad Manager");
        }

        initViews();
        adHelper = new AdManagerAdHelper(this, this);
        setupButtons();
        onStatusChanged("Google Ad Manager SDK Ready");
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvLog = findViewById(R.id.tv_log);
        btnShowInterstitial = findViewById(R.id.btn_show_interstitial);
        bannerContainer = findViewById(R.id.banner_container);

        TextView tvTitle = findViewById(R.id.tv_sdk_title);
        tvTitle.setText("Google Ad Manager");

        findViewById(R.id.btn_load_banner).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_load_interstitial).setVisibility(View.VISIBLE);
        btnShowInterstitial.setVisibility(View.VISIBLE);
    }

    private void setupButtons() {
        findViewById(R.id.btn_load_banner).setOnClickListener(v -> adHelper.loadBanner(bannerContainer));
        findViewById(R.id.btn_load_interstitial).setOnClickListener(v -> adHelper.loadInterstitial());
        btnShowInterstitial.setOnClickListener(v -> adHelper.showInterstitial(this));
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
    protected void onDestroy() {
        adHelper.destroy();
        super.onDestroy();
    }
}
