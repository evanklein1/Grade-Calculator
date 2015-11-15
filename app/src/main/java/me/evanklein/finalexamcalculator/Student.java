package me.evanklein.finalexamcalculator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Evan on 14-Nov-2015.
 */
public class Student {
    private ArrayList<Course> courses;
    private double desiredAverage;

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }

    public double getDesiredAverage() {
        return desiredAverage;
    }

    public void setDesiredAverage(double desiredAverage) {
        this.desiredAverage = desiredAverage;
    }
}
