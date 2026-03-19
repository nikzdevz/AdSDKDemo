package com.example.adsdkdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.Tapjoy;

import java.util.Hashtable;

/**
 * Demonstrates Tapjoy integration with Content/Offerwall ads.
 * Uses Tapjoy test SDK Key.
 */
public class TapjoyActivity extends AppCompatActivity {
    private static final String TAG = "TapjoyActivity";

    // Tapjoy test SDK Key
    private static final String SDK_KEY = "u6SfEbh_TA-WMiGqgQ3W8QECyiQIURFEeKm0zbOggubusy-o5ZfXp33sTXaYLPjyE1gl25Iz3WQ5cIQf";

    private TextView tvStatus;
    private TextView tvLog;
    private Button btnLoadInterstitial;
    private Button btnShowInterstitial;

    private TJPlacement contentPlacement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_demo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tapjoy");
        }

        initViews();
        setupButtons();
        initializeSDK();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvLog = findViewById(R.id.tv_log);
        btnLoadInterstitial = findViewById(R.id.btn_load_interstitial);
        btnShowInterstitial = findViewById(R.id.btn_show_interstitial);

        TextView tvTitle = findViewById(R.id.tv_sdk_title);
        tvTitle.setText("Tapjoy");

        btnLoadInterstitial.setVisibility(View.VISIBLE);
        btnShowInterstitial.setVisibility(View.VISIBLE);
        btnLoadInterstitial.setText("Load Content Placement");
        btnShowInterstitial.setText("Show Content Placement");
    }

    private void setupButtons() {
        btnLoadInterstitial.setOnClickListener(v -> loadContentPlacement());
        btnShowInterstitial.setOnClickListener(v -> showContentPlacement());
    }

    private void initializeSDK() {
        updateStatus("Initializing Tapjoy SDK...");
        appendLog("Initializing Tapjoy SDK...");

        Tapjoy.setDebugEnabled(true);

        Hashtable<String, Object> connectFlags = new Hashtable<>();
        connectFlags.put("TJC_OPTION_ENABLE_LOGGING", "true");

        Tapjoy.connect(getApplicationContext(), SDK_KEY, connectFlags, new TJConnectListener() {
            @Override
            public void onConnectSuccess() {
                appendLog("Tapjoy SDK CONNECTED successfully");
                updateStatus("Tapjoy SDK Connected");
            }

            @Override
            public void onConnectFailure(int code, String message) {
                appendLog("Tapjoy SDK Connection FAILED: " + message + " (code: " + code + ")");
                updateStatus("Tapjoy Connection Failed");
            }
        });
    }

    private void loadContentPlacement() {
        appendLog("Loading Tapjoy Content Placement...");

        contentPlacement = Tapjoy.getPlacement("video_unit", new TJPlacementListener() {
            @Override
            public void onRequestSuccess(TJPlacement placement) {
                appendLog("Tapjoy Placement Request SUCCESS");
                if (placement.isContentAvailable()) {
                    appendLog("Tapjoy Content IS available");
                    updateStatus("Content Available - Ready to Show");
                    runOnUiThread(() -> btnShowInterstitial.setEnabled(true));
                } else {
                    appendLog("Tapjoy Content NOT available");
                    updateStatus("No Content Available");
                }
            }

            @Override
            public void onRequestFailure(TJPlacement placement, TJError error) {
                appendLog("Tapjoy Placement Request FAILED: " + error.message);
                updateStatus("Placement Failed: " + error.message);
            }

            @Override
            public void onContentReady(TJPlacement placement) {
                appendLog("Tapjoy Content READY to display");
                runOnUiThread(() -> btnShowInterstitial.setEnabled(true));
            }

            @Override
            public void onContentShow(TJPlacement placement) {
                appendLog("Tapjoy Content SHOWN");
            }

            @Override
            public void onContentDismiss(TJPlacement placement) {
                appendLog("Tapjoy Content Dismissed");
                runOnUiThread(() -> btnShowInterstitial.setEnabled(false));
            }

            @Override
            public void onPurchaseRequest(TJPlacement placement, TJActionRequest request,
                                          String productId) {
                appendLog("Tapjoy Purchase Request: " + productId);
            }

            @Override
            public void onRewardRequest(TJPlacement placement, TJActionRequest request,
                                        String itemId, int quantity) {
                appendLog("Tapjoy Reward Request: " + itemId + " x" + quantity);
            }

            @Override
            public void onClick(TJPlacement placement) {
                appendLog("Tapjoy Content Clicked");
            }
        });

        contentPlacement.requestContent();
    }

    private void showContentPlacement() {
        if (contentPlacement != null && contentPlacement.isContentReady()) {
            contentPlacement.showContent();
        } else {
            appendLog("Tapjoy Content not ready");
            updateStatus("Content not loaded yet");
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
}
