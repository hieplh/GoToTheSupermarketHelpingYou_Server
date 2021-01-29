package com.smhu.overall;

import java.util.List;

public class Overall {

    private SystemAmount year;
    private List<SystemAmount> term;
    private List<SystemAmount> month;

    public Overall() {
    }

    public Overall(SystemAmount year, List<SystemAmount> term, List<SystemAmount> month) {
        this.year = year;
        this.term = term;
        this.month = month;
    }

    public SystemAmount getYear() {
        return year;
    }

    public void setYear(SystemAmount year) {
        this.year = year;
    }

    public List<SystemAmount> getTerm() {
        return term;
    }

    public void setTerm(List<SystemAmount> term) {
        this.term = term;
    }

    public List<SystemAmount> getMonth() {
        return month;
    }

    public void setMonth(List<SystemAmount> month) {
        this.month = month;
    }

    @Override
    public String toString() {
        return "Overall{" + "year=" + year + ", term=" + term + ", month=" + month + '}';
    }

}
