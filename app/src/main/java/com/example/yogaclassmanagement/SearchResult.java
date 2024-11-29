package com.example.yogaclassmanagement;

public class SearchResult {
    private String classId;
    private String dayOfWeek;
    private String time;
    private String type;
    private String instanceDate;
    private String teacher;

    public SearchResult(String classId, String dayOfWeek, String time, String type) {
        this.classId = classId;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.type = type;
    }

    // Getters
    public String getClassId() { return classId; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getTime() { return time; }
    public String getType() { return type; }
    public String getInstanceDate() { return instanceDate; }
    public String getTeacher() { return teacher; }

    // Setters
    public void setInstanceDate(String instanceDate) { this.instanceDate = instanceDate; }
    public void setTeacher(String teacher) { this.teacher = teacher; }

    public String getDisplayText() {
        if (instanceDate != null && teacher != null) {
            return String.format("%s - %s\n%s with %s", type, dayOfWeek, instanceDate, teacher);
        } else {
            return String.format("%s - %s at %s", type, dayOfWeek, time);
        }
    }
}
