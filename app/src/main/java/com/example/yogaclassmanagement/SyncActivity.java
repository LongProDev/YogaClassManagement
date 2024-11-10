package com.example.yogaclassmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SyncActivity extends AppCompatActivity {
    private Button btnSync;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private CloudSyncManager syncManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        syncManager = new CloudSyncManager(this);
        initializeViews();
        setupSyncButton();
    }

    private void initializeViews() {
        btnSync = findViewById(R.id.btnSync);
        progressBar = findViewById(R.id.progressBar);
        tvStatus = findViewById(R.id.tvStatus);
    }

    private void setupSyncButton() {
        btnSync.setOnClickListener(v -> startSync());
    }

    private void startSync() {
        if (!syncManager.isNetworkAvailable()) {
            tvStatus.setText("No network connection available");
            return;
        }

        btnSync.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        tvStatus.setText("Syncing data...");

        syncManager.syncAllData(new CloudSyncManager.SyncCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvStatus.setText("Sync completed successfully");
                    btnSync.setEnabled(true);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvStatus.setText("Sync failed: " + message);
                    btnSync.setEnabled(true);
                });
            }
        });
    }
}
