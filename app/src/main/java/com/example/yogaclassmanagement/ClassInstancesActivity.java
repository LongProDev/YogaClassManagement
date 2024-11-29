package com.example.yogaclassmanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ClassInstancesActivity extends AppCompatActivity {
    private String classId;
    private String dayOfWeek;
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private Button btnAddInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_instances);

        classId = getIntent().getStringExtra("class_id");
        if (classId == null) {
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        
        YogaClass yogaClass = dbHelper.getYogaClassById(classId);
        if (yogaClass != null) {
            dayOfWeek = yogaClass.getDayOfWeek().toUpperCase();
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnAddInstance = findViewById(R.id.btnAddInstance);
        btnAddInstance.setOnClickListener(v -> showAddInstanceDialog());

        loadInstances();
    }

    private void loadInstances() {
        List<YogaClassInstance> instances = dbHelper.getClassInstancesForClass(classId);
        ClassInstanceAdapter adapter = new ClassInstanceAdapter(
            instances,
            this::showDeleteInstanceDialog,
            this::showEditInstance
        );
        recyclerView.setAdapter(adapter);
    }

    private void showAddInstanceDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_instance, null);
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etTeacher = dialogView.findViewById(R.id.etTeacher);
        EditText etComments = dialogView.findViewById(R.id.etComments);

        etDate.setOnClickListener(v -> showDatePicker(etDate));

        new AlertDialog.Builder(this)
                .setTitle("Add Class Instance")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    if (validateInstanceInput(etDate, etTeacher)) {
                        YogaClassInstance instance = new YogaClassInstance(
                                classId,
                                etDate.getText().toString(),
                                etTeacher.getText().toString(),
                                etComments.getText().toString()
                        );
                        dbHelper.addClassInstance(instance);
                        loadInstances();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDatePicker(EditText etDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    if (isDayMatch(calendar)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
                        etDate.setText(sdf.format(calendar.getTime()));
                    } else {
                        Toast.makeText(this, 
                            "Please select a " + dayOfWeek + " date", 
                            Toast.LENGTH_SHORT).show();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private boolean isDayMatch(Calendar selectedDate) {
        String selectedDayOfWeek = selectedDate.getDisplayName(
                Calendar.DAY_OF_WEEK, 
                Calendar.LONG, 
                Locale.UK).toUpperCase();
        return selectedDayOfWeek.equals(dayOfWeek.toUpperCase());
    }

    private boolean validateInstanceInput(EditText etDate, EditText etTeacher) {
        if (etDate.getText().toString().trim().isEmpty() ||
                etTeacher.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showDeleteInstanceDialog(YogaClassInstance instance) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Instance")
                .setMessage("Are you sure you want to delete this class instance?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteClassInstance(instance.getId());
                    loadInstances();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showEditInstance(YogaClassInstance instance) {
        Intent intent = new Intent(this, EditClassInstanceActivity.class);
        intent.putExtra("instance_id", instance.getId());
        startActivity(intent);
    }
}
