package me.evanklein.finalexamcalculator;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.List;

public class CourseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<List> {

    private ArrayAdapter mAdapter;
    // The Loader's id (this id is specific to the ListFragment's LoaderManager)
    private static final int LOADER_ID = 1;
    private  static final boolean DEBUG = true;
    private static final String TAG = "CustomLoaderExampleListFragment";
    private SQLiteDatabase mDatabase;
    private AssessmentDataSource mDataSource;
    private DBHelper mDbHelper;
    private Course course;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDbHelper = new DBHelper(getApplicationContext());
        mDatabase = mDbHelper.getWritableDatabase();
        mDataSource = new AssessmentDataSource(mDatabase);

        // Initialize a Loader with id '1'. If the Loader with this id already
        // exists, then the LoaderManager will reuse the existing Loader.
        getLoaderManager().initLoader(LOADER_ID, null, this);
        //when a course activity is started, we need to get the name of the course, and load all of
        //its assessments from the database
        //reads courses from db
//        DBHelper helper = new DBHelper(this);
//        SQLiteDatabase database = helper.getWritableDatabase();
//        CourseDataSource dataSource = new CourseDataSource(database);
        List<Assessment> assessments = mDataSource.read();
        //now we want to iterate through all the assessments and display them in a table layout

        mDbHelper.close();
        mDatabase.close();
    }

    @Override
    public Loader<List> onCreateLoader(int id, Bundle args) {
        AssessmentLoader loader = new AssessmentLoader(getApplicationContext(), mDataSource, null, null, null, null, null);
        return loader;
    }
    @Override
    public void onLoadFinished(Loader<List<Assessment>> loader, List<Assessment> data) {
        if (DEBUG) Log.i(TAG, "+++ onLoadFinished() called! +++");
        mAdapter.clear();
        for(Assessment assessment : data){
            mAdapter.add(assessment);
        }
    }
    @Override
    public void onLoaderReset(Loader<List> arg0) {
        mAdapter.clear();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
        mDatabase.close();
        mDataSource = null;
        mDbHelper = null;
        mDatabase = null;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadAssessments() {

    }

    public void displayAssessments(List<Assessment> assessments) {
        //for assessment in list
        for(int i = 0; i < assessments.size(); i++) {
            addRow(i);
        }

    }
    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    public void addRow(final Integer currentRowNum, Assessment a) {
        course.addAssessment(currentRowNum);
        TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams newTypeLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
        TableRow.LayoutParams newYourMarkLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
        TableRow.LayoutParams newWorthLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
        newTypeLayoutParams.column = 0;
        newYourMarkLayoutParams.column = 1;
        newWorthLayoutParams.column = 2;
        final EditText newType = new EditText(this);
        final EditText newYourMark = new EditText(this);
        final EditText newWorth = new EditText(this);
        String rowTag = "row_" + Integer.toString(currentRowNum);
        tableRow.setTag(rowTag);
        String typeString = "type_" + Integer.toString(currentRowNum);
        newType.setTag(typeString);
        newType.setLayoutParams(newTypeLayoutParams);
        String yourMarkString = "your_mark_" + Integer.toString(currentRowNum);
        newYourMark.setTag(yourMarkString);
        newYourMark.setLayoutParams(newYourMarkLayoutParams);
        newYourMark.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        String worthString = "worth_" + Integer.toString(currentRowNum);
        newWorth.setTag(worthString);
        newWorth.setLayoutParams(newWorthLayoutParams);
        newWorth.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        setTypeListener(newType, currentRowNum);
        setMarkListener(newYourMark, currentRowNum);
        setWorthListener(newWorth, currentRowNum);
        if (a != null) {
            //we want to fill in the values of the edit texts and then disable them
            newType.setText(a.getType());
            newYourMark.setText(a.getMark().toString());
            newWorth.setText(a.getWorth().toString());
            disableEditText(newType);
            disableEditText(newYourMark);
            disableEditText(newWorth);
        }
        tableRow.addView(newType);
        tableRow.addView(newYourMark);
        tableRow.addView(newWorth);
        tableLayout.addView(tableRow, params);
    }
}
