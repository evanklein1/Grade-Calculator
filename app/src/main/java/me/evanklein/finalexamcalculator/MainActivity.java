package me.evanklein.finalexamcalculator;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.Layout;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private HashMap<String, EditText> editTextMap;
    private Integer numRows = 0;
    Assessment a1;
    Course course;
    TableLayout tableLayout;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        course.addAssessment(1);
        a1 = course.getAssessment(1);
        final EditText type1EditText = (EditText) findViewById(R.id.type_1);
        final EditText yourMark1EditText = (EditText) findViewById(R.id.your_mark_1);
        final EditText worth1EditText = (EditText) findViewById(R.id.worth_1);
        setTypeListener(type1EditText, 1);
//        type1EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                //if we just lost focus
//                if (!hasFocus) {
//                    //change the assessment object
//                    a1.setType(type1EditText.getText().toString());
//                    //validate their input
//                }
//                else {
//                    //we have entered focus: if the number of existing rows in the activity is
//                    //more than current rowNum, don't do anything.
//                    if (numRows.equals(1)) {
//                        //else, create a new row (INFLATE activity)
//                        addRow(2);
//                        numRows += 1;
//                    }
//                    removeExtraRow(1, tableLayout);
//                }
//                updateTotals();
//            }
//        });

        yourMark1EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //if we just exited the type field
                if (!hasFocus) {
                    //change the assessment object
                    if (!("".equals(yourMark1EditText.getText().toString()))) {
                        a1.setMark(Double.valueOf(yourMark1EditText.getText().toString()));
                        a1.setMarked(true);
                    }
                    else {
                        //not marked
                        a1.setMarked(false);
                    }
                }
                else {
                    //we have entered focus: if the number of existing rows in the activity is
                    //more than current rowNum, don't do anything.
                    if (numRows.equals(1)) {
                        //else, create a new row (INFLATE activity)
                        addRow(2);
                        numRows += 1;
                    }
                    removeExtraRow(1, tableLayout);
                }
                updateTotals();
            }
        });
        worth1EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //if we just lost focus
                if (!hasFocus) {
                    //change the assessment object
                    if (!("".equals(worth1EditText.getText().toString()))) {

                        a1.setWorth(Double.valueOf(worth1EditText.getText().toString()));
                    }
                }
                else {
                    //we have entered focus: if the number of existing rows in the activity is
                    //more than current rowNum, don't do anything.
                    if (numRows.equals(1)) {
                        //else, create a new row (INFLATE activity)
                        addRow(2);
                        numRows += 1;
                    }
                    removeExtraRow(1, tableLayout);
                }
                updateTotals();
            }
        });

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
//        newType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                //if we just exited the type field
//                if (!hasFocus) {
//                    //change the assessment objec
//                        course.addAssessment(currentRowNum).setType(newType.getText().toString());
//                } else {
//                    //we have entered focus: if the number of existing rows in the activity is
//                    //more than current rowNum, don't do anything.
//                    if (numRows.equals(currentRowNum)) {
//                        //else, create a new row (INFLATE activity)
//                        addRow(currentRowNum + 1);
//                        numRows += 1;
//                    }
//                    removeExtraRow(currentRowNum, tableLayout);
//                }
//
//                updateTotals();
//            }
//        });
        setMarkListener(newYourMark, currentRowNum);
//        newYourMark.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                //if we just exited the type field
//                if (!hasFocus) {
//                    Assessment currentAss = course.addAssessment(currentRowNum);
//                    //change the assessment object
//                    if (!("".equals(newYourMark.getText().toString()))) {
//                        currentAss.setMark(Double.valueOf(newYourMark.getText().toString()));
//                        currentAss.setMarked(true);
//                    }
//                    else {
//                        //not marked
//                        currentAss.setMarked(false);
//                    }
//                } else {
//                    //we have entered focus: if the number of existing rows in the activity is
//                    //more than current rowNum, don't do anything.
//                    if (numRows.equals(currentRowNum)) {
//                        //else, create a new row (INFLATE activity)
//                        addRow(currentRowNum + 1);
//                        numRows += 1;
//                    }
//                    removeExtraRow(currentRowNum, tableLayout);
//                }
//                updateTotals();
//            }
//        });
        newWorth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //if we just exited the type field
                if (!hasFocus) {
                    //change the assessment object
                    //check if assessment is empty
                    if (!("".equals(newWorth.getText().toString()))) {
                        course.addAssessment(currentRowNum).setWorth(Double.valueOf(newWorth.getText().toString()));
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

//    public void createEditWorthListener(final View view) {
//        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//        @Override
//        public void onFocusChange(View v, boolean hasFocus) {
//            //if we just exited the type field
//            if (!hasFocus) {
//                //change the assessment object
//                //check if assessment is empty
//                if (!("".equals(view.getText()))) {
//                    course.getAssessment(currentRowNum).setWorth(Double.valueOf(view.getText().toString()).doubleValue());
//                }
//            } else {
//                //we have entered focus: if the number of existing rows in the activity is
//                //more than current rowNum, don't do anything.
//                if (numRows.equals(currentRowNum)) {
//                    //else, create a new row (INFLATE activity)
//                    addRow(currentRowNum + 1);
//                    numRows += 1;
//                }
//            }
//            //either way, want to update the mark so far (or just make sure it's up to date
//            //and the worth so far
//            updateTotals();
//        }
//    });
//    }
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
}
