package me.evanklein.finalexamcalculator;

/**
 * Created by Evan on 31/10/2015.
 */
public class Assessment {
    private boolean marked;
    private Double mark;
    private Double worth;
    private String type;

    public boolean isEmpty() {
        return (worth.equals(0.0)) && (type.equals("")) && (mark.equals(0.0));
    }
    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public Double getMark() {
        return mark;
    }

    public void setMark(Double mark) {
        this.mark = mark;
    }

    public Double getWorth() {
        return worth;
    }

    public void setWorth(Double worth) {
        this.worth = worth;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Assessment(String type, Double yourMark, boolean marked, Double worth) {
        this.setMark(yourMark);
        this.setMarked(marked);
        this.setType(type);
        this.setWorth(worth);
    }
}
