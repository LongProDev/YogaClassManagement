package com.example.yogaclassmanagement.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SearchUtils {
    private static final List<String> VALID_DAYS = Arrays.asList(
            "Monday", "Tuesday", "Wednesday", "Thursday", 
            "Friday", "Saturday", "Sunday");

    public static boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isValidDayOfWeek(String day) {
        String standardizedDay = day.substring(0, 1).toUpperCase() + 
                               day.substring(1).toLowerCase();
        return VALID_DAYS.contains(standardizedDay);
    }

    public static String standardizeDayOfWeek(String day) {
        return day.substring(0, 1).toUpperCase() + 
               day.substring(1).toLowerCase();
    }
}
