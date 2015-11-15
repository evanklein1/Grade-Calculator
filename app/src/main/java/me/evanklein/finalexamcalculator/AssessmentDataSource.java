package me.evanklein.finalexamcalculator;

/**
 * Created by Evan on 15-Nov-2015.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class AssessmentDataSource extends DataSource<Assessment> {
    public static final String TABLE_NAME = "assessment";
    public static final String COLUMN_COURSE = "course";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_MARK = "mark";
    public static final String COLUMN_MARKED = "marked";
    public static final String COLUMN_WORTH = "worth";
    // Database creation sql statement
    public static final String CREATE_COMMAND = "create table " + TABLE_NAME
            + "("
            + COLUMN_COURSE + " text not null, "
            + COLUMN_ID + " integer not null, "
            + COLUMN_TYPE + " text, "
            + COLUMN_MARK + " float, "
            + COLUMN_MARKED + " boolean, "
            + COLUMN_WORTH + " float, "
            + "PRIMARY KEY (" + COLUMN_COURSE + "," + COLUMN_ID + "));";
    public AssessmentDataSource(SQLiteDatabase database) {
        super(database);
    }
    @Override
    public boolean insert(Assessment entity) {
        if (entity == null) {
            return false;
        }
        long result = mDatabase.insert(TABLE_NAME, null,
                generateContentValuesFromObject(entity));
        return result != -1;
    }
    @Override
    public boolean delete(Assessment entity) {
        if (entity == null) {
            return false;
        }
        int result = mDatabase.delete(TABLE_NAME,
                COLUMN_ID + " = " + entity.getId(), null);
        return result != 0;
    }
    @Override
    public boolean update(Assessment entity) {
        if (entity == null) {
            return false;
        }
        int result = mDatabase.update(TABLE_NAME,
                generateContentValuesFromObject(entity), COLUMN_ID + " = "
                        + entity.getId(), null);
        return result != 0;
    }
    @Override
    public List read() {
        Cursor cursor = mDatabase.query(TABLE_NAME, getAllColumns(), null,
                null, null, null, null);
        List assessments = new ArrayList();
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                assessments.add(generateObjectFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return assessments;
    }
    @Override
    public List read(String selection, String[] selectionArgs,
                     String groupBy, String having, String orderBy) {
        Cursor cursor = mDatabase.query(TABLE_NAME, getAllColumns(), selection, selectionArgs, groupBy, having, orderBy);
        List assessments = new ArrayList();
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                assessments.add(generateObjectFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return assessments;
    }
    public String[] getAllColumns() {
        return new String[] { COLUMN_COURSE, COLUMN_ID, COLUMN_TYPE, COLUMN_MARK, COLUMN_MARKED, COLUMN_WORTH };
    }
    public Assessment generateObjectFromCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        Assessment assessment = new Assessment("", 0.0, false, 0.0);
        assessment.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        assessment.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
        assessment.setMark(cursor.getDouble(cursor.getColumnIndex(COLUMN_MARK)));
        Boolean marked = (cursor.getInt(cursor.getColumnIndex(COLUMN_MARKED)) > 0);
        assessment.setMarked(marked);
        assessment.setWorth(cursor.getDouble(cursor.getColumnIndex(COLUMN_WORTH)));

        return assessment;
    }
    public ContentValues generateContentValuesFromObject(Assessment entity) {
        if (entity == null) {
            return null;
        }
        ContentValues assessmentValues = new ContentValues();
        //add the assessment "COURSE NAME" and "DESIRED GRADE" to the course table
        assessmentValues.put(COLUMN_ID, entity.getId());
        assessmentValues.put(COLUMN_TYPE, entity.getType());
        assessmentValues.put(COLUMN_MARK, entity.getMark());
        assessmentValues.put(COLUMN_MARKED, entity.isMarked());
        assessmentValues.put(COLUMN_WORTH, entity.getWorth());
        return assessmentValues;
    }
}

