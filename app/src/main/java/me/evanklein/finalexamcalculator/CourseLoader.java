package me.evanklein.finalexamcalculator;

import java.util.List;
import android.content.Context;
public class CourseLoader extends AbstractDataLoader<List> {
    private DataSource mDataSource;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mGroupBy;
    private String mHaving;
    private String mOrderBy;

    public CourseLoader(Context context, DataSource dataSource, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        super(context);
        mDataSource = dataSource;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mGroupBy = groupBy;
        mHaving = having;
        mOrderBy = orderBy;
    }

    @Override
    protected List buildList() {
        List testList = mDataSource.read(mSelection, mSelectionArgs, mGroupBy, mHaving,	mOrderBy);
        return testList;
    }
    public void insert(Course entity) {
        new InsertTask(this).execute(entity);
    }
    public void update(Course entity) {
        new UpdateTask(this).execute(entity);
    }
    public void delete(Course entity) {
        new DeleteTask(this).execute(entity);
    }
    private class InsertTask extends ContentChangingTask<Course, Void, Void> {
        InsertTask(CourseLoader loader) {
            super(loader);
        }
        @Override
        protected Void doInBackground(Course... params) {
            mDataSource.insert(params[0]);
            return (null);
        }
    }
    private class UpdateTask extends ContentChangingTask<Course, Void, Void> {
        UpdateTask(CourseLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Course... params) {
            mDataSource.update(params[0]);
            return (null);
        }
    }
    private class DeleteTask extends ContentChangingTask<Course, Void, Void> {
        DeleteTask(CourseLoader loader) {
            super(loader);
        }
        @Override
        protected Void doInBackground(Course... params) {
            mDataSource.delete(params[0]);
            return (null);
        }
    }
}