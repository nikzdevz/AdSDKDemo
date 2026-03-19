package com.example.metaapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity that demonstrates Meta Audience Network ads using MetaAdManager.
 */
public class MetaAudienceActivity extends AppCompatActivity implements MetaAdManager.AdEventListener {

    private TextView tvStatus;
    private TextView tvLog;
    private Button btnShowInterstitial;
    private FrameLayout bannerContainer;

    private MetaAdManager adManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Meta Audience Network");
        }

        initViews();
        adManager = new MetaAdManager(this, this);
        setupButtons();
        adManager.initialize();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvLog = findViewById(R.id.tv_log);
        btnShowInterstitial = findViewById(R.id.btn_show_interstitial);
        bannerContainer = findViewById(R.id.banner_container);

        TextView tvTitle = findViewById(R.id.tv_sdk_title);
        tvTitle.setText("Meta Audience Network (Facebook)");

        findViewById(R.id.btn_load_banner).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_load_interstitial).setVisibility(View.VISIBLE);
        btnShowInterstitial.setVisibility(View.VISIBLE);
    }

    private void setupButtons() {
        findViewById(R.id.btn_load_banner).setOnClickListener(v -> adManager.loadBanner(bannerContainer));
        findViewById(R.id.btn_load_interstitial).setOnClickListener(v -> adManager.loadInterstitial());
        btnShowInterstitial.setOnClickListener(v -> adManager.showInterstitial());
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
        adManager.destroy();
        super.onDestroy();
    }
}
