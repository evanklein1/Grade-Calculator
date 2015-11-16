package me.evanklein.finalexamcalculator;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Course>>{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    final static String COURSE_NAME = "courseName";
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

    public void promptCourseName(View view) {
        //ask for course name
        AlertDialog.Builder alertDB = new AlertDialog.Builder(this);
        alertDB.setMessage("Enter the name of the course:");
        final EditText courseNameET = new EditText(this);
        alertDB.setView(courseNameET);
        alertDB.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String courseName = courseNameET.getText().toString();
                Intent i = new Intent(MainActivity.this, CourseActivity.class);
                i.putExtra(COURSE_NAME, courseName);
                startActivity(i);
                return;
            }
        });
        alertDB.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        AlertDialog alert = alertDB.create();
        alert.show();
    }

    public void editCourse(View view) {
        TableRow tableRow = (TableRow) view.getParent();
        TextView courseNameTV = (TextView) tableRow.getChildAt(0);
        String courseName = courseNameTV.toString();
        Intent i = new Intent(this, CourseActivity.class);
        i.putExtra(COURSE_NAME, courseName);
        startActivity(i);
    }

    public void addDrawerItems() {
        //first add the home button

        ArrayList<Course> courses = student.getCourses();
        String[] courseNames = new String[courses.size() + 1];
        courseNames[0] = "Home";
        for (int i = 1; i < courses.size(); i++) {
            courseNames[i] = courses.get(i).getName();
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

        String dgString = "desired_grade_" + Integer.toString(currentRowNum);
        newDG.setTag(dgString);
        newDG.setLayoutParams(newDGLayoutParams);
        String buttonString = "edit_button_" + Integer.toString(currentRowNum);
        newButton.setTag(buttonString);
        newButton.setLayoutParams(newButtonLayoutParams);
        //set onclick listener for button
        setButtonListener(newButton, c.getName());
            //we want to fill in the values of the edit texts and then disable them
        newName.setText(c.getName());
        newDG.setText(c.getDesiredGrade().toString());
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
                i.putExtra(COURSE_NAME, courseName);
                startActivity(i);
                return;
            }
        });
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
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            i.putExtra(COURSE_NAME, student.getCourses().get(position).getName());
            startActivity(i);
            mDrawerLayout.closeDrawers();
        }
        else {
            // Create a new fragment and specify the planet to show based on position
            Intent i = new Intent(MainActivity.this, CourseActivity.class);
            i.putExtra(COURSE_NAME, student.getCourses().get(position).getName());
            startActivity(i);
            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
            setTitle(student.getCourses().get(position).getName());
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        CharSequence mTitle = title;
        getActionBar().setTitle(mTitle);
    }
}
