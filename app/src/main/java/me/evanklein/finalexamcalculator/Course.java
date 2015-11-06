package me.evanklein.finalexamcalculator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Evan on 01/11/2015.
 */
public class Course {
    private String name;
    private HashMap<Integer, Assessment> assessments;
    private Double desiredGrade;


    public Double getDesiredGrade() {
        return desiredGrade;
    }

    public void setDesiredGrade(Double desiredGrade) {
        this.desiredGrade = desiredGrade;
    }

    /*
        Returns your current mark, as a percentage, in the course based on what has been graded so far.
     */
    public Double getMarkSoFar() {
        Double mark = 0.0;
        for (Map.Entry<Integer, Assessment> aEntry : assessments.entrySet()) {
            if (aEntry.getValue().isMarked()) {
                mark += aEntry.getValue().getMark() * aEntry.getValue().getWorth() / 100;
            }
        }
        Double markedWorth = getMarkedWorth();
        if (markedWorth.equals(0.0)) {
            return 0.0;
        }
        else {
            return mark / markedWorth * 100;
        }
    }

    public Double getMarkedWorth() {
        Double worth = 0.0;
        for (Map.Entry<Integer, Assessment> aEntry : assessments.entrySet()) {
            if (aEntry.getValue().isMarked()) {
                worth += aEntry.getValue().getWorth();
            }
        }
        return worth;
    }

    public HashMap<Integer, Assessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(HashMap<Integer, Assessment> assessments) {
        this.assessments = assessments;
    }

    public Double getTotalWorth() {
        Double worth = 0.0;
        for (Map.Entry<Integer, Assessment> aEntry : assessments.entrySet()) {
            worth += aEntry.getValue().getWorth();
        }
        return worth;
    }
    /*
    Checks if a mapping for this rowNum exists: if it does, return it.
    If not, add a mapping for it to a new blank assessment, and return that assessment.
     */
    public Assessment addAssessment(Integer rowNum) {
        Assessment a = assessments.get(rowNum);
        if (a == null) {
            //create an assessment, put it in the map
            a = new Assessment("", 0.0, false, 0.0);
            assessments.put(rowNum, a);
        }
        return a;
    }
    public Assessment getAssessment(Integer rowNum) {
        return assessments.get(rowNum);
    }

    public void removeAssessment(Integer rowNum) {
        assessments.remove(rowNum);
    }

    public Double getRequiredRestMark() {
        Double yetToMark;
        Double totalWorth = getTotalWorth();
        Double markedWorth = getMarkedWorth();
        if (totalWorth.equals(markedWorth)) {
            yetToMark = 100-markedWorth;
        }
        else {
            yetToMark = totalWorth - markedWorth;
        }
        return ((desiredGrade - (getMarkSoFar() * markedWorth / 100))/yetToMark) * 100;
    }

    public Course() {
        assessments = new HashMap<Integer, Assessment>();
    }
}