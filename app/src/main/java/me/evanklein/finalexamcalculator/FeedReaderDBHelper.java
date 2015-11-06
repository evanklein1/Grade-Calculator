package me.evanklein.finalexamcalculator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Evan on 06/11/2015.
 */
public class FeedReaderDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "grade_calculator.db";

    public FeedReaderDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        String courses_table = "CREATE TABLE course (Name varchar(255), desiredGrade float, currentGrade float);";
        String assessment_table =
                "CREATE TABLE assessment (Type varchar(255), mark float, marked boolean, worth float);";
        db.execSQL(courses_table);
        db.execSQL(assessment_table);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        String courses_table = "DROP TABLE IF EXISTS course;";
        String assessment_table = "DROP TABLE IF EXISTS assessment;";
        db.execSQL(courses_table);
        db.execSQL(assessment_table);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
