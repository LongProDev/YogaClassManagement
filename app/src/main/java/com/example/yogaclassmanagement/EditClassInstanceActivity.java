package com.example.yogaclassmanagement;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditClassInstanceActivity extends AppCompatActivity {
    private EditText etDate, etTeacher, etComments;
    private Button btnUpdate;
    private DatabaseHelper dbHelper;
    private String instanceId;
    private YogaClassInstance instance;
    private String dayOfWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_instance);

        instanceId = getIntent().getStringExtra("instance_id");
        if (instanceId == null) {
            Toast.makeText(this, "Error: Instance ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        instance = dbHelper.getClassInstanceById(instanceId);
        
        if (instance == null) {
            Toast.makeText(this, "Error: Instance not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        YogaClass yogaClass = dbHelper.getYogaClassById(instance.getYogaClassId());
        if (yogaClass != null) {
            dayOfWeek = yogaClass.getDayOfWeek().toUpperCase();
        } else {
            Toast.makeText(this, "Error: Associated yoga class not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        populateFields();
        setupDatePicker();
        setupUpdateButton();
    }

    private void initializeViews() {
        etDate = findViewById(R.id.etDate);
        etTeacher = findViewById(R.id.etTeacher);
        etComments = findViewById(R.id.etComments);
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    private void populateFields() {
        etDate.setText(instance.getDate());
        etTeacher.setText(instance.getTeacher());
        etComments.setText(instance.getAdditionalComments());
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
                calendar.setTime(sdf.parse(instance.getDate()));
            } catch (Exception e) {
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    
                    if (isDayMatch(selectedDate)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
                        etDate.setText(sdf.format(selectedDate.getTime()));
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
        });
    }

    private boolean isDayMatch(Calendar selectedDate) {
        String selectedDayOfWeek = selectedDate.getDisplayName(
                Calendar.DAY_OF_WEEK, 
                Calendar.LONG, 
                Locale.UK).toUpperCase();
        return selectedDayOfWeek.equals(dayOfWeek);
    }

    private void setupUpdateButton() {
        btnUpdate.setOnClickListener(v -> {
            if (validateInputs()) {
                updateClassInstance();
            }
        });
    }

    private boolean validateInputs() {
        if (etDate.getText().toString().trim().isEmpty() ||
                etTeacher.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", 
                Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateClassInstance() {
        YogaClassInstance updatedInstance = new YogaClassInstance(
                instance.getYogaClassId(),
                etDate.getText().toString().trim(),
                etTeacher.getText().toString().trim(),
                etComments.getText().toString().trim()
        );
        updatedInstance.setId(instanceId);

        boolean success = dbHelper.updateClassInstance(updatedInstance);
        if (success) {
            Toast.makeText(this, "Class instance updated successfully", 
                Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error updating class instance", 
                Toast.LENGTH_SHORT).show();
        }
    }
}
