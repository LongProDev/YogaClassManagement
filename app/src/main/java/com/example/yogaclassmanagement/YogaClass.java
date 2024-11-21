package com.example.yogaclassmanagement;

import java.io.Serializable;

public class YogaClass {
    private String id;
    private String dayOfWeek;
    private String time;
    private int capacity;
    private int duration;
    private double price;
    private String type;
    private String description;

    // Constructor
    public YogaClass(String dayOfWeek, String time, int capacity,
                     int duration, double price, String type, String description) {
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.type = type;
        this.description = description;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getTime() { return time; }
    public int getCapacity() { return capacity; }
    public int getDuration() { return duration; }
    public double getPrice() { return price; }
    public String getType() { return type; }
    public String getDescription() { return description; }
}
