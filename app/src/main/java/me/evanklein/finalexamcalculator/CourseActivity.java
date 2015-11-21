package me.evanklein.finalexamcalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<List<Assessment>> {

    // The Loader's id (this id is specific to the ListFragment's LoaderManager)
    private static final int LOADER_ID = 1;
    private  static final boolean DEBUG = true;
    private static final String TAG = "CustomLoaderExampleListFragment";
    private SQLiteDatabase db;
    private AssessmentDataSource assessmentDS;
    private CourseDataSource courseDS;
    private DBHelper mDbHelper;
    private Integer numRows = 0;
    private Course course;
    private TableLayout tableLayout;
    private DrawerLayout mDrawerLayout;
    private Boolean newCourse;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private Student student;
    private EditText desiredGradeET;
    private HashMap<Integer, Assessment> newAssessments =  new HashMap<Integer, Assessment>();
    private HashMap<Integer, String> fractionMarks = new HashMap<Integer, String>();
    private static boolean isCourseValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        registerForContextMenu(mDrawerList);

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        tableLayout = (TableLayout) findViewById(R.id.table_home);
        //set the desired grade edit text
        desiredGradeET = (EditText) findViewById(R.id.desired_grade);
        //set the student object
        student = Student.getInstance();

        mDbHelper = new DBHelper(getApplicationContext());
        db = mDbHelper.getWritableDatabase();
        assessmentDS = new AssessmentDataSource(db);
        courseDS = new CourseDataSource(db);

        // Initialize a Loader with id '1'. If the Loader with this id already
        // exists, then the LoaderManager will reuse the existing Loader.
        getLoaderManager().initLoader(LOADER_ID, null, this);
        //when a course activity is started, we need to get the name of the course, and load all of
        //its assessments from the database
        //reads courses from db

        //listen for the desired grade change here, so that when it is loaded from the database
        //the final message will be displayed
        setDesiredGradeListener();

        //get the course name which has been passed to you
        Bundle extras = getIntent().getExtras();
        String courseName;

        if (extras == null) {
            //the user is entering information for a NEW course, maybe
        }
        else {
            courseName = extras.getString(MainActivity.COURSE_NAME);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(courseName);
            }

            if (extras.getString(MainActivity.NEW_COURSE).equals(MainActivity.TRUE)) {
                //NEW COURSE
                newCourse = true;
                course = new Course();
                course.setName(courseName);
                final EditText type1EditText = (EditText) findViewById(R.id.type_1);
                final EditText yourMark1EditText = (EditText) findViewById(R.id.your_mark_1);
                final EditText worth1EditText = (EditText) findViewById(R.id.worth_1);
                final Button delBtn1 = (Button) findViewById(R.id.del_btn_1);
                setTypeListener(type1EditText, 1);
                setMarkListener(yourMark1EditText, 1);
                setWorthListener(worth1EditText, 1);
                setButtonListener(delBtn1, 1);
                newAssessments.put(1, new Assessment("", 0.0, false, 0.0));
                numRows += 1;
            }
            else {
                //EXISTING COURSE
                //remove the first default row
                tableLayout.removeView(tableLayout.findViewById(R.id.table_row_1));
                String[] whereArgs = new String[]{courseName};
                List<Course> courses = courseDS.read(String.format("%s = ?", CourseDataSource.COLUMN_NAME), whereArgs, null, null, null);
                //hopefully courses just contains one course
                course = courses.get(0);
                String selection = String.format("%s = ?", AssessmentDataSource.COLUMN_COURSE);
                List<Assessment> assessments = assessmentDS.read(selection, whereArgs, null, null, "id");
                //if assessments is empty, this is a new course
                //this is an existing course
                //so we want to iterate through all the assessments and display them in a table layout
                newCourse = false;
                displayAssessments(assessments);
                // display the desired grade
                //get the desired grade from the database
                desiredGradeET.setText(formatDecimal(course.getDesiredGrade()));
                updateTotals();
            }
        }
        setTouchListener();
        //set sidebar items
        addDrawerItems();
    }

    @Override
    public Loader<List<Assessment>> onCreateLoader(int id, Bundle args) {
        AssessmentLoader loader = new AssessmentLoader(getApplicationContext(), assessmentDS, null, null, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Assessment>> loader, List<Assessment> data) {
//        mAdapter.clear();
        for(int i = 0; i < data.size(); i++){
            //mAdapter.add(data.get(i));
        }
    }
    @Override
    public void onLoaderReset(Loader<List<Assessment>> arg0) {
        //mAdapter.clear();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
        db.close();
        assessmentDS = null;
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==mDrawerList.getId()) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            for (int i = 0; i<MainActivity.menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, MainActivity.menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        final String oldName = student.getCourses().get(info.position-1).getName();

        if (menuItemIndex == 0) {
            //Edit course name
            //put an alertdialog with the coursename already filled in
            AlertDialog.Builder alertDB = new AlertDialog.Builder(this);
            alertDB.setMessage("Please enter a new name for this course:");
            final EditText newNameET = new EditText(this);
            alertDB.setView(newNameET);
            addNewCourseNameListener(newNameET);
            alertDB.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String newName = newNameET.getText().toString();
                    //if they didn't change anything, don't change anything
//                    if (newName.equals(oldName)) {
//                        return;
//                    } else {
                    //validate the courseName
                    if (isCourseValid) {
                        editCourseName(oldName, newName, info.position);
                    }
//                    }
                }
            });
            alertDB.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            AlertDialog alert = alertDB.create();
            alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            alert.show();
            newNameET.setText(oldName);
            newNameET.setSelection(oldName.length());
        }
        else {//menu item index is 1 -> delete course
            Boolean deleteActiveCourse = false;
            if (info.position == (student.getCourses().indexOf(course)+1)) {
                deleteActiveCourse = true;
            }
            areYouSureCourse(oldName, deleteActiveCourse);

        }
        return true;
    }

    public void areYouSureCourse(final String courseToDelete, final Boolean delActvCourse) {
        AlertDialog.Builder alertDB = new AlertDialog.Builder(this);
        alertDB.setMessage("Are you sure you want to delete \"" + courseToDelete + "\"?");
        alertDB.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteCourse(courseToDelete);
                if (delActvCourse) {
                    //go home
                    startActivity(new Intent(CourseActivity.this, MainActivity.class));
                }

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
    public void deleteCourse(String toDeleteName) {
        //change it in the student class
        student.removeCourseWithName(toDeleteName);
        //delete it from the database
        SQLiteStatement stmt = db.compileStatement(
                "DELETE FROM " + CourseDataSource.TABLE_NAME
                        + " WHERE " + CourseDataSource.COLUMN_NAME + " = ?");
//        "UPDATE course SET name='CSC374' where name='CSC373'"
        stmt.bindString(1, toDeleteName);
        stmt.execute();
        //change it in the drawers
        addDrawerItems();
    }
    public void editCourseName(String oldName, String newName, Integer index) {
        if (oldName.equals(course.getName())) {
            //if we're editing the course we're currently in, just change the title
            getSupportActionBar().setTitle(newName);
            course.setName(newName);
        }
        //change it in the student class
        student.changeCourse(oldName, newName);
        //change this course's name in the database
        SQLiteStatement stmt = db.compileStatement(
                "UPDATE " + CourseDataSource.TABLE_NAME
                        + " SET " + CourseDataSource.COLUMN_NAME + " = ?"
                        + " WHERE " + CourseDataSource.COLUMN_NAME + " = ?");
//        "UPDATE course SET name='CSC374' where name='CSC373'"
        stmt.bindString(1, newName);
        stmt.bindString(2, oldName);
        stmt.execute();
        //also need to update the assessment table
        //updateAssessmentTableOnEdit(oldName, newName);
        //change it in the drawers
        addDrawerItems();
    }

    public void addNewCourseNameListener(final EditText courseET) {
        courseET.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                //just check if that name already exists
                String courseSTR = s.toString();
                courseSTR.replace(" ", "");
                if (student.getCourseWithName(courseSTR) != null) {
                    courseET.setError("This course is already in your list of courses! Please enter a different course name.");
                    isCourseValid = false;
                }
                else {
                    isCourseValid = true;
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    public void displayAssessments(List<Assessment> assessments) {
        //if assessments is empty, this is a new course
        if (assessments.size() == 0) {
            //add a new blank row at the top
            addRow(numRows + 1, new Assessment("", 0.0, false, 0.0));
        }
        else {
            for (int i = 0; i < assessments.size(); i++) {
                if (assessments.get(i).getMarkSTR() != null) {
                    fractionMarks.put(i+1, assessments.get(i).getMarkSTR());
                }
                if (assessments.get(i).isEmpty()) {
                    //just add a blank row
                    addRow(i + 1, new Assessment("", 0.0, false, 0.0));
                } else {
                    addRow(i + 1, assessments.get(i));
                }
            }
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
        course.addAssessment(currentRowNum, a);
        TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams newTypeLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
        TableRow.LayoutParams newYourMarkLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
        TableRow.LayoutParams newWorthLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
        TableRow.LayoutParams newDeleteBtnLayoutParams = new TableRow.LayoutParams(getSizeInDP(30), getSizeInDP(30));
        newTypeLayoutParams.column = 0;
        newYourMarkLayoutParams.column = 1;
        newWorthLayoutParams.column = 2;
        newDeleteBtnLayoutParams.column = 3;
        final EditText newType = new EditText(this);
        final EditText newYourMark = new EditText(this);
        final EditText newWorth = new EditText(this);
        final Button newDelBtn = new Button(this, null, android.R.attr.buttonStyleSmall);
        String rowTag = "row_" + Integer.toString(currentRowNum);
        tableRow.setTag(rowTag);
        String typeString = "type_" + Integer.toString(currentRowNum);
        newType.setTag(typeString);
        newType.setLayoutParams(newTypeLayoutParams);
        newType.setHint("Test, etc.");
        newType.setMinWidth(getSizeInDP(60));
        newType.setMaxWidth(getSizeInDP(60));
        String yourMarkString = "your_mark_" + Integer.toString(currentRowNum);
        newYourMark.setTag(yourMarkString);
        newYourMark.setLayoutParams(newYourMarkLayoutParams);
        newYourMark.setInputType(InputType.TYPE_CLASS_NUMBER);
        newYourMark.setKeyListener(DigitsKeyListener.getInstance("0123456789./"));
        newYourMark.setMinWidth(getSizeInDP(10));
        newYourMark.setMaxWidth(getSizeInDP(10));
        String worthString = "worth_" + Integer.toString(currentRowNum);
        newWorth.setTag(worthString);
        newWorth.setLayoutParams(newWorthLayoutParams);
        newWorth.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        newWorth.setMinWidth(getSizeInDP(10));
        newWorth.setMaxWidth(getSizeInDP(10));
        String buttonString = "del_button_" + Integer.toString(currentRowNum);
        newDelBtn.setTag(buttonString);
        newDelBtn.setLayoutParams(newDeleteBtnLayoutParams);
        newDelBtn.setBackgroundResource(R.drawable.delete_button);
        newDelBtn.setText("X");
        setButtonListener(newDelBtn, currentRowNum);
        setTypeListener(newType, currentRowNum);
        setMarkListener(newYourMark, currentRowNum);
        setWorthListener(newWorth, currentRowNum);
        if (!a.isEmpty()) {
            //we want to fill in the values of the edit texts and then disable them
            newType.setText(a.getType());
            if (a.isMarked()) {
                newYourMark.setText(formatDecimal(a.getMark()));
            }
            newWorth.setText(formatDecimal(a.getWorth()));
//            disableEditText(newType);
//            disableEditText(newYourMark);
//            disableEditText(newWorth);
        }
        else {
            //if we're adding an empty assessment, it is a new assessment
            newAssessments.put(currentRowNum, a);
        }
        tableRow.addView(newType);
        tableRow.addView(newYourMark);
        tableRow.addView(newWorth);
        tableRow.addView(newDelBtn);
        tableLayout.addView(tableRow, params);
        numRows += 1;
    }

    public int getSizeInDP(Integer size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics());
    }

    public void updateTotals() {
        TextView mark_so_far = (TextView)findViewById(R.id.mark_so_far);
        TextView worth_so_far = (TextView)findViewById(R.id.total_worth);
        Double worth = course.getTotalWorth();
        Double mark = course.getMarkSoFar();
        worth_so_far.setText(String.format("%s %%", formatDecimal(worth)));
        mark_so_far.setText(String.format("%.1f %%", mark));
        calculateRequiredMark();
    }

    public void removeExtraRows(Integer currentRowNum, TableLayout tableLayout) {
        //remove all empty rows > max(currentRowNum + 1, non-empty row)
        while (course.getAssessment(numRows-1) != null && course.getAssessment(numRows-1).isEmpty()
                && (numRows - 1 > currentRowNum)) {
            tableLayout.removeView(tableLayout.findViewWithTag("row_" + Integer.toString(numRows)));
            course.removeAssessment(numRows);
            numRows -= 1;
        }
    }

    public void setButtonListener(final Button button, final Integer currentRowNum) {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                //ask if they're sure

                areYouSure(course.getAssessment(currentRowNum).getType(), currentRowNum);

                //delete assessment and row

//                Intent i = new Intent(MainActivity.this, CourseActivity.class);
//                Bundle extras = new Bundle();
//                extras.putString(COURSE_NAME, courseName);
//                extras.putString(NEW_COURSE, FALSE);
//                i.putExtras(extras);
//                startActivity(i);
//                return;
            }
        });
    }

    public void areYouSure(String assessmentName, final Integer currentRowNum) {
        AlertDialog.Builder alertDB = new AlertDialog.Builder(this);
        alertDB.setMessage("Are you sure you want to delete \"" + assessmentName + "\"?");
        alertDB.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //delete assessment
                course.removeAssessment(currentRowNum);
                if (currentRowNum.equals(numRows)) {
                    //if we are deleting the last row, decrease the number of rows
                    numRows -= 1;
                }
                //delete row
                tableLayout.removeView(tableLayout.findViewWithTag("row_" + Integer.toString(currentRowNum)));
                //if we removed all the rows, need to add a first one in
                if (course.getAssessments().size() == 0) {
                    addRow(numRows + 1, new Assessment("", 0.0, false, 0.0));
                    return;
                }
                updateTotals();
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
    public void setDesiredGradeListener() {
        desiredGradeET.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                updateTotals();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }
    public void calculateRequiredMark() {
        String desiredGradeSTR = desiredGradeET.getText().toString();
        if (!("".equals(desiredGradeSTR))) {
            Double desiredGrade = Double.valueOf(desiredGradeET.getText().toString());
            course.setDesiredGrade(desiredGrade);
            Double requiredRestMark = course.getRequiredRestMark();
            //change the text of the message at the bottom
            TextView messageTV = (TextView) findViewById(R.id.final_message);
            if (requiredRestMark <= 0) {
                //they already have their desired grade -> tell them
                messageTV.setText(String.format
                        ("You already have %s%% in this course. You're good!",
                                formatDecimal(desiredGrade)));
            } else {
                messageTV.setText(String.format
                        ("You need %.1f%% in the rest of the course to get %s%% in this course.",
                                requiredRestMark, formatDecimal(desiredGrade)));
            }
        }
    }

    public void setTypeListener(final EditText typeET, Integer rowNum) {
        final Integer currentRowNum = rowNum;
        typeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //if we just exited the type field
                if (!hasFocus) {
                    //change the assessment object
                    course.addAssessment(currentRowNum, new Assessment("", 0.0, false, 0.0)).setType(typeET.getText().toString());
                } else {
                    //we have entered focus: if the number of existing rows in the activity is
                    //more than current rowNum, don't do anything.
                    if (numRows.equals(currentRowNum)) {
                        //else, create a new row (INFLATE activity)
                        addRow(currentRowNum + 1, new Assessment("", 0.0, false, 0.0));
                    }
                    removeExtraRows(currentRowNum, tableLayout);
                }
                calculateRequiredMark();
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
                    String markSTR = markET.getText().toString();
                    if (markSTR.contains("/")) {
                        //we lost focus, so we want to display this as a percentage, but save the fraction
                        fractionMarks.put(currentRowNum, markSTR);
                        Assessment currentA = course.getAssessment(currentRowNum);
                        //we know currentA is not null because the markET is not empty (it contains "/")
                        markET.setText(formatDecimal(currentA.getMark()));
                    }
//                    Assessment currentAss = course.addAssessment(currentRowNum, new Assessment("", 0.0, false, 0.0));
//                    //change the assessment object
//                    if (!("".equals(markET.getText().toString()))) {
//                        currentAss.setMark(Double.valueOf(markET.getText().toString()));
//                        currentAss.setMarked(true);
//                    } else {
//                        //not marked
//                        currentAss.setMarked(false);
//                        currentAss.setMark(0.0);
//                    }
                } else {
                    //we have entered focus:
                    // display the mark as a fraction (if necessary)
                    String fraction = fractionMarks.get(currentRowNum);
                    if (fraction != null) {
                        markET.setText(fraction);
                    }
                    // if the number of existing rows in the activity is
                    //more than current rowNum, don't do anything.
                    if (numRows.equals(currentRowNum)) {
                        //else, create a new row (INFLATE activity)
                        addRow(currentRowNum + 1, new Assessment("", 0.0, false, 0.0));
                    }
                    removeExtraRows(currentRowNum, tableLayout);
                }
                calculateRequiredMark();
                updateTotals();
            }
        });
        markET.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                //if there is no mapping for this key
                Assessment currentA = course.getAssessment(currentRowNum);
                if (currentA == null) {
                    //add a new assessment
                    currentA = course.addAssessment(currentRowNum, new Assessment("", 0.0, false, 0.0));
                }
                //we want to check if they wrote a fraction or a percentage
                Double result = 0.0;
                String markSTR = s.toString();
                if (markSTR.contains("/")) {
                    String fraction[] = markSTR.split("/");
                    if (fraction.length != 2) {
                        markET.setError("Not a valid fraction!");
                    } else {
                        try {
                            Double numerator = Double.parseDouble(fraction[0]);
                            Double denominator = Double.parseDouble(fraction[1]);
                            result = numerator / denominator * 100;
                            currentA.setMark(result);
                            currentA.setMarked(true);
                        } catch (Exception e) {
                            markET.setError("Not a valid fraction!");
                        }
                    }
                } else {

                    //change the assessment object
                    if (!("".equals(markET.getText().toString()))) {
                        currentA.setMark(Double.valueOf(s.toString()));
                        currentA.setMarked(true);
                    } else {
                        //not marked
                        currentA.setMarked(false);
                        currentA.setMark(0.0);
                    }
                    //calculateRequiredMark();
                    //updateTotals();
                }
                updateTotals();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
//                    if (!("".equals(worthET.getText().toString()))) {
//                        course.addAssessment(currentRowNum, new Assessment("", 0.0, false, 0.0)).setWorth(Double.valueOf(worthET.getText().toString()));
//                    } else {
//                        course.getAssessment(currentRowNum).setWorth(0.0);
//                    }
                } else {
                    //we have entered focus: if the number of existing rows in the activity is
                    //more than current rowNum, don't do anything.
                    if (numRows.equals(currentRowNum)) {
                        //else, create a new row (INFLATE activity)
                        addRow(currentRowNum + 1, new Assessment("", 0.0, false, 0.0));
                    }
                    removeExtraRows(currentRowNum, tableLayout);
                }
                //either way, want to update the mark so far (or just make sure it's up to date
                //and the worth so far
                calculateRequiredMark();
                updateTotals();
            }
        });
        worthET.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                //if there is no mapping for this key
                Assessment currentA = course.getAssessment(currentRowNum);
                if (currentA == null) {
                    //add a new assessment
                    currentA = course.addAssessment(currentRowNum, new Assessment("", 0.0, false, 0.0));
                }
                //change the assessment object
                if (!("".equals(worthET.getText().toString()))) {
                    currentA.setWorth(Double.valueOf(worthET.getText().toString()));
                } else {
                    currentA.setWorth(0.0);
                }
                //calculateRequiredMark();
                updateTotals();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

    }

    public void setTouchListener() {
        findViewById(R.id.main_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//                InputMethodManager imm = (InputMethodManager) view.getContext()
//                        .getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    };

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void addCourseToDB() {
        //create a database of this user's data so they can save it
//        DBHelper mDBHelper = new DBHelper(getApplicationContext());
//        db = mDBHelper.getWritableDatabase();
//        CourseDataSource cds = new CourseDataSource(db);
        courseDS.insert(course);
//        ContentValues courseValues = new ContentValues();
//        //add the course "COURSE NAME" and "DESIRED GRADE" to the course table
//        courseValues.put(CourseDataSource.COLUMN_NAME, course.getName());
//        courseValues.put(CourseDataSource.COLUMN_DESIRED_GRADE, course.getDesiredGrade());
//        db.insert(
//                Course,
//                null,
//                courseValues);
    }

    public void updateCourseInDB() {
        //create a database of this user's data so they can save it

        //just going to delete the table and then make a new one
         courseDS.update(course);

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
        Integer id = 1;
        for (Map.Entry<Integer, Assessment> aEntry : course.getAssessments().entrySet()) {
            //put all nonempty assessments in, with id incremented only when we've put one in
            Assessment a = aEntry.getValue();
            if (!a.isEmpty()) {
                //this is a new assignment, add it
                ContentValues assValues = new ContentValues();
                assValues.put(AssessmentDataSource.COLUMN_COURSE, course.getName());
                assValues.put(AssessmentDataSource.COLUMN_ID, id);
                assValues.put(AssessmentDataSource.COLUMN_TYPE, a.getType());
                assValues.put(AssessmentDataSource.COLUMN_MARK, a.getMark());
                assValues.put(AssessmentDataSource.COLUMN_MARK_STRING, fractionMarks.get(aEntry.getKey()));
                assValues.put(AssessmentDataSource.COLUMN_MARKED, a.isMarked());
                assValues.put(AssessmentDataSource.COLUMN_WORTH, a.getWorth());
                db.insertOrThrow(
                        AssessmentDataSource.TABLE_NAME,
                        null,
                        assValues);
                id++;

            }
        }
    }
    public void dropAndRecreateTable() {
        String assessment_table = "DROP TABLE IF EXISTS assessment;";
        db.execSQL(assessment_table);
        db.execSQL(AssessmentDataSource.CREATE_COMMAND);
    }
    public void deleteAllAssessmentsInDB() {
        //delete all assessments from the database
        SQLiteStatement stmt = db.compileStatement("DELETE FROM " + AssessmentDataSource.TABLE_NAME
                + " WHERE " + AssessmentDataSource.COLUMN_COURSE + " = ?");
        stmt.bindString(1, course.getName());
        stmt.execute();
    }

    public void saveCourse(View view) {
        if (newCourse) {
            student.addCourse(course);
            addCourseToDB();
        }
        else {
            //update the course
            updateCourseInDB();
        }
        deleteAllAssessmentsInDB();
        addAssessmentsToDB();
        db.close();
        AlertDialog.Builder alertDB = new AlertDialog.Builder(this);
        alertDB.setMessage("Saved!");
        alertDB.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                return;
            }
        });
        AlertDialog alert = alertDB.create();
        alert.show();
        addDrawerItems();
        //go home
        startActivity(new Intent(this, MainActivity.class));
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

    public void addDrawerItems() {
        ArrayList<Course> courses = student.getCourses();
        String[] courseNames = new String[courses.size() + 1];
        courseNames[0] = "Home";
        for (int i = 0; i < courses.size(); i++) {
            courseNames[i+1] = courses.get(i).getName();
        }
        mAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, courseNames);
        mDrawerList.setAdapter(mAdapter);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        if (position == 0) {
            //go to main activity
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
        else if (position == (student.getCourses().indexOf(course)+1)) {
            //they selected the current course, do nothing, just close drawer
            mDrawerLayout.closeDrawers();
        }
        else {
            mDrawerList.setItemChecked(position, true);
            Integer index = position;
            // Create a new fragment and specify the planet to show based on position
            Intent i = new Intent(this, CourseActivity.class);
            String courseName = student.getCourses().get(index-1).getName();
            Bundle extras = new Bundle();
            extras.putString(MainActivity.COURSE_NAME, courseName);
            extras.putString(MainActivity.NEW_COURSE, MainActivity.FALSE);
            i.putExtras(extras);
            mDrawerLayout.closeDrawer(mDrawerList);
//            i.putExtra(MainActivity.COURSE_NAME, student.getCourses().get(position).getName());
            startActivity(i);
            // Highlight the selected item, update the title, and close the drawer
        }
    }

    public String formatDecimal(Double d) {
        String s = d.toString();
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        String result = decimalFormat.format(Double.valueOf(s));
        return result;
    }
}
