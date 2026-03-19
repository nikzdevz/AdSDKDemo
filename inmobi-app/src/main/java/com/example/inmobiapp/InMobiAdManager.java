package com.example.inmobiapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.inmobi.ads.AdMetaInfo;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.ads.listeners.BannerAdEventListener;
import com.inmobi.ads.listeners.InterstitialAdEventListener;
import com.inmobi.sdk.InMobiSdk;
import com.inmobi.sdk.SdkInitializationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Manages InMobi ad loading, showing, and lifecycle events.
 * Supports Banner and Interstitial ad formats.
 */
public class InMobiAdManager {
    private static final String TAG = "InMobiAdManager";
    private static final String ACCOUNT_ID = "4028cb8b2c3a0b45012c406824e800ba";
    private static final long BANNER_PLACEMENT_ID = 1685041492873L;
    private static final long INTERSTITIAL_PLACEMENT_ID = 1685049498974L;

    private final Context context;
    private final AdEventListener listener;
    private InMobiBanner inMobiBanner;
    private InMobiInterstitial inMobiInterstitial;

    public interface AdEventListener {
        void onAdEvent(String message);
        void onStatusChanged(String status);
        void onInterstitialReady(boolean ready);
    }

    public InMobiAdManager(Context context, AdEventListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void initialize() {
        notifyStatus("Initializing InMobi SDK...");
        notifyEvent("Initializing InMobi SDK with Account ID: " + ACCOUNT_ID);
        JSONObject consentObject = new JSONObject();
        try {
            consentObject.put("gdpr_consent_available", true);
            consentObject.put("gdpr", "0");
        } catch (JSONException e) {
            Log.e(TAG, "Error creating consent JSON", e);
        }
        InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);
        InMobiSdk.init(context, ACCOUNT_ID, consentObject, new SdkInitializationListener() {
            @Override
            public void onInitializationComplete(Error error) {
                if (error == null) {
                    notifyEvent("InMobi SDK INITIALIZED successfully");
                    notifyStatus("InMobi SDK Initialized");
                } else {
                    notifyEvent("InMobi SDK Init FAILED: " + error.getMessage());
                    notifyStatus("Init Failed: " + error.getMessage());
                }
            }
        });
    }

    public void loadBanner(Activity activity, ViewGroup container) {
        notifyEvent("Loading InMobi Banner Ad...");
        if (inMobiBanner != null) {
            container.removeAllViews();
        }
        inMobiBanner = new InMobiBanner(activity, BANNER_PLACEMENT_ID);
        inMobiBanner.setAnimationType(InMobiBanner.AnimationType.ROTATE_HORIZONTAL_AXIS);
        inMobiBanner.setEnableAutoRefresh(false);
        inMobiBanner.setListener(new BannerAdEventListener() {
            @Override
            public void onAdLoadSucceeded(InMobiBanner ad, AdMetaInfo info) {
                notifyEvent("InMobi Banner LOADED successfully");
                notifyStatus("Banner Loaded");
            }

            @Override
            public void onAdLoadFailed(InMobiBanner ad, InMobiAdRequestStatus status) {
                notifyEvent("InMobi Banner FAILED: " + status.getMessage()
                        + " (Code: " + status.getStatusCode() + ")");
                notifyStatus("Banner Failed: " + status.getMessage());
            }

            @Override
            public void onAdDisplayed(InMobiBanner ad) {
                notifyEvent("InMobi Banner Displayed");
            }

            @Override
            public void onAdDismissed(InMobiBanner ad) {
                notifyEvent("InMobi Banner Dismissed");
            }

            @Override
            public void onAdClicked(InMobiBanner ad, Map<Object, Object> params) {
                notifyEvent("InMobi Banner Clicked");
            }

            @Override
            public void onUserLeftApplication(InMobiBanner ad) {
                notifyEvent("InMobi Banner - User Left Application");
            }

            @Override
            public void onAdImpression(InMobiBanner ad) {
                notifyEvent("InMobi Banner Impression recorded");
            }

            @Override
            public void onRewardsUnlocked(InMobiBanner ad, Map<Object, Object> rewards) {
                notifyEvent("InMobi Banner Rewards Unlocked");
            }
        });
        int heightPx = (int) (50 * activity.getResources().getDisplayMetrics().density);
        inMobiBanner.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, heightPx));
        container.removeAllViews();
        container.addView(inMobiBanner);
        inMobiBanner.load();
    }

    public void loadInterstitial(Activity activity) {
        notifyEvent("Loading InMobi Interstitial Ad...");
        inMobiInterstitial = new InMobiInterstitial(activity, INTERSTITIAL_PLACEMENT_ID,
                new InterstitialAdEventListener() {
                    @Override
                    public void onAdLoadSucceeded(InMobiInterstitial ad, AdMetaInfo info) {
                        notifyEvent("InMobi Interstitial LOADED successfully");
                        notifyStatus("Interstitial Loaded - Ready to Show");
                        listener.onInterstitialReady(true);
                    }

                    @Override
                    public void onAdLoadFailed(InMobiInterstitial ad, InMobiAdRequestStatus status) {
                        notifyEvent("InMobi Interstitial FAILED: " + status.getMessage()
                                + " (Code: " + status.getStatusCode() + ")");
                        notifyStatus("Interstitial Failed: " + status.getMessage());
                    }

                    @Override
                    public void onAdWillDisplay(InMobiInterstitial ad) {
                        notifyEvent("InMobi Interstitial Will Display");
                    }

                    @Override
                    public void onAdDisplayed(InMobiInterstitial ad, AdMetaInfo info) {
                        notifyEvent("InMobi Interstitial DISPLAYED");
                    }

                    @Override
                    public void onAdDisplayFailed(InMobiInterstitial ad) {
                        notifyEvent("InMobi Interstitial Display Failed");
                    }

                    @Override
                    public void onAdDismissed(InMobiInterstitial ad) {
                        notifyEvent("InMobi Interstitial Dismissed");
                        listener.onInterstitialReady(false);
                    }

                    @Override
                    public void onAdClicked(InMobiInterstitial ad, Map<Object, Object> params) {
                        notifyEvent("InMobi Interstitial Clicked");
                    }

                    @Override
                    public void onUserLeftApplication(InMobiInterstitial ad) {
                        notifyEvent("InMobi Interstitial - User Left Application");
                    }

                    @Override
                    public void onAdImpression(InMobiInterstitial ad) {
                        notifyEvent("InMobi Interstitial Impression recorded");
                    }

                    @Override
                    public void onRewardsUnlocked(InMobiInterstitial ad, Map<Object, Object> rewards) {
                        notifyEvent("InMobi Interstitial Rewards Unlocked");
                    }
                });
        inMobiInterstitial.load();
    }

    public void showInterstitial() {
        if (inMobiInterstitial != null && inMobiInterstitial.isReady()) {
            inMobiInterstitial.show();
        } else {
            notifyEvent("InMobi Interstitial not ready");
            notifyStatus("Interstitial not loaded yet");
        }
    }

    public void destroy() {
        if (inMobiBanner != null) {
            inMobiBanner.destroy();
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
