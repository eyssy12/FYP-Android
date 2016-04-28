package com.eyssyapps.fypcms.models;

/**
 * Created by eyssy on 04/04/2016.
 */
public class Student
{
    private int studentId;
    private String enrollmentDate;

    public Student(int studentId, String enrollmentDate)
    {
        this.studentId = studentId;
        this.enrollmentDate = enrollmentDate;
    }

    public String getEnrollmentDate()
    {
        return enrollmentDate;
    }

    public int getStudentId()
    {
        return studentId;
    }
}
