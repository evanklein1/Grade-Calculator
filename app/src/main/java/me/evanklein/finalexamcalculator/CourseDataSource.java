package me.evanklein.finalexamcalculator;

/**
 * Created by Evan on 14-Nov-2015.
 */
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
public class CourseDataSource extends DataSource<Course> {
    public static final String TABLE_NAME = "course";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESIRED_GRADE = "desired_grade";
    // Database creation sql statement
    public static final String CREATE_COMMAND = "create table " + TABLE_NAME
            + "(" + COLUMN_NAME + " text not null, "
            + COLUMN_NAME + " float);";
    public CourseDataSource(SQLiteDatabase database) {
        super(database);
    }
    @Override
    public boolean insert(Course entity) {
        if (entity == null) {
            return false;
        }
        long result = mDatabase.insert(TABLE_NAME, null,
                generateContentValuesFromObject(entity));
        return result != -1;
    }
    @Override
    public boolean delete(Course entity) {
        if (entity == null) {
            return false;
        }
        int result = mDatabase.delete(TABLE_NAME,
                COLUMN_NAME + " = " + entity.getName(), null);
        return result != 0;
    }
    @Override
    public boolean update(Course entity) {
        if (entity == null) {
            return false;
        }
        int result = mDatabase.update(TABLE_NAME,
                generateContentValuesFromObject(entity), COLUMN_NAME + " = "
                        + entity.getName(), null);
        return result != 0;
    }
    @Override
    public List read() {
        Cursor cursor = mDatabase.query(TABLE_NAME, getAllColumns(), null,
                null, null, null, null);
        List courses = new ArrayList();
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                courses.add(generateObjectFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return courses;
    }
    @Override
    public List read(String selection, String[] selectionArgs,
                     String groupBy, String having, String orderBy) {
        Cursor cursor = mDatabase.query(TABLE_NAME, getAllColumns(), selection, selectionArgs, groupBy, having, orderBy);
        List courses = new ArrayList();
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                courses.add(generateObjectFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return courses;
    }
    public String[] getAllColumns() {
        return new String[] { COLUMN_NAME, COLUMN_DESIRED_GRADE };
    }
    public Course generateObjectFromCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        Course course = new Course();
        course.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        course.setDesiredGrade(cursor.getDouble(cursor.getColumnIndex(COLUMN_DESIRED_GRADE)));
        return course;
    }
    public ContentValues generateContentValuesFromObject(Course entity) {
        if (entity == null) {
            return null;
        }
        ContentValues courseValues = new ContentValues();
        //add the course "COURSE NAME" and "DESIRED GRADE" to the course table
        courseValues.put(COLUMN_NAME, entity.getName());
        courseValues.put(COLUMN_DESIRED_GRADE, entity.getDesiredGrade());
        return courseValues;
    }
}
