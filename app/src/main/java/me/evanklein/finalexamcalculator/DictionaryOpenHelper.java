package me.evanklein.finalexamcalculator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Evan on 02/11/2015.
 */
public class DictionaryOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "grade_calculator";

//    private static final String DICTIONARY_TABLE_CREATE =
//            "CREATE TABLE " + DICTIONARY_TABLE_NAME + " (" +
//                    KEY_WORD + " TEXT, " +
//                    KEY_DEFINITION + " TEXT);";

    public DictionaryOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String courses_table = "CREATE TABLE course (Name varchar(255), desiredGrade float, currentGrade float);";
        String assessment_table =
                "CREATE TABLE assessment (Type varchar(255), mark float, marked boolean, worth float)";
        db.execSQL(courses_table);
        db.execSQL(assessment_table);
    }
    public void onUpgrade(SQLiteDatabase db, int i, int j) {

    }
}
