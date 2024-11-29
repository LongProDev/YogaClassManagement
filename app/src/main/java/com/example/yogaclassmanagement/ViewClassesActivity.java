package com.example.yogaclassmanagement;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ViewClassesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private YogaClassAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_classes);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadYogaClasses();
    }

    private void loadYogaClasses() {
        List<YogaClass> classes = dbHelper.getAllYogaClasses();
        adapter = new YogaClassAdapter(classes, this::onClassClick, this::onDeleteClick, this::onEditClick);
        recyclerView.setAdapter(adapter);
    }

    private void onClassClick(YogaClass yogaClass) {
        Intent intent = new Intent(this, ClassInstancesActivity.class);
        intent.putExtra("class_id", yogaClass.getId());
        startActivity(intent);
    }

    private void onDeleteClick(YogaClass yogaClass) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteYogaClass(yogaClass.getId());
                    loadYogaClasses();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void onEditClick(YogaClass yogaClass) {
        Intent intent = new Intent(this, EditYogaClassActivity.class);
        intent.putExtra("class_id", yogaClass.getId());
        startActivity(intent);
    }
}
