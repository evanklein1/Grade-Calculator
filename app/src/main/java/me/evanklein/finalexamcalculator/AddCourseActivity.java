package me.evanklein.finalexamcalculator;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class AddCourseActivity extends AppCompatActivity {

    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<String> coursesList;

    // url to get all products list
    private static String url_get_courses = "http://192.168.6.1/android_connect/get_courses.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_COURSES = "courses";
    private static final String TAG_COURSE = "course";

    // products JSONArray
    JSONArray courses = null;
    private AutoCompleteTextView courseTV;
    private ArrayAdapter<String> coursesAdapter;
    private Student student;
    private EditText schoolET;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        //set the title to be "Add a Course"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add a Course");
        }
        student = Student.getInstance();
        courseTV = (AutoCompleteTextView) findViewById(R.id.addCourseName);
        courseTV.setThreshold(0);
        courseTV.setAdapter(coursesAdapter);

        schoolET = (EditText) findViewById(R.id.school);
        setSchoolListener();

    }

    public void setSchoolListener() {
        schoolET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //if we just exited the school field
                if (!hasFocus) {
                    //store the school
                    String school = schoolET.getText().toString();
                    if (!school.equals("")) {
                        student.setSchool(school);
                        new LoadAllCourses().execute();
                    }
                } else {
                    //maybe we want to display a list of school suggestions

                }
            }
        });
    }
    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    private class LoadAllCourses extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddCourseActivity.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            //connect to url
            //make a get request with certain parameters (the name of the school)
            //parse the data we get back
            //store it in an array
            String query = null;
            try {
                query = String.format("school=%s", URLEncoder.encode(student.getSchool(), JSONParser.charset));
                String url = url_get_courses + "?" + query;
                JSONObject json = jParser.makeHttpRequest(url);
                Log.d("All Products: ", json.toString());

                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    courses = json.getJSONArray(TAG_COURSES);

                    // looping through All courses
                    for (int i = 0; i < courses.length(); i++) {
                        JSONObject c = courses.getJSONObject(i);

                        // Storing each json item in variable
                        String courseName = c.getString(TAG_COURSE);

                        //add course to arraylist
                        coursesList.add(courseName);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // Check your log cat for JSON reponse

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    //first add the home button
                    String[] courseNames = coursesList.toArray(new String[coursesList.size()]);
                    coursesAdapter = new ArrayAdapter<String>(AddCourseActivity.this, R.layout.addcourse_dropdown_item, courseNames);
                    courseTV.setAdapter(coursesAdapter);
                }
            });

        }

    }

//    public void makeGetRequest() {
//        try {
//            String query = String.format("school=%s",
//                    URLEncoder.encode(school, StandardCharsets.UTF_8.name()));
//            URL myURL = new URL(url_get_courses + "?" + query);
//            HttpURLConnection con = (HttpURLConnection) myURL.openConnection();
//            con.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
//
//            InputStream response = con.getInputStream();
//
//            BufferedReader in = new BufferedReader(
//                    new InputStreamReader(response));
//
//        }
//        catch (MalformedURLException e) {
//            // new URL() failed
//            // ...
//        }
//        catch (IOException e) {
//            // openConnection() failed
//            // ...
//        }
//    }
}
