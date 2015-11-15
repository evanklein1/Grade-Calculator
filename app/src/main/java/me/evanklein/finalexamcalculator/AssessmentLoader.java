package me.evanklein.finalexamcalculator;

/**
 * Created by Evan on 15-Nov-2015.
 */

import android.content.Context;

import java.util.List;

public class AssessmentLoader extends AbstractDataLoader<List> {
    private DataSource mDataSource;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mGroupBy;
    private String mHaving;
    private String mOrderBy;

    public AssessmentLoader(Context context, DataSource dataSource, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
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
    public void insert(Assessment entity) {
        new InsertTask(this).execute(entity);
    }
    public void update(Assessment entity) {
        new UpdateTask(this).execute(entity);
    }
    public void delete(Assessment entity) {
        new DeleteTask(this).execute(entity);
    }
    private class InsertTask extends ContentChangingTask<Assessment, Void, Void> {
        InsertTask(AssessmentLoader loader) {
            super(loader);
        }
        @Override
        protected Void doInBackground(Assessment... params) {
            mDataSource.insert(params[0]);
            return (null);
        }
    }
    private class UpdateTask extends ContentChangingTask<Assessment, Void, Void> {
        UpdateTask(AssessmentLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Assessment... params) {
            mDataSource.update(params[0]);
            return (null);
        }
    }
    private class DeleteTask extends ContentChangingTask<Assessment, Void, Void> {
        DeleteTask(AssessmentLoader loader) {
            super(loader);
        }
        @Override
        protected Void doInBackground(Assessment... params) {
            mDataSource.delete(params[0]);
            return (null);
        }
    }
}