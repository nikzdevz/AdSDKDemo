package com.example.tapjoyapp;

import android.content.Context;
import android.util.Log;

import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.Tapjoy;

import java.util.Hashtable;

/**
 * Manages Tapjoy ad loading, showing, and lifecycle events.
 * Supports Content Placement ad format.
 */
public class TapjoyAdManager {
    private static final String TAG = "TapjoyAdManager";
    private static final String SDK_KEY = "u6SfEbh_TA-WMiGqgQ3W8QECyiQIURFEeKm0zbOggubusy-o5ZfXp33sTXaYLPjyE1gl25Iz3WQ5cIQf";

    private final Context context;
    private final AdEventListener listener;
    private TJPlacement contentPlacement;

    public interface AdEventListener {
        void onAdEvent(String message);
        void onStatusChanged(String status);
        void onContentReady(boolean ready);
    }

    public TapjoyAdManager(Context context, AdEventListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void initialize() {
        notifyStatus("Initializing Tapjoy SDK...");
        notifyEvent("Initializing Tapjoy SDK...");
        Tapjoy.setDebugEnabled(true);
        Hashtable<String, Object> connectFlags = new Hashtable<>();
        connectFlags.put("TJC_OPTION_ENABLE_LOGGING", "true");
        Tapjoy.connect(context, SDK_KEY, connectFlags, new TJConnectListener() {
            @Override
            public void onConnectSuccess() {
                notifyEvent("Tapjoy SDK CONNECTED successfully");
                notifyStatus("Tapjoy SDK Connected");
            }

            @Override
            public void onConnectFailure(int code, String message) {
                notifyEvent("Tapjoy SDK Connection FAILED: " + message + " (code: " + code + ")");
                notifyStatus("Tapjoy Connection Failed");
            }
        });
    }

    public void loadContent() {
        notifyEvent("Loading Tapjoy Content Placement...");
        contentPlacement = Tapjoy.getPlacement("video_unit", new TJPlacementListener() {
            @Override
            public void onRequestSuccess(TJPlacement placement) {
                notifyEvent("Tapjoy Placement Request SUCCESS");
                if (placement.isContentAvailable()) {
                    notifyEvent("Tapjoy Content IS available");
                    notifyStatus("Content Available - Ready to Show");
                    listener.onContentReady(true);
                } else {
                    notifyEvent("Tapjoy Content NOT available");
                    notifyStatus("No Content Available");
                }
            }

            @Override
            public void onRequestFailure(TJPlacement placement, TJError error) {
                notifyEvent("Tapjoy Placement Request FAILED: " + error.message);
                notifyStatus("Placement Failed: " + error.message);
            }

            @Override
            public void onContentReady(TJPlacement placement) {
                notifyEvent("Tapjoy Content READY to display");
                listener.onContentReady(true);
            }

            @Override
            public void onContentShow(TJPlacement placement) {
                notifyEvent("Tapjoy Content SHOWN");
            }

            @Override
            public void onContentDismiss(TJPlacement placement) {
                notifyEvent("Tapjoy Content Dismissed");
                listener.onContentReady(false);
            }

            @Override
            public void onPurchaseRequest(TJPlacement placement, TJActionRequest request,
                                          String productId) {
                notifyEvent("Tapjoy Purchase Request: " + productId);
            }

            @Override
            public void onRewardRequest(TJPlacement placement, TJActionRequest request,
                                        String itemId, int quantity) {
                notifyEvent("Tapjoy Reward Request: " + itemId + " x" + quantity);
            }

            @Override
            public void onClick(TJPlacement placement) {
                notifyEvent("Tapjoy Content Clicked");
            }
        });
        contentPlacement.requestContent();
    }

    public void showContent() {
        if (contentPlacement != null && contentPlacement.isContentReady()) {
            contentPlacement.showContent();
        } else {
            notifyEvent("Tapjoy Content not ready");
            notifyStatus("Content not loaded yet");
        }
    }

    private void notifyEvent(String message) {
        Log.d(TAG, message);
        listener.onAdEvent(message);
    }

    private void notifyStatus(String status) {
        listener.onStatusChanged(status);
    }
}
