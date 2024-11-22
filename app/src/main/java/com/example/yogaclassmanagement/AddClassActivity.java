package com.example.yogaclassmanagement;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddClassActivity extends AppCompatActivity {
    private EditText etDay, etTime, etCapacity, etDuration, etPrice, etDescription;
    private Spinner spinnerType;
    private Button btnSave;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        dbHelper = new DatabaseHelper(this);

        initializeViews();
        setupTypeSpinner();
        setupSaveButton();
    }

    private void initializeViews() {
        etDay = findViewById(R.id.etDay);
        etTime = findViewById(R.id.etTime);
        etCapacity = findViewById(R.id.etCapacity);
        etDuration = findViewById(R.id.etDuration);
        etPrice = findViewById(R.id.etPrice);
        spinnerType = findViewById(R.id.spinnerType);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.yoga_class_types,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            if (validateInputs()) {
                saveYogaClass();
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

    private void saveYogaClass() {
        YogaClass yogaClass = new YogaClass(
                etDay.getText().toString().trim(),
                etTime.getText().toString().trim(),
                Integer.parseInt(etCapacity.getText().toString().trim()),
                Integer.parseInt(etDuration.getText().toString().trim()),
                Double.parseDouble(etPrice.getText().toString().trim()),
                spinnerType.getSelectedItem().toString(),
                etDescription.getText().toString().trim()
        );

        String id = dbHelper.addYogaClass(yogaClass);
        if (id != null) {
            Toast.makeText(this, "Yoga class saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving yoga class", Toast.LENGTH_SHORT).show();
        }
    }
}
