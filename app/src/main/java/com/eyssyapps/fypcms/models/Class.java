package com.eyssyapps.fypcms.models;

import com.eyssyapps.fypcms.utils.Constants;

/**
 * Created by eyssy on 06/04/2016.
 */
public class Class
{
    private int id,
        enrollmentStage;

    private String name,
        yearCommenced;

    private Semester semester;

    public Class(int id, int enrollmentStage, String name, String yearCommenced, Semester semester)
    {
        this.id = id;
        this.enrollmentStage = enrollmentStage;
        this.name = name;
        this.yearCommenced = yearCommenced;
        this.semester = semester;
    }

    public int getId()
    {
        return id;
    }

    public String getEnrollmentStage()
    {
        return Constants.ENROLLMENT_STAGES.get(enrollmentStage);
    }

    public String getName()
    {
        return name;
    }

    public String getYearCommenced()
    {
        return yearCommenced;
    }

    public Semester getSemester()
    {
        return semester;
    }
}
