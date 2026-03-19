package com.example.adsdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Main activity displaying a list of Ad SDK integrations to demo.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ad SDK Integration Demo");
        }

        setupCardClickListeners();
    }

    private void setupCardClickListeners() {
        findViewById(R.id.card_admob).setOnClickListener(v ->
                startActivity(new Intent(this, AdMobActivity.class)));

        findViewById(R.id.card_admanager).setOnClickListener(v ->
                startActivity(new Intent(this, AdManagerActivity.class)));

        findViewById(R.id.card_meta).setOnClickListener(v ->
                startActivity(new Intent(this, MetaAudienceActivity.class)));

        findViewById(R.id.card_unity).setOnClickListener(v ->
                startActivity(new Intent(this, UnityAdsActivity.class)));

        findViewById(R.id.card_applovin).setOnClickListener(v ->
                startActivity(new Intent(this, AppLovinActivity.class)));

        findViewById(R.id.card_ironsource).setOnClickListener(v ->
                startActivity(new Intent(this, IronSourceActivity.class)));

        findViewById(R.id.card_inmobi).setOnClickListener(v ->
                startActivity(new Intent(this, InMobiActivity.class)));

        findViewById(R.id.card_tapjoy).setOnClickListener(v ->
                startActivity(new Intent(this, TapjoyActivity.class)));

        findViewById(R.id.card_chartboost).setOnClickListener(v ->
                startActivity(new Intent(this, ChartboostActivity.class)));
    }
}
