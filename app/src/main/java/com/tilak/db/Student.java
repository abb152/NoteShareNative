package com.tilak.db;

import com.orm.SugarRecord;

/**
 * Created by Jay on 08-10-2015.
 */
public class Student extends SugarRecord{

    public String name;
    public int rollno;

    public Student() {
        super();
    }

    public Student(String name, int rollno) {
        super();
        this.name = name;
        this.rollno = rollno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRollno() {
        return rollno;
    }

    public void setRollno(int rollno) {
        this.rollno = rollno;
    }
}
