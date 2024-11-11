package com.example.yogaclassmanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "yoga_class_management.db";
    public static final String TABLE_YOGA_CLASSES = "yoga_classes";
    public static final String TABLE_CLASS_INSTANCES = "class_instances";
    public static final String KEY_ID = "id";
    public static final String KEY_DAY = "day_of_week";
    public static final String KEY_TIME = "time";
    public static final String KEY_CAPACITY = "capacity";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_PRICE = "price";
    public static final String KEY_TYPE = "type";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_YOGA_CLASS_ID = "yoga_class_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_TEACHER = "teacher";
    public static final String KEY_COMMENTS = "comments";

    // Create table statements
    private static final String CREATE_YOGA_CLASSES_TABLE = "CREATE TABLE " + TABLE_YOGA_CLASSES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_DAY + " TEXT NOT NULL,"
            + KEY_TIME + " TEXT NOT NULL,"
            + KEY_CAPACITY + " INTEGER NOT NULL,"
            + KEY_DURATION + " INTEGER NOT NULL,"
            + KEY_PRICE + " REAL NOT NULL,"
            + KEY_TYPE + " TEXT NOT NULL,"
            + KEY_DESCRIPTION + " TEXT"
            + ")";

    private static final String CREATE_CLASS_INSTANCES_TABLE = "CREATE TABLE " + TABLE_CLASS_INSTANCES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_YOGA_CLASS_ID + " INTEGER NOT NULL,"
            + KEY_DATE + " TEXT NOT NULL,"
            + KEY_TEACHER + " TEXT NOT NULL,"
            + KEY_COMMENTS + " TEXT,"
            + "FOREIGN KEY(" + KEY_YOGA_CLASS_ID + ") REFERENCES " + TABLE_YOGA_CLASSES + "(" + KEY_ID + ")"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_YOGA_CLASSES_TABLE);
        db.execSQL(CREATE_CLASS_INSTANCES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_INSTANCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_YOGA_CLASSES);
        onCreate(db);
    }

    // Add a new yoga class
    public long addYogaClass(YogaClass yogaClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DAY, yogaClass.getDayOfWeek());
        values.put(KEY_TIME, yogaClass.getTime());
        values.put(KEY_CAPACITY, yogaClass.getCapacity());
        values.put(KEY_DURATION, yogaClass.getDuration());
        values.put(KEY_PRICE, yogaClass.getPrice());
        values.put(KEY_TYPE, yogaClass.getType());
        values.put(KEY_DESCRIPTION, yogaClass.getDescription());

        long id = db.insert(TABLE_YOGA_CLASSES, null, values);
        db.close();
        return id;
    }

    // Add a new class instance
    public long addClassInstance(YogaClassInstance instance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_YOGA_CLASS_ID, instance.getYogaClassId());
        values.put(KEY_DATE, instance.getDate());
        values.put(KEY_TEACHER, instance.getTeacher());
        values.put(KEY_COMMENTS, instance.getAdditionalComments());

        long id = db.insert(TABLE_CLASS_INSTANCES, null, values);
        db.close();
        return id;
    }

    // Get all yoga classes
    public List<YogaClass> getAllYogaClasses() {
        List<YogaClass> classes = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_YOGA_CLASSES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                YogaClass yogaClass = new YogaClass(
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DAY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CAPACITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DURATION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION))
                );
                yogaClass.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)));
                classes.add(yogaClass);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return classes;
    }

    // Get class instances for a specific yoga class
    public List<YogaClassInstance> getClassInstancesForClass(long yogaClassId) {
        List<YogaClassInstance> instances = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CLASS_INSTANCES +
                " WHERE " + KEY_YOGA_CLASS_ID + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(yogaClassId)});

        if (cursor.moveToFirst()) {
            do {
                YogaClassInstance instance = new YogaClassInstance(
                        cursor.getLong(cursor.getColumnIndexOrThrow(KEY_YOGA_CLASS_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TEACHER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_COMMENTS))
                );
                instance.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)));
                instances.add(instance);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return instances;
    }

    // Delete a yoga class
    public void deleteYogaClass(long classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS_INSTANCES, KEY_YOGA_CLASS_ID + " = ?",
                new String[]{String.valueOf(classId)});
        db.delete(TABLE_YOGA_CLASSES, KEY_ID + " = ?",
                new String[]{String.valueOf(classId)});
        db.close();
    }

    // Delete a class instance
    public void deleteClassInstance(long instanceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS_INSTANCES, KEY_ID + " = ?",
                new String[]{String.valueOf(instanceId)});
        db.close();
    }

    public Cursor searchByTeacher(String teacherName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT yc.*, ci.* FROM " + TABLE_YOGA_CLASSES + " yc " +
                "INNER JOIN " + TABLE_CLASS_INSTANCES + " ci ON yc." + KEY_ID + " = ci." + KEY_YOGA_CLASS_ID +
                " WHERE ci." + KEY_TEACHER + " LIKE ?";
        return db.rawQuery(query, new String[]{"%" + teacherName + "%"});
    }

    public Cursor searchByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT yc.*, ci.* FROM " + TABLE_YOGA_CLASSES + " yc " +
                "INNER JOIN " + TABLE_CLASS_INSTANCES + " ci ON yc." + KEY_ID + " = ci." + KEY_YOGA_CLASS_ID +
                " WHERE ci." + KEY_DATE + " LIKE ?";
        return db.rawQuery(query, new String[]{"%" + date + "%"});
    }

    public Cursor searchByDayOfWeek(String day) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_YOGA_CLASSES +
                " WHERE " + KEY_DAY + " LIKE ?";
        return db.rawQuery(query, new String[]{"%" + day + "%"});
    }

    // Method to get full class details including instances
    public Cursor getClassWithInstances(long classId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT yc.*, ci.* FROM " + TABLE_YOGA_CLASSES + " yc " +
                "LEFT JOIN " + TABLE_CLASS_INSTANCES + " ci ON yc." + KEY_ID + " = ci." + KEY_YOGA_CLASS_ID +
                " WHERE yc." + KEY_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(classId)});
    }
}
