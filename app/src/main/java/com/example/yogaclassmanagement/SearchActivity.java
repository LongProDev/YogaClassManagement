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
import com.example.yogaclassmanagement.SearchResultsAdapter;
import com.example.yogaclassmanagement.SearchResult;
import com.example.yogaclassmanagement.utils.SearchUtils;

import java.util.ArrayList;
import java.util.List;

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
                    if (!SearchUtils.isValidDate(query)) {
                        Toast.makeText(this, "Please enter a valid date (dd/MM/yyyy)", 
                            Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cursor = dbHelper.searchByDate(query);
                    break;
                case "Day of Week":
                    if (!SearchUtils.isValidDayOfWeek(query)) {
                        Toast.makeText(this, "Please enter a valid day of the week", 
                            Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String standardizedDay = SearchUtils.standardizeDayOfWeek(query);
                    cursor = dbHelper.searchByDayOfWeek(standardizedDay);
                    break;
            }

            if (cursor != null && cursor.getCount() > 0) {
                updateSearchResults(cursor);
            } else {
                Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show();
                adapter.updateResults(new ArrayList<>()); // Clear results
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error performing search", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void performDateSearch(String searchDate) {
        if (!SearchUtils.isValidDate(searchDate)) {
            Toast.makeText(this, "Please enter a valid date (dd/MM/yyyy)", 
                Toast.LENGTH_SHORT).show();
            return;
        }
        
        Cursor cursor = dbHelper.searchByDate(searchDate);
        if (cursor != null && cursor.getCount() > 0) {
            // Process results
            updateSearchResults(cursor);
        } else {
            Toast.makeText(this, "No classes found for this date", 
                Toast.LENGTH_SHORT).show();
        }
    }

    private void performDaySearch(String searchDay) {
        if (!SearchUtils.isValidDayOfWeek(searchDay)) {
            Toast.makeText(this, "Please enter a valid day of the week", 
                Toast.LENGTH_SHORT).show();
            return;
        }
        
        String standardizedDay = SearchUtils.standardizeDayOfWeek(searchDay);
        Cursor cursor = dbHelper.searchByDayOfWeek(standardizedDay);
        if (cursor != null && cursor.getCount() > 0) {
            // Process results
            updateSearchResults(cursor);
        } else {
            Toast.makeText(this, "No classes found for this day", 
                Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(String classId) {
        // Show class details
        Cursor classDetails = dbHelper.getClassWithInstances(classId);
        if (classDetails != null && classDetails.moveToFirst()) {
            Toast.makeText(this, "Class selected: " + classId, Toast.LENGTH_SHORT).show();
            classDetails.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    private void updateSearchResults(Cursor cursor) {
        List<SearchResult> searchResults = new ArrayList<>();
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                SearchResult result = new SearchResult(
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_DAY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_TYPE))
                );
                
                // Add instance details if available
                if (cursor.getColumnIndex(DatabaseHelper.KEY_DATE) != -1) {
                    result.setInstanceDate(cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_DATE)));
                    result.setTeacher(cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_TEACHER)));
                }
                
                searchResults.add(result);
            } while (cursor.moveToNext());
            
            cursor.close();
        }
        
        adapter.updateResults(searchResults);
    }
}
