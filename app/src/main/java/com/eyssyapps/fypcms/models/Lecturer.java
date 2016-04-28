package com.eyssyapps.fypcms.models;

/**
 * Created by eyssy on 15/04/2016.
 */
public class Lecturer
{
    private int lecturerId;

    private String hireDate;

    public Lecturer(int lecturerId, String hireDate)
    {
        this.lecturerId = lecturerId;
        this.hireDate = hireDate;
    }

    public int getLecturerId()
    {
        return lecturerId;
    }

    public String getHireDate()
    {
        return hireDate;
    }
}