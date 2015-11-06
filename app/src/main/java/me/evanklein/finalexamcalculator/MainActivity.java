package me.evanklein.finalexamcalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private HashMap<String, EditText> editTextMap;
    private Integer numRows = 0;
    Assessment a1;
    Course course;
    TableLayout tableLayout;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //map the variable names to values
        LinearLayout linearLayout = new LinearLayout(this);
        numRows = 1;
        editTextMap = new HashMap<String, EditText>();
        course = new Course();
        editTextMap.put("type_1", (EditText) findViewById(R.id.type_1));
        editTextMap.put("your_mark_1", (EditText) findViewById(R.id.your_mark_1));
        editTextMap.put("worth_1", (EditText) findViewById(R.id.worth_1));

        tableLayout = (TableLayout) findViewById(R.id.table_home);
        //create an Assessment object for the 1st row
//        course.addAssessment(1);
//        a1 = course.getAssessment(1);
        final EditText type1EditText = (EditText) findViewById(R.id.type_1);
        final EditText yourMark1EditText = (EditText) findViewById(R.id.your_mark_1);
        final EditText worth1EditText = (EditText) findViewById(R.id.worth_1);
        setTypeListener(type1EditText, 1);
        setMarkListener(yourMark1EditText, 1);
        setWorthListener(worth1EditText, 1);
        setDesiredGradeListener();
        setTouchListener();
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

    //validate returns false if the entire row is not filled in,
    //or raises an error if there is something invalid entered
//    public boolean validate(Integer rowNum) {
//        //check if the values in rowNum are empty
//        String typeStr = "type_" + Integer.toString(rowNum);
//        String yourMarkStr = "your_mark_" + Integer.toString(rowNum);
//        String worthStr = "worth_" + Integer.toString(rowNum);
//        if ("".equals(editTextMap.get(typeStr))) {
//            //if it's empty, return False
//            return false;
//        }
//        else {
//            //check if it's in the proper format
//            //text can pretty much be anything
//        }
//        if ("".equals())
//    }
    public void addRow(final Integer currentRowNum) {
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
                        addRow(currentRowNum + 1);
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
                    }
                    else {
                        //not marked
                        currentAss.setMarked(false);
                        currentAss.setMark(0.0);
                    }
                } else {
                    //we have entered focus: if the number of existing rows in the activity is
                    //more than current rowNum, don't do anything.
                    if (numRows.equals(currentRowNum)) {
                        //else, create a new row (INFLATE activity)
                        addRow(currentRowNum + 1);
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
                } else {
                    //we have entered focus: if the number of existing rows in the activity is
                    //more than current rowNum, don't do anything.
                    if (numRows.equals(currentRowNum)) {
                        //else, create a new row (INFLATE activity)
                        addRow(currentRowNum + 1);
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
        FeedReaderDBHelper mDBHelper = new FeedReaderDBHelper(getApplicationContext());
        db = mDBHelper.getWritableDatabase();
        ContentValues courseValues = new ContentValues();
        //add the course "COURSE NAME" and "DESIRED GRADE" to the course table
        courseValues.put(FeedReaderDBHelper.COURSE_TABLE_NAME_COLUMN, course.getName());
        courseValues.put(FeedReaderDBHelper.COURSE_TABLE_DESIRED_GRADE_COLUMN, course.getDesiredGrade());
        db.insert(
                FeedReaderDBHelper.COURSE_TABLE,
                null,
                courseValues);
    }

    public void addAssessmentsToDB() {
        //add the assessments to the ass table
        for (Map.Entry<Integer, Assessment> aEntry : course.getAssessments().entrySet()) {
            ContentValues assValues = new ContentValues();
            Assessment a = aEntry.getValue();
            assValues.put(FeedReaderDBHelper.ASS_TABLE_TYPE_COLUMN, a.getType());
            assValues.put(FeedReaderDBHelper.ASS_TABLE_MARK_COLUMN, a.getMark());
            assValues.put(FeedReaderDBHelper.ASS_TABLE_MARKED_COLUMN, a.isMarked());
            assValues.put(FeedReaderDBHelper.ASS_TABLE_WORTH_COLUMN, a.getWorth());
            db.insert(
                    FeedReaderDBHelper.ASS_TABLE,
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
        addCourseToDB();
        addAssessmentsToDB();
    }
}
