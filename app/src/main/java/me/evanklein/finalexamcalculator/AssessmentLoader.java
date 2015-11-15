package me.evanklein.finalexamcalculator;

/**
 * Created by Evan on 15-Nov-2015.
 */

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class AssessmentLoader extends AsyncTaskLoader<List<Assessment>> {
    private DataSource mDataSource;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mGroupBy;
    private String mHaving;
    private String mOrderBy;

    protected List<Assessment> mLastDataList = null;
    /**
     * Runs on a worker thread, loading in our data. Delegates the real work to
     * concrete subclass' buildCursor() method.
     */
    @Override
    public List<Assessment> loadInBackground() {
        return buildList();
    }
    /**
     * Runs on the UI thread, routing the results from the background thread to
     * whatever is using the dataList.
     */
    public void deliverResult(List<Assessment> dataList) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            emptyDataList(dataList);
            return;
        }
        List<Assessment> oldDataList = mLastDataList;
        mLastDataList = dataList;
        if (isStarted()) {
            super.deliverResult(dataList);
        }
        if (oldDataList != null && oldDataList != dataList
                && oldDataList.size() > 0) {
            emptyDataList(oldDataList);
        }
    }
    /**
     * Starts an asynchronous load of the list data. When the result is ready
     * the callbacks will be called on the UI thread. If a previous load has
     * been completed and is still valid the result may be passed to the
     * callbacks immediately.
     *
     * Must be called from the UI thread.
     */
    @Override
    protected void onStartLoading() {
        if (mLastDataList != null) {
            deliverResult(mLastDataList);
        }
        if (takeContentChanged() || mLastDataList == null
                || mLastDataList.size() == 0) {
            forceLoad();
        }
    }
    /**
     * Must be called from the UI thread, triggered by a call to stopLoading().
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }
    /**
     * Must be called from the UI thread, triggered by a call to cancel(). Here,
     * we make sure our Cursor is closed, if it still exists and is not already
     * closed.
     */
    public void onCanceled(List<Assessment> dataList) {

        if (dataList != null && dataList.size() > 0) {
            emptyDataList(dataList);
        }
    }
    /**
     * Must be called from the UI thread, triggered by a call to reset(). Here,
     * we make sure our Cursor is closed, if it still exists and is not already
     * closed.
     */
    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();
        if (mLastDataList != null && mLastDataList.size() > 0) {
            emptyDataList(mLastDataList);
        }
        mLastDataList = null;
    }
    protected void emptyDataList(List<Assessment> dataList) {
        if (dataList != null && dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                dataList.remove(i);
            }
        }
    }
    public AssessmentLoader(Context context, DataSource dataSource, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        super(context);
        mDataSource = dataSource;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mGroupBy = groupBy;
        mHaving = having;
        mOrderBy = orderBy;
    }

    protected List buildList() {
        List testList = mDataSource.read(mSelection, mSelectionArgs, mGroupBy, mHaving,	mOrderBy);
        return testList;
    }
    public void insert(Assessment entity) {
        new InsertTask().execute(entity);
    }
    public void update(Assessment entity) {
        new UpdateTask().execute(entity);
    }
    public void delete(Assessment entity) {
        new DeleteTask().execute(entity);
    }
    private class InsertTask extends AsyncTask<Assessment, Void, Void> {
        @Override
        protected Void doInBackground(Assessment... params) {
            mDataSource.insert(params[0]);
            return (null);
        }
    }
    private class UpdateTask extends AsyncTask<Assessment, Void, Void> {

        @Override
        protected Void doInBackground(Assessment... params) {
            mDataSource.update(params[0]);
            return (null);
        }
    }
    private class DeleteTask extends AsyncTask<Assessment, Void, Void> {

        @Override
        protected Void doInBackground(Assessment... params) {
            mDataSource.delete(params[0]);
            return (null);
        }
    }
}