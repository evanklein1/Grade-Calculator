package me.evanklein.finalexamcalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    Course course;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    final static String COURSE_NAME = "courseName";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
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
}
