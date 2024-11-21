package com.example.yogaclassmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
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

        try {
            syncManager = new CloudSyncManager(this);
            initializeViews();
            setupSyncButton();
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing sync: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        btnSync = findViewById(R.id.btnSync);
        progressBar = findViewById(R.id.progressBar);
        tvStatus = findViewById(R.id.tvStatus);
        
        if (btnSync == null || progressBar == null || tvStatus == null) {
            throw new IllegalStateException("Failed to initialize views");
        }
    }

    private void setupSyncButton() {
        btnSync.setOnClickListener(v -> startSync());
    }

    private void startSync() {
        try {
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
                        try {
                            progressBar.setVisibility(View.GONE);
                            tvStatus.setText("Sync completed successfully");
                            btnSync.setEnabled(true);
                            Toast.makeText(SyncActivity.this, 
                                "Sync completed successfully", 
                                Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            handleError("Error updating UI: " + e.getMessage());
                        }
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        handleError(message);
                    });
                }
            });
        } catch (Exception e) {
            handleError("Error starting sync: " + e.getMessage());
        }
    }

    private void handleError(String message) {
        try {
            progressBar.setVisibility(View.GONE);
            tvStatus.setText("Sync failed: " + message);
            btnSync.setEnabled(true);
            Toast.makeText(this, "Sync failed: " + message, 
                Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // Last resort error handling
            Toast.makeText(this, "Critical error during sync", 
                Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (syncManager != null) {
            // Clean up any resources if needed
        }
    }
}
