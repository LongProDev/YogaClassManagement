package com.example.yogaclassmanagement;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;

public class SearchActivity extends AppCompatActivity implements SearchResultsAdapter.OnItemClickListener {
    private DatabaseHelper dbHelper;
    private TextInputEditText searchInput;
    private Spinner searchTypeSpinner;
    private RecyclerView resultsRecyclerView;
    private SearchResultsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dbHelper = new DatabaseHelper(this);

        // Initialize views
        searchInput = findViewById(R.id.searchInput);
        searchTypeSpinner = findViewById(R.id.searchTypeSpinner);
        resultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);

        // Setup spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.search_types, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchTypeSpinner.setAdapter(spinnerAdapter);

        // Setup RecyclerView
        adapter = new SearchResultsAdapter(this);
        resultsRecyclerView.setAdapter(adapter);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add text change listener for search
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                performSearch(s.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void performSearch(String query) {
        String searchType = searchTypeSpinner.getSelectedItem().toString();
        Cursor cursor = null;

        try {
            switch (searchType) {
                case "Teacher":
                    cursor = dbHelper.searchByTeacher(query);
                    break;
                case "Date":
                    cursor = dbHelper.searchByDate(query);
                    break;
                case "Day of Week":
                    cursor = dbHelper.searchByDayOfWeek(query);
                    break;
            }
            adapter.updateResults(cursor);
        } catch (Exception e) {
            Toast.makeText(this, "Error performing search", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onItemClick(long classId) {
        // Show class details
        Cursor classDetails = dbHelper.getClassWithInstances(classId);
        if (classDetails != null && classDetails.moveToFirst()) {
            // You can create a dialog or start a new activity to show details
            Toast.makeText(this, "Class selected: " + classId, Toast.LENGTH_SHORT).show();
            classDetails.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
