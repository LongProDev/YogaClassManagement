package com.example.yogaclassmanagement;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
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

public class YogaClassInstance {
    private String id;
    private String yogaClassId;
    private String date;
    private String teacher;
    private String additionalComments;

    public YogaClassInstance(String yogaClassId, String date, String teacher, String additionalComments) {
        this.yogaClassId = yogaClassId;
        this.date = date;
        this.teacher = teacher;
        this.additionalComments = additionalComments;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getYogaClassId() { return yogaClassId; }
    public String getDate() { return date; }
    public String getTeacher() { return teacher; }
    public String getAdditionalComments() { return additionalComments; }
}
