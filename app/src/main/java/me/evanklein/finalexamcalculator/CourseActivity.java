package me.evanklein.finalexamcalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<List<Assessment>> {

    private ArrayAdapter mAdapter;
    // The Loader's id (this id is specific to the ListFragment's LoaderManager)
    private static final int LOADER_ID = 1;
    private  static final boolean DEBUG = true;
    private static final String TAG = "CustomLoaderExampleListFragment";
    private SQLiteDatabase db;
    private AssessmentDataSource mDataSource;
    private DBHelper mDbHelper;
    private Integer numRows = 0;
    private Course course;
    private TableLayout tableLayout;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Boolean newCourse;
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

        tableLayout = (TableLayout) findViewById(R.id.table_home);

        mDbHelper = new DBHelper(getApplicationContext());
        db = mDbHelper.getWritableDatabase();
        mDataSource = new AssessmentDataSource(db);

        // Initialize a Loader with id '1'. If the Loader with this id already
        // exists, then the LoaderManager will reuse the existing Loader.
        getLoaderManager().initLoader(LOADER_ID, null, this);
        //when a course activity is started, we need to get the name of the course, and load all of
        //its assessments from the database
        //reads courses from db
//        DBHelper helper = new DBHelper(this);
//        SQLiteDatabase database = helper.getWritableDatabase();
//        CourseDataSource dataSource = new CourseDataSource(database);

        //get the course name which has been passed to you
        Bundle extras = getIntent().getExtras();
        String courseName;

        if (extras == null) {
            //the user is entering information for a NEW course, maybe
        }
        else {
            courseName = extras.getString(MainActivity.COURSE_NAME);
            //create a new Course to be used for this activity, set its name
            course = new Course();
            course.setName(courseName);
            String[] whereArgs = new String[] {courseName};
            List<Assessment> assessments = mDataSource.read("course = ?", whereArgs, null, null, "id");
            //if assessments is empty, this is a new course
            if (assessments.size() == 0) {
                newCourse = true;
                final EditText type1EditText = (EditText) findViewById(R.id.type_1);
                final EditText yourMark1EditText = (EditText) findViewById(R.id.your_mark_1);
                final EditText worth1EditText = (EditText) findViewById(R.id.worth_1);
                setTypeListener(type1EditText, 1);
                setMarkListener(yourMark1EditText, 1);
                setWorthListener(worth1EditText, 1);
            }
            else {
                //now we want to iterate through all the assessments and display them in a table layout
                newCourse = false;
                displayAssessments(assessments);
            }
        }
        setDesiredGradeListener();
        setTouchListener();
    }

    @Override
    public Loader<List<Assessment>> onCreateLoader(int id, Bundle args) {
        AssessmentLoader loader = new AssessmentLoader(getApplicationContext(), mDataSource, null, null, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Assessment>> loader, List<Assessment> data) {
        mAdapter.clear();
        for(int i = 0; i < data.size(); i++){
            mAdapter.add(data.get(i));
        }
    }
    @Override
    public void onLoaderReset(Loader<List<Assessment>> arg0) {
        mAdapter.clear();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
        db.close();
        mDataSource = null;
        mDbHelper = null;
        db = null;
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

    public void displayAssessments(List<Assessment> assessments) {
        //if assessments is empty, this is a new course
        for(int i = 0; i < assessments.size(); i++) {
            addRow(i, assessments.get(i));
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

    public void updateTotals() {
        TextView mark_so_far = (TextView)findViewById(R.id.mark_so_far);
        TextView worth_so_far = (TextView)findViewById(R.id.total_worth);
        Double worth = course.getTotalWorth();
        Double mark = course.getMarkSoFar();
        worth_so_far.setText(String.format("%.0f", worth));
        mark_so_far.setText(String.format("%.1f %%", mark));
    }

    public void removeExtraRow(Integer currentRowNum, TableLayout tableLayout) {
        if ((course.getAssessment(currentRowNum+1) != null) && course.getAssessment(currentRowNum+1).isEmpty()) {
            if ((course.getAssessment(currentRowNum+2) != null) && course.getAssessment(currentRowNum+2).isEmpty()) {
                //remove currentRowNum+2
                tableLayout.removeView(tableLayout.findViewWithTag("row_" + Integer.toString(currentRowNum + 2)));
                course.removeAssessment(currentRowNum+2);
                numRows -= 1;

            }
        }
    }

    public void setDesiredGradeListener() {
        final EditText desiredGradeET = (EditText) findViewById(R.id.desired_grade);
        desiredGradeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //if we just lost focus
                if (!hasFocus) {
                    //change the assessment object
                    Double desiredGrade = Double.valueOf(desiredGradeET.getText().toString());
                    course.setDesiredGrade(desiredGrade);
                    Double requiredRestMark = course.getRequiredRestMark();
                    //change the text of the message at the bottom
                    TextView messageTV = (TextView) findViewById(R.id.final_message);
                    messageTV.setText(String.format
                            ("You need %.1f%% in the rest of the course to get %.0f%% in this course.",
                                    requiredRestMark, desiredGrade));
                    updateTotals();

                }
            }
        });
    }

    public void setTypeListener(final EditText typeET, Integer rowNum) {
        final Integer currentRowNum = rowNum;
        typeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //if we just exited the type field
                if (!hasFocus) {
                    //change the assessment object
                    course.addAssessment(currentRowNum).setType(typeET.getText().toString());
                } else {
                    //we have entered focus: if the number of existing rows in the activity is
                    //more than current rowNum, don't do anything.
                    if (numRows.equals(currentRowNum)) {
                        //else, create a new row (INFLATE activity)
                        addRow(currentRowNum + 1, null);
                        numRows += 1;
                    }
                    removeExtraRow(currentRowNum, tableLayout);
                }
                updateTotals();
            }
        });
    }

    public void setMarkListener(final EditText markET, Integer rowNum) {
        final Integer currentRowNum = rowNum;
        markET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //if we just exited the type field
                if (!hasFocus) {
                    Assessment currentAss = course.addAssessment(currentRowNum);
                    //change the assessment object
                    if (!("".equals(markET.getText().toString()))) {
                        currentAss.setMark(Double.valueOf(markET.getText().toString()));
                        currentAss.setMarked(true);
                    } else {
                        //not marked
                        currentAss.setMarked(false);
                        currentAss.setMark(0.0);
                    }
                } else {
                    //we have entered focus: if the number of existing rows in the activity is
                    //more than current rowNum, don't do anything.
                    if (numRows.equals(currentRowNum)) {
                        //else, create a new row (INFLATE activity)
                        addRow(currentRowNum + 1, null);
                        numRows += 1;
                    }
                    removeExtraRow(currentRowNum, tableLayout);
                }
                updateTotals();
            }
        });
    }

    public void setWorthListener(final EditText worthET, Integer rowNum) {
        final Integer currentRowNum = rowNum;
        worthET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //if we just exited the type field
                if (!hasFocus) {
                    //change the assessment object
                    //check if assessment is empty
                    if (!("".equals(worthET.getText().toString()))) {
                        course.addAssessment(currentRowNum).setWorth(Double.valueOf(worthET.getText().toString()));
                    }
                    else {
                        course.getAssessment(currentRowNum).setWorth(0.0);
                    }
                } else {
                    //we have entered focus: if the number of existing rows in the activity is
                    //more than current rowNum, don't do anything.
                    if (numRows.equals(currentRowNum)) {
                        //else, create a new row (INFLATE activity)
                        addRow(currentRowNum + 1, null);
                        numRows += 1;
                    }
                    removeExtraRow(currentRowNum, tableLayout);
                }
                //either way, want to update the mark so far (or just make sure it's up to date
                //and the worth so far
                updateTotals();
            }
        });
    }

    public void setTouchListener() {
        findViewById(R.id.main_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) view.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    };

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void addCourseToDB() {
        //create a database of this user's data so they can save it
        DBHelper mDBHelper = new DBHelper(getApplicationContext());
        db = mDBHelper.getWritableDatabase();
        CourseDataSource cds = new CourseDataSource(db);
        cds.insert(course);
//        ContentValues courseValues = new ContentValues();
//        //add the course "COURSE NAME" and "DESIRED GRADE" to the course table
//        courseValues.put(CourseDataSource.COLUMN_NAME, course.getName());
//        courseValues.put(CourseDataSource.COLUMN_DESIRED_GRADE, course.getDesiredGrade());
//        db.insert(
//                Course,
//                null,
//                courseValues);
    }

    public void addAssessmentsToDB() {
        //add the assessments to the ass table
        for (Map.Entry<Integer, Assessment> aEntry : course.getAssessments().entrySet()) {
            ContentValues assValues = new ContentValues();
            Assessment a = aEntry.getValue();
            assValues.put(AssessmentDataSource.COLUMN_COURSE, course.getName());
            assValues.put(AssessmentDataSource.COLUMN_ID, aEntry.getKey());
            assValues.put(AssessmentDataSource.COLUMN_TYPE, a.getType());
            assValues.put(AssessmentDataSource.COLUMN_MARK, a.getMark());
            assValues.put(AssessmentDataSource.COLUMN_MARKED, a.isMarked());
            assValues.put(AssessmentDataSource.COLUMN_WORTH, a.getWorth());
            db.insert(
                    AssessmentDataSource.TABLE_NAME,
                    null,
                    assValues);
        }
    }

    public void promptCourseName(View view) {
        //ask for course name
        AlertDialog.Builder alertDB = new AlertDialog.Builder(this);
        alertDB.setMessage("Enter the name of this course:");
        final EditText courseNameET = new EditText(this);
        alertDB.setView(courseNameET);
        alertDB.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String courseName = courseNameET.getText().toString();
                saveCourse(courseName);
                return;
            }
        });
        alertDB.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog alert = alertDB.create();
        alert.show();
    }

    public void saveCourse(String name) {
        course.setName(name);
        if (newCourse) {
            addCourseToDB();
            addAssessmentsToDB();
        }
        else {
            //updateCourse
        }

        //add course to sidebar
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerList = (ListView) findViewById(R.id.left_drawer);
//        List<String> mCourses = new ArrayList<String>();
//        mCourses.add(course.getName());
//        // Set the adapter for the list view
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
//                R.layout.nav_bar_layout, mCourses));
//        // Set the list's click listener
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }


}
