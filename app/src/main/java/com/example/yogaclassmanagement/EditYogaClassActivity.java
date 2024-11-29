package com.example.yogaclassmanagement;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditYogaClassActivity extends AppCompatActivity {
    private EditText etDay, etTime, etCapacity, etDuration, etPrice, etDescription;
    private Spinner spinnerType;
    private Button btnUpdate;
    private DatabaseHelper dbHelper;
    private String classId;
    private YogaClass yogaClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class); //reuse the add class layout

        classId = getIntent().getStringExtra("class_id");
        if (classId == null) {
            Toast.makeText(this, "Error: Class ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        yogaClass = dbHelper.getYogaClassById(classId);
        
        if (yogaClass == null) {
            Toast.makeText(this, "Error: Class not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupTypeSpinner();
        populateFields();
        setupUpdateButton();
    }

    private void initializeViews() {
        etDay = findViewById(R.id.etDay);
        etTime = findViewById(R.id.etTime);
        etCapacity = findViewById(R.id.etCapacity);
        etDuration = findViewById(R.id.etDuration);
        etPrice = findViewById(R.id.etPrice);
        spinnerType = findViewById(R.id.spinnerType);
        etDescription = findViewById(R.id.etDescription);
        btnUpdate = findViewById(R.id.btnSave);
        btnUpdate.setText("Update Class");
    }

    private void setupTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.yoga_class_types,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
    }

    private void populateFields() {
        etDay.setText(yogaClass.getDayOfWeek());
        etTime.setText(yogaClass.getTime());
        etCapacity.setText(String.valueOf(yogaClass.getCapacity()));
        etDuration.setText(String.valueOf(yogaClass.getDuration()));
        etPrice.setText(String.valueOf(yogaClass.getPrice()));
        etDescription.setText(yogaClass.getDescription());
        
        // Set spinner selection
        ArrayAdapter adapter = (ArrayAdapter) spinnerType.getAdapter();
        int position = adapter.getPosition(yogaClass.getType());
        spinnerType.setSelection(position);
    }

    private void setupUpdateButton() {
        btnUpdate.setOnClickListener(v -> {
            if (validateInputs()) {
                updateYogaClass();
            }
        });
    }

    private boolean validateInputs() {
        if (etDay.getText().toString().trim().isEmpty() ||
                etTime.getText().toString().trim().isEmpty() ||
                etCapacity.getText().toString().trim().isEmpty() ||
                etDuration.getText().toString().trim().isEmpty() ||
                etPrice.getText().toString().trim().isEmpty() ||
                spinnerType.getSelectedItem() == null) {

            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateYogaClass() {
        YogaClass updatedClass = new YogaClass(
                etDay.getText().toString().trim(),
                etTime.getText().toString().trim(),
                Integer.parseInt(etCapacity.getText().toString().trim()),
                Integer.parseInt(etDuration.getText().toString().trim()),
                Double.parseDouble(etPrice.getText().toString().trim()),
                spinnerType.getSelectedItem().toString(),
                etDescription.getText().toString().trim()
        );
        updatedClass.setId(classId);

        boolean success = dbHelper.updateYogaClass(updatedClass);
        if (success) {
            Toast.makeText(this, "Yoga class updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error updating yoga class", Toast.LENGTH_SHORT).show();
        }
    }
}
