package com.example.yogaclassmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnAddClass;
    private Button btnViewClasses;
    private Button btnSearchClasses;
    private Button btnSync;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        btnAddClass = findViewById(R.id.btnAddClass);
        btnViewClasses = findViewById(R.id.btnViewClasses);
        btnSearchClasses = findViewById(R.id.btnSearchClasses);
        btnSync = findViewById(R.id.btnSync);

        btnAddClass.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddClassActivity.class);
            startActivity(intent);
        });

        btnViewClasses.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewClassesActivity.class);
            startActivity(intent);
        });

        btnSearchClasses.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        btnSync.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SyncActivity.class);
            startActivity(intent);
        });
    }
}