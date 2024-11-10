package com.example.yogaclassmanagement;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;

public class AddClassActivity extends AppCompatActivity {
    private EditText etDay, etTime, etCapacity, etDuration, etPrice, etType, etDescription;
    private Button btnSave;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        dbHelper = new DatabaseHelper(this);

        initializeViews();
        setupSaveButton();
    }

    private void initializeViews() {
        etDay = findViewById(R.id.etDay);
        etTime = findViewById(R.id.etTime);
        etCapacity = findViewById(R.id.etCapacity);
        etDuration = findViewById(R.id.etDuration);
        etPrice = findViewById(R.id.etPrice);
        etType = findViewById(R.id.etType);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            if (validateInputs()) {
                saveYogaClass();
            }
        });
    }

    private boolean validateInputs() {
        // Validate required fields
        if (etDay.getText().toString().trim().isEmpty() ||
                etTime.getText().toString().trim().isEmpty() ||
                etCapacity.getText().toString().trim().isEmpty() ||
                etDuration.getText().toString().trim().isEmpty() ||
                etPrice.getText().toString().trim().isEmpty() ||
                etType.getText().toString().trim().isEmpty()) {

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
                etType.getText().toString().trim(),
                etDescription.getText().toString().trim()
        );

        long id = dbHelper.addYogaClass(yogaClass);
        if (id != -1) {
            Toast.makeText(this, "Yoga class saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving yoga class", Toast.LENGTH_SHORT).show();
        }
    }
}
