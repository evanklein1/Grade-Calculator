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

    //create an object of SingleObject
    private static Student instance = new Student();

    //make the constructor private so that this class cannot be
    //instantiated
    private Student(){
        courses = new ArrayList<Course>();
    }

    //Get the only object available
    public static Student getInstance(){
        return instance;
    }
    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }

    public void addCourse(Course course) {
        courses.add(course);
    }
    public void removeCourse(Course course) {
        courses.remove(course);
    }
    public double getDesiredAverage() {
        return desiredAverage;
    }

    public void setDesiredAverage(double desiredAverage) {
        this.desiredAverage = desiredAverage;
    }
}
