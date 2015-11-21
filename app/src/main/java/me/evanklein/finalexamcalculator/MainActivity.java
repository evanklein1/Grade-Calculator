package me.evanklein.finalexamcalculator;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Course>>{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    //constants
    final static String COURSE_NAME = "courseName";
    final static String NEW_COURSE = "newCourse";
    final static String TRUE = "True";
    final static String FALSE = "False";
    String[] menuItems = {"Edit Name", "Delete Course"};
    private Student student;
    private ArrayAdapter<String> mAdapter;
    private SQLiteDatabase db;
    private CourseDataSource mDataSource;
    private DBHelper mDbHelper;
    private static final int LOADER_ID = 1;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        tableLayout = (TableLayout) findViewById(R.id.table_home);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        registerForContextMenu(mDrawerList);

        mDbHelper = new DBHelper(getApplicationContext());
        db = mDbHelper.getWritableDatabase();
        mDataSource = new CourseDataSource(db);

        // Initialize a Loader with id '1'. If the Loader with this id already
        // exists, then the LoaderManager will reuse the existing Loader.
        getLoaderManager().initLoader(LOADER_ID, null, this);
        //initialize a student object, which is a singleton class and represents the student using
        //the app
        student = Student.getInstance();
        //get all the courses from the database
        List<Course> courses = mDataSource.read();
        student.setCourses((ArrayList) courses);
        addDrawerItems();
        //if courses are empty, want to just print a string telling them to add courses
        if (courses.size() == 0) {
            displayNoCoursesMessage();
        }
        else {
            //now we want to iterate through all the assessments and display them in a table layout
            displayCourses();
        }
    }

    @Override
    public Loader<List<Course>> onCreateLoader(int id, Bundle args) {
        CourseLoader loader = new CourseLoader(getApplicationContext(), mDataSource, null, null, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Course>> loader, List<Course> data) {
//        mAdapter.clear();
        for(int i = 0; i < data.size(); i++){
            //mAdapter.add(data.get(i));
        }
    }
    @Override
    public void onLoaderReset(Loader<List<Course>> arg0) {
        //mAdapter.clear();
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

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==mDrawerList.getId()) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
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
            alertDB.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String newName = newNameET.getText().toString();
                    //if they didn't change anything, don't change anything
                    if (newName.equals(oldName)) {
                        return;
                    }
                    else {
                        //validate the courseName
                        if (validateCourseName(newName)) {
                            editCourseName(oldName, newName, info.position);
                        }
                    }
                }
            });
            alertDB.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            showSoftKeyboard(newNameET);
            AlertDialog alert = alertDB.create();
            alert.show();
            newNameET.requestFocus();
        }
        else {//menu item index is 1 -> delete course


        }
        return true;
    }

    public void deleteCourse(String toDeleteName) {
        //change it in the student class
        student.removeCourseWithName(toDeleteName);
        //delete it from the database
        SQLiteStatement stmt = db.compileStatement(
                "UPDATE " + CourseDataSource.TABLE_NAME
                        + " SET " + CourseDataSource.COLUMN_NAME + " = ?"
                        + " WHERE " + CourseDataSource.COLUMN_NAME + " = ?");
//        "UPDATE course SET name='CSC374' where name='CSC373'"
        stmt.bindString(1, newName);
        stmt.bindString(2, oldName);
        stmt.execute();
        //change it in the drawers
        addDrawerItems();
        //change it on the home screen
        EditText courseNameET = (EditText) tableLayout.findViewWithTag("row_" + Integer.toString(index));
        courseNameET.setText(newName);
    }
    public void editCourseName(String oldName, String newName, Integer index) {
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
        updateAssessmentTableOnEdit(oldName, newName);
        //change it in the drawers
        addDrawerItems();
        //change it on the home screen
        EditText courseNameET = (EditText) tableLayout.findViewWithTag("row_" + Integer.toString(index));
        courseNameET.setText(newName);
    }

    public void updateAssessmentTableOnEdit(String oldName, String newName) {
        SQLiteStatement stmtA = db.compileStatement(
                "UPDATE " + AssessmentDataSource.TABLE_NAME
                        + " SET " + AssessmentDataSource.COLUMN_COURSE + " = ?"
                        + " WHERE " + AssessmentDataSource.COLUMN_COURSE + " = ?");
//        "UPDATE assessment SET name='CSC374' where name='CSC373'"
        stmtA.bindString(1, newName);
        stmtA.bindString(2, oldName);
        stmtA.execute();
    }
    public void promptCourseName(View view) {
        //ask for course name
        AlertDialog.Builder alertDB = new AlertDialog.Builder(this);
        alertDB.setMessage("Enter the name of the course:");
        final EditText courseNameET = new EditText(this);
        alertDB.setView(courseNameET);
        alertDB.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String courseName = courseNameET.getText().toString();
                //validate the courseName
                if (validateCourseName(courseName)) {
                    Intent i = new Intent(MainActivity.this, CourseActivity.class);
                    //THIS IS A NEW COURSE
                    Bundle extras = new Bundle();
                    extras.putString(COURSE_NAME, courseName);
                    extras.putString(NEW_COURSE, TRUE);
                    i.putExtras(extras);
                    startActivity(i);
                    return;
                }
            }
        });
        alertDB.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        showSoftKeyboard(courseNameET);
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        AlertDialog alert = alertDB.create();
        alert.show();
    }

    public boolean validateCourseName(String name) {
        if (student.getCourseWithName(name)!=null) {
            //they can't enter this name
            AlertDialog.Builder alertDB = new AlertDialog.Builder(this);
            alertDB.setMessage("This course is already in your list of courses. Please enter a different course name.");
            alertDB.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    return;
                }
            });
            AlertDialog alert = alertDB.create();
            alert.show();
            return false;
        }
        else {
            return true;
        }
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    public void editCourse(View view) {
        TableRow tableRow = (TableRow) view.getParent();
        TextView courseNameTV = (TextView) tableRow.getChildAt(0);
        String courseName = courseNameTV.toString();
        Intent i = new Intent(this, CourseActivity.class);
        Bundle extras = new Bundle();
        extras.putString(COURSE_NAME, courseName);
        extras.putString(NEW_COURSE, FALSE);
        i.putExtras(extras);
        startActivity(i);
    }

    public void addDrawerItems() {
        //first add the home button

        ArrayList<Course> courses = student.getCourses();
        String[] courseNames = new String[courses.size() + 1];
        courseNames[0] = "Home";
        for (int i = 0; i < courses.size(); i++) {
            courseNames[i+1] = courses.get(i).getName();
        }
        mAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, courseNames);
        mDrawerList.setAdapter(mAdapter);
    }

    public void displayCourses() {
        //if assessments is empty, this is a new course
        ArrayList<Course> courses = student.getCourses();
        for(int i = 0; i < courses.size(); i++) {
            addRow(i, courses.get(i));
        }
    }

    public void displayNoCoursesMessage() {
        TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams newNameLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
        final TextView noCoursesTV = new TextView(this);
        noCoursesTV.setLayoutParams(newNameLayoutParams);
        noCoursesTV.setText("It looks like you haven't added courses yet.");
        tableRow.addView(noCoursesTV);
        tableLayout.addView(tableRow, params);
    }
    public void addRow(final Integer currentRowNum, Course c) {
        TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams newNameLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
        TableRow.LayoutParams newDGLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
        TableRow.LayoutParams newButtonLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
        newNameLayoutParams.column = 0;
        newDGLayoutParams.column = 1;
        newButtonLayoutParams.column = 2;
        final TextView newName = new TextView(this);
        final TextView newDG = new TextView(this);
        final Button newButton = new Button(this);
        String rowTag = "row_" + Integer.toString(currentRowNum);
        tableRow.setTag(rowTag);
        //set tag and layout params for name
        String nameString = "name_" + Integer.toString(currentRowNum);
        newName.setTag(nameString);
        newName.setLayoutParams(newNameLayoutParams);
        newName.setTextSize(20);
        String dgString = "current_grade_" + Integer.toString(currentRowNum);
        newDG.setTag(dgString);
        newDG.setLayoutParams(newDGLayoutParams);
        newDG.setTextSize(20);
        String buttonString = "edit_button_" + Integer.toString(currentRowNum);
        newButton.setTag(buttonString);
        newButton.setLayoutParams(newButtonLayoutParams);
        //set onclick listener for button
        setButtonListener(newButton, c.getName());
            //we want to fill in the values of the edit texts and then disable them
        newName.setText(c.getName());
        //newDG.setText(c.getCurrentGrade().toString());
        newButton.setText("View");
        tableRow.addView(newName);
        tableRow.addView(newDG);
        tableRow.addView(newButton);
        tableLayout.addView(tableRow, params);
    }

    public void setButtonListener(final Button button, final String courseName) {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent i = new Intent(MainActivity.this, CourseActivity.class);
                Bundle extras = new Bundle();
                extras.putString(COURSE_NAME, courseName);
                extras.putString(NEW_COURSE, FALSE);
                i.putExtras(extras);
                startActivity(i);
                return;
            }
        });
    }

    private class DrawerItemLongClickListener implements ListView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            showCourseOptions(position);
            return true;
        }
    }

    private void showCourseOptions(int position) {

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        if (position == 0) {
            //go to main activity
            //i.e. do nothing
            mDrawerLayout.closeDrawers();
        }
        else {
            // Create a new fragment and specify the planet to show based on position
            mDrawerList.setItemChecked(position, true);
            Integer index = position;
            //setTitle(student.getCourses().get(position).getName());
            Intent i = new Intent(this, CourseActivity.class);
            String courseName = student.getCourses().get(index-1).getName();
            Bundle extras = new Bundle();
            extras.putString(COURSE_NAME, courseName);
            extras.putString(NEW_COURSE, FALSE);
            i.putExtras(extras);
            mDrawerLayout.closeDrawer(mDrawerList);
            startActivity(i);
            // Highlight the selected item, update the title, and close the drawer
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        CharSequence mTitle = title;
        getActionBar().setTitle(mTitle);
    }
}
