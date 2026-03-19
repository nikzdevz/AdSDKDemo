package com.example.tapjoyapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TapjoyActivity extends AppCompatActivity implements TapjoyAdManager.AdEventListener {
    private TextView tvStatus, tvLog;
    private Button btnShowContent;
    private TapjoyAdManager adManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Tapjoy");
        initViews();
        adManager = new TapjoyAdManager(getApplicationContext(), this);
        setupButtons();
        adManager.initialize();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvLog = findViewById(R.id.tv_log);
        Button btnLoadContent = findViewById(R.id.btn_load_interstitial);
        btnShowContent = findViewById(R.id.btn_show_interstitial);
        ((TextView) findViewById(R.id.tv_sdk_title)).setText("Tapjoy");
        btnLoadContent.setVisibility(View.VISIBLE);
        btnShowContent.setVisibility(View.VISIBLE);
        btnLoadContent.setText("Load Content Placement");
        btnShowContent.setText("Show Content Placement");
    }

    private void setupButtons() {
        findViewById(R.id.btn_load_interstitial).setOnClickListener(v -> adManager.loadContent());
        btnShowContent.setOnClickListener(v -> adManager.showContent());
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
    public void onContentReady(boolean ready) {
        runOnUiThread(() -> btnShowContent.setEnabled(ready));
    }
}
